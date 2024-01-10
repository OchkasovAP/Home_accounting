package ru.redw4y.HomeAccounting.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import ru.redw4y.HomeAccounting.dto.MainViewDTO;
import ru.redw4y.HomeAccounting.dto.OperationDTO;
import ru.redw4y.HomeAccounting.dto.OperationFilter;
import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.HomePageService;
import ru.redw4y.HomeAccounting.util.Operation;
import ru.redw4y.HomeAccounting.util.OperationType;


@Controller
public class MainController {
	private final HomePageService homePageService;
	private final ModelMapper modelMapper;
	
	public MainController(HomePageService homePageService, ModelMapper modelMapper) {
		super();
		this.homePageService = homePageService;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/{type}")
	public String mainOperationsView(@PathVariable("type") String typeName, Model model,
			@ModelAttribute("filter") OperationFilter operationFilter, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		OperationDTO operationDTO = operationFilter.getFilter();
		OperationType type = OperationType.getTypeFromName(typeName);
		Class<Operation> operClass = type.getOperationClass();
		Operation operation = modelMapper.map(operationDTO, operClass);
		operation.setCashAccount(new CashAccount.Builder().name(operationDTO.getAccount()).build());
		operation.setCategory(type.newCategory(operationDTO.getCategory()));
		model.addAllAttributes(homePageService.mainPageAttributes(operation, operationFilter.getDateRange(), userDetails.getUser()));
		return "main/homepage"; 
	}

	@GetMapping("menu")
	public String showMenu(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
 		model.addAttribute("user", userDetails.getUser());
		return "main/mainMenu"; 
	}

}
