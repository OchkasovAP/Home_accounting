package ru.redw4y.HomeAccounting.controllers;


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


import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.CashAccountsService;


@Controller
@RequestMapping("/cashAccounts")
public class CashAccountController {
	@Autowired
	private CashAccountsService accountService;

	@GetMapping()
	public String showCashAccounts(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		int userID = userDetails.getUser().getId();
		List<CashAccount> cashAccounts = accountService.findAllByUser(userID);
		model.addAttribute("cashAccounts", cashAccounts);
		model.addAttribute("generalBalance", accountService.getGeneralBalance(cashAccounts));
		return "cashAccounts/showCashAccounts";
	}
	@GetMapping("/{id}")
	public String cashAccountInfo(Model model, @PathVariable("id") int cashAccID) {
		model.addAttribute("cashAccount", accountService.findById(cashAccID));
		return "cashAccounts/infoCashAccount";
	}

	@GetMapping("/new")
	public String createCashAccount(@ModelAttribute("cashAccount") CashAccount cashAccount) {
		return "cashAccounts/createCashAccount";
	}

	@PostMapping()
	public String addNewCashAccount(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@ModelAttribute("cashAccount") CashAccount cashAccount) {
		accountService.create(userDetails.getUser().getId(), cashAccount);
		return "redirect:cashAccounts";
	}

	@DeleteMapping("/{id}")
	public String removeCashAccount(@PathVariable("id") int cashAccountID) {
		accountService.remove(cashAccountID);
		return "redirect:/cashAccounts";
	}

	@PatchMapping()
	public String editCashAccount(@ModelAttribute("cashAccount") CashAccount cashAccount) {
		accountService.edit(cashAccount);
		return "redirect:/cashAccounts";
	}
}
