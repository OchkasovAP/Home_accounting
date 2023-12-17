package ru.redw4y.HomeAccounting.controllers;


import java.util.List;



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
import org.springframework.web.bind.annotation.SessionAttribute;

import ru.redw4y.HomeAccounting.dao.CashAccountDAO;
import ru.redw4y.HomeAccounting.models.CashAccount;


@Controller
@RequestMapping("/cashAccounts")
public class CashAccountController {
	@Autowired
	private CashAccountDAO cashAccDAO;

	@GetMapping()
	public String showCashAccounts(@SessionAttribute("userID") int userID, Model model) {
		List<CashAccount> cashAccounts = cashAccDAO.getCashAccounts(userID);
		model.addAttribute("cashAccounts", cashAccounts);
		model.addAttribute("generalBalance", CashAccount.getGeneralBalance(cashAccounts));
		return "cashAccounts/showCashAccounts";
	}
	@GetMapping("/{id}")
	public String cashAccountInfo(Model model, @PathVariable("id") int cashAccID) {
		model.addAttribute("cashAccount", cashAccDAO.getCashAccount(cashAccID));
		return "cashAccounts/infoCashAccount";
	}

	@GetMapping("/new")
	public String createCashAccount(@ModelAttribute("cashAccount") CashAccount cashAccount) {
		return "cashAccounts/createCashAccount";
	}

	@PostMapping()
	public String addNewCashAccount(@SessionAttribute("userID") int userID,
			@ModelAttribute("cashAccount") CashAccount cashAccount) {
		cashAccDAO.addACashAccout(userID, cashAccount);
		return "redirect:cashAccounts";
	}

	@DeleteMapping("/{id}")
	public String removeCashAccount(@PathVariable("id") int cashAccountID, @SessionAttribute("userID") int userID) {
		cashAccDAO.removeCashAccount(userID, cashAccountID);
		return "redirect:/cashAccounts";
	}

	@PatchMapping()
	public String editCashAccount(@ModelAttribute("cashAccount") CashAccount cashAccount) {
		cashAccDAO.editCashAccount(cashAccount);
		return "redirect:/cashAccounts";
	}
}
