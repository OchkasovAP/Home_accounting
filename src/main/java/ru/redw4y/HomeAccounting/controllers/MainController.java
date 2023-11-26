package ru.redw4y.HomeAccounting.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import ru.redw4y.HomeAccounting.dao.CashAccountDAO;
import ru.redw4y.HomeAccounting.dao.OperationsDAO;
import ru.redw4y.HomeAccounting.dao.UserDAO;
import ru.redw4y.HomeAccounting.entity.CashAccount;
import ru.redw4y.HomeAccounting.entity.User;
import ru.redw4y.HomeAccounting.entityUtil.DateRange;
import ru.redw4y.HomeAccounting.entityUtil.MainViewModel;
import ru.redw4y.HomeAccounting.entityUtil.Operation;
import ru.redw4y.HomeAccounting.entityUtil.OperationModel;
import ru.redw4y.HomeAccounting.entityUtil.OperationsFilter;

@Controller
public class MainController {
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private OperationsDAO operationsDAO;
	@Autowired
	private CashAccountDAO accountDAO;
	@Autowired
	private ApplicationContext applicationContext;
	//TODO
	@GetMapping()
	public String mainOperationsView(HttpSession session, Model model,
			@ModelAttribute("viewModel") MainViewModel viewModel) {
		if (session.getAttribute("userID") == null) {
			return "redirect:/users/autorization";
		}
		int userID = (int)session.getAttribute("userID");
		User user = userDAO.getUser(userID);
		viewModel.setUserID(userID);
		OperationsFilter operationFilter = new OperationsFilter(viewModel);
		List<? extends Operation> operations = operationsDAO.getUsersOperationsInPeriod(operationFilter);
		viewModel.init(operations);
		if (viewModel.getCashAccountID() != null) {
			CashAccount cashAccount = accountDAO.getItem(viewModel.getCashAccountID(), CashAccount.class);
			viewModel.recalculateGeneralBalance(cashAccount);
		} else {
			viewModel.recalculateGeneralBalance(user);
			viewModel.setCashAccountName("Итого");
		}
		model.addAttribute("filter", operationFilter);
		model.addAttribute("cashAccounts", user.getCashAccounts());
		model.addAttribute("viewModel", viewModel);
		model.addAttribute("operations", operations);
		return "main/homepage"; 
	}

	@GetMapping("menu")
	public String showMenu(HttpSession session, Model model) {
		if (session.getAttribute("userID") == null) {
			return "redirect:/users/autorization";
		}
		model.addAttribute("user", userDAO.getUser((int) session.getAttribute("userID")));
		return "main/mainMenu"; 
	}

	@GetMapping("/exit")
	public String exit(HttpSession session, @ModelAttribute("user") User user) {
		session.setAttribute("userID", null);
		return "redirect:/users/autorization";
	}

}
