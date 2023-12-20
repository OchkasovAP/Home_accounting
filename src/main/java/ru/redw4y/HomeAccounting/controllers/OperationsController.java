package ru.redw4y.HomeAccounting.controllers;

import java.util.Date;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.OperationsService;
import ru.redw4y.HomeAccounting.services.UserService;
import ru.redw4y.HomeAccounting.util.DateRange;
import ru.redw4y.HomeAccounting.util.DateUtil;
import ru.redw4y.HomeAccounting.util.Operation;
import ru.redw4y.HomeAccounting.util.OperationModel;
import ru.redw4y.HomeAccounting.util.OperationType;


@Controller
@RequestMapping("/operations")
public class OperationsController {
	@Autowired
	private OperationsService operationsService;
	@Autowired
	private UserService userService;
	
	@GetMapping()
	public String showOperationsList(Model model, @ModelAttribute("filter") OperationModel filter,
			@ModelAttribute("startDate") String startDate, @ModelAttribute("endDate") String endDate) {
		DateRange dateRange = new DateRange(startDate, endDate);
		List<? extends Operation> operations = operationsService.findAllByUserInPeriod(filter, dateRange);
		model.addAttribute("operations", operations);
		model.addAttribute("dateRange", dateRange);
		return "operations/showOperations";
	}

	@GetMapping("/new")
	public String addOperation(@AuthenticationPrincipal UserDetailsImpl userDetails, @ModelAttribute("type") String typeName, Model model) {
		OperationModel operationModel = new OperationModel();
		operationModel.setDate(DateUtil.convertDateToString(new Date()));
		operationModel.setType(typeName);
		model.addAttribute("operation", operationModel);
		model.addAttribute("user", userService.getFullUser(userDetails.getUser().getId()));
		return "operations/addOperation";
	}

	@GetMapping("/{type}/{id}")
	public String editOperationForm(@PathVariable("type") String typeName, @PathVariable("id") int operationID,
			Model model) {
		OperationType type = OperationType.getTypeFromName(typeName);
		Operation operation = operationsService.findById(operationID, type.getOperationClass());
		OperationModel operationModel = new OperationModel(operation);
		User user = userService.getFullUser(operation.getUser().getId());
		model.addAttribute("user", user);
		model.addAttribute("operationID", operationID);
		model.addAttribute("operation", operationModel);
		return "operations/editOperation";
	}
	@PostMapping()
	public String postOperation(@ModelAttribute("operation") OperationModel operationModel,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		operationModel.setUserID(userDetails.getUser().getId());
		operationsService.create(operationModel);
		return "redirect:/";
	}

	@PatchMapping("/{id}")
	public String editOperation(@ModelAttribute("operation") OperationModel operationModel,
			 @PathVariable("id") int operationID) {
		operationsService.edit(operationModel);
		return "redirect:/";
	}

	@DeleteMapping("/{type}/{id}")
	public String deleteOperation(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable("id") int operationID,
			@PathVariable("type") String typeName) {
		int userID = userDetails.getUser().getId();
		operationsService.delete(new OperationModel.Builder().id(operationID).userID(userID).type(typeName).build());
		return "redirect:/";
	}

}
