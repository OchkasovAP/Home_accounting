package ru.redw4y.HomeAccounting.controllers;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import ru.redw4y.HomeAccounting.dto.AccountDTO;
import ru.redw4y.HomeAccounting.dto.CategoryDTO;
import ru.redw4y.HomeAccounting.dto.OperationDTO;
import ru.redw4y.HomeAccounting.dto.OperationFilterDTO;
import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.CashAccountsService;
import ru.redw4y.HomeAccounting.services.CategoriesService;
import ru.redw4y.HomeAccounting.services.OperationsService;
import ru.redw4y.HomeAccounting.util.DateRange;
import ru.redw4y.HomeAccounting.util.Operation;
import ru.redw4y.HomeAccounting.util.OperationFilter;
import ru.redw4y.HomeAccounting.util.OperationType;
import ru.redw4y.HomeAccounting.util.exceptions.HomeAccountingException;
import ru.redw4y.HomeAccounting.util.exceptions.OperationNotValidException;
import ru.redw4y.HomeAccounting.util.validators.OperationValidator;

@Controller
@RequestMapping("/operations")
public class OperationsController extends AbstractHomeAccountingController{
	
	private final OperationsService operationsService;
	private final CashAccountsService accountsService;
	private final CategoriesService categoriesService;
	private final OperationValidator validator;
	private final ModelMapper modelMapper;
	
	@Autowired
	public OperationsController(OperationsService operationsService, OperationValidator validator, ModelMapper modelMapper,
			CashAccountsService accountsService, CategoriesService categoriesService) {
		super();
		this.operationsService = operationsService;
		this.accountsService = accountsService;
		this.categoriesService = categoriesService;
		this.validator = validator;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/{type}")
	public String showOperationsList(@PathVariable("type") OperationType type, @ModelAttribute("filter") OperationFilterDTO filter,
			@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		model.addAttribute("operations", operationsService.findAll(convertFilterDTO(filter, type), userDetails.getUser().getId())
				.stream()
				.map(this::convertOperation)
				.toList());
		return "/operations/showOperations";
	}
	
	@GetMapping("/{type}/new")
	public String createOperationForm(@PathVariable("type") OperationType type, @ModelAttribute("operation") OperationDTO operationDTO,
			@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		int userId = userDetails.getUser().getId();
		Map<String, Object> attributes = Map.of("type", type.name().toLowerCase(), 
				"cashAccounts", accountsService.findAllByUser(userId),
				"categories", categoriesService.findAllByUser(userId, type));		
		model.addAllAttributes(attributes);
		return "/operations/createOperation";
	}
	
	@GetMapping("/{type}/{id}")
	public String showOperation(@PathVariable("type") OperationType type, @PathVariable("id") int operationID,
			@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		int userId = userDetails.getUser().getId();
		try {
			Operation operation = operationsService.findById(userDetails.getUser().getId(), operationID, type.getOperationClass());
			Map<String, Object> attributes = Map.of("type", type.name().toLowerCase(),
					"cashAccounts", accountsService.findAllByUser(userId),
					"categories", categoriesService.findAllByUser(userId, type),
					"operation", convertOperation(operation));		
			model.addAllAttributes(attributes);
			return "/operations/editOperation";
		} catch (NoSuchElementException ex) {
			throw new HomeAccountingException("Операции с таким id не существует в базе данных");
		}
	}
	
	@PostMapping("/{type}")
	public String createOperation(@PathVariable("type") OperationType type, @ModelAttribute("operation") @Valid OperationDTO operationDTO,
			BindingResult bindingResult, @AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		validator.validate(operationDTO, bindingResult);
		if(bindingResult.hasErrors()) {
			int userId =userDetails.getUser().getId();
			model.addAllAttributes(Map.of("cashAccounts", accountsService.findAllByUser(userId),
					"categories", categoriesService.findAllByUser(userId, type),
					"type", type.name().toLowerCase()));
			return "/operations/createOperation";
		}
		operationsService.create(convertDTO(operationDTO, type), userDetails.getUser().getId());
		return "redirect:/main/"+type;
	}

	@PatchMapping("/{type}")
	public String editOperation(@ModelAttribute("operation") @Valid OperationDTO operationDTO, BindingResult bindingResult,
			 @PathVariable("type") OperationType type, @AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		validator.validate(operationDTO, bindingResult);
		if(bindingResult.hasErrors()) {
			int userId =userDetails.getUser().getId();
			model.addAllAttributes(Map.of("cashAccounts", accountsService.findAllByUser(userId),
					"categories", categoriesService.findAllByUser(userId, type),
					"type", type.name().toLowerCase()));
			return "/operations/editOperation";
		}
		Operation operation = convertDTO(operationDTO, type);
		operation.setUser(userDetails.getUser());
		operationsService.edit(operation);
		return "redirect:/operations/"+type.name().toLowerCase();
	}

	@DeleteMapping("/{type}/{id}")
	public String deleteOperation(@PathVariable("id") int operationID,
			@PathVariable("type") OperationType type, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		int userId = userDetails.getUser().getId();
		Class<Operation> operationClass = type.getOperationClass();
		operationsService.delete(userId, operationID, operationClass);
		return "redirect:/operations/"+type.name().toLowerCase();
	}

	private OperationDTO convertOperation(Operation operation) {
		OperationDTO operationDTO = modelMapper.map(operation, OperationDTO.class);
		operationDTO.setAccount(operation.getCashAccount().getName());
		operationDTO.setCategory(operation.getCategory().getName());
		return operationDTO;
	}
	private Operation convertDTO(OperationDTO operationDTO, OperationType type) {
		Class<Operation> operClass = type.getOperationClass();
		Operation operation = modelMapper.map(operationDTO, operClass);
		operation.setCashAccount(new CashAccount.Builder().name(operationDTO.getAccount()).build());
		operation.setCategory(type.newCategory(operationDTO.getCategory()));
		return operation;
	}
	private OperationFilter convertFilterDTO(OperationFilterDTO filterDTO, OperationType type) {
		OperationFilter filter = modelMapper.map(filterDTO, OperationFilter.class);
		filter.setType(type);
		return filter;
	}
}
