package ru.redw4y.HomeAccounting.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import ru.redw4y.HomeAccounting.dao.CashAccountDAO;
import ru.redw4y.HomeAccounting.dao.OperationsDAO;
import ru.redw4y.HomeAccounting.dao.UserDAO;
import ru.redw4y.HomeAccounting.entityUtil.Operation;
import ru.redw4y.HomeAccounting.entityUtil.OperationModel;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;
import ru.redw4y.HomeAccounting.entityUtil.OperationsFilter;
import ru.redw4y.HomeAccounting.model.User;
import ru.redw4y.HomeAccounting.util.DateUtil;


@Controller
@RequestMapping("/operations")
public class OperationsController {
	@Autowired
	private OperationsDAO operationDAO;
	@Autowired
	private UserDAO userDAO;
	
	@GetMapping()
	public String showOperationsList(@SessionAttribute("userID") int userID, Model model, @ModelAttribute("filter") OperationsFilter filter) {
		List<? extends Operation> operations = operationDAO
				.getUsersOperationsInPeriod(filter);
		model.addAttribute("filter", filter);
		model.addAttribute("operations", operations);
		return "operations/showOperations";
	}

	@GetMapping("/new")
	public String addOperation(@SessionAttribute("userID") int userID, @RequestParam("type") String typeName, Model model) {
		OperationModel operationModel = new OperationModel();
		operationModel.setDate(DateUtil.convertDateToString(new Date()));
		operationModel.setType(typeName);
		model.addAttribute("operation", operationModel);
		model.addAttribute("user", userDAO.getFullUser(userID));
		return "operations/addOperation";
	}

	@GetMapping("/{type}/{id}")
	public String editOperationForm(@PathVariable("type") String typeName, @PathVariable("id") int operationID,
			Model model) {
		OperationType type = OperationType.getTypeFromName(typeName);
		Operation operation = operationDAO.getOperation(operationID, type.getOperationClass());
		OperationModel operationModel = new OperationModel(operation);
		User user = userDAO.getFullUser(operation.getUser().getId());
		model.addAttribute("user", user);
		model.addAttribute("operationID", operationID);
		model.addAttribute("operation", operationModel);
		return "operations/editOperation";
	}
	@PostMapping()
	public String postOperation(@ModelAttribute("operation") OperationModel operationInfo,
			@SessionAttribute("userID") int userID) {
		operationInfo.setUserID(userID);
		operationDAO.addOperation(operationInfo);
		return "redirect:/";
	}

	@PatchMapping("/{id}")
	public String editOperation(@ModelAttribute("operation") OperationModel operationInfo,
			@SessionAttribute("userID") int userID, @PathVariable("id") int operationID) {
		operationDAO.editOperation(operationID, operationInfo);
		return "redirect:/";
	}

	@DeleteMapping("/{type}/{id}")
	public String deleteOperation(@SessionAttribute("userID") int userID, @PathVariable("id") int operationID,
			@PathVariable("type") String typeName) {
		OperationType type = OperationType.getTypeFromName(typeName);
		operationDAO.removeOperation(userID, operationID, type.getOperationClass());
		return "redirect:/";
	}

}
