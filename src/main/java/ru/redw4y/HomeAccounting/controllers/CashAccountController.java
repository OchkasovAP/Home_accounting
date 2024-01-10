package ru.redw4y.HomeAccounting.controllers;


import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ru.redw4y.HomeAccounting.dto.AccountDTO;
import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.CashAccountsService;
import ru.redw4y.HomeAccounting.util.exceptions.AccountNotValidException;
import ru.redw4y.HomeAccounting.util.exceptions.HomeAccountingException;
import ru.redw4y.HomeAccounting.util.validators.AccountValidator;


@Controller
@RequestMapping("/cashAccounts")
public class CashAccountController extends AbstractHomeAccountingController{

	private final CashAccountsService accountService;
	private final ModelMapper modelMapper;
	private final AccountValidator validator;
	
	@Autowired
	public CashAccountController(CashAccountsService accountService, ModelMapper modelMapper,
			AccountValidator validator) {
		super();
		this.accountService = accountService;
		this.modelMapper = modelMapper;
		this.validator = validator;
	}
	
	@GetMapping()
	public String showCashAccounts(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		int userID = userDetails.getUser().getId();
		List<CashAccount> cashAccounts = accountService.findAllByUser(userID);
		List<AccountDTO> accountDTOList = cashAccounts.stream().map(this::convertAccount).toList();
		BigDecimal generalBalance = accountService.getGeneralBalance(cashAccounts);
		model.addAllAttributes(Map.of("cashAccounts", accountDTOList, "generalBalance", generalBalance));
		return "/cashAccounts/showCashAccounts";
	}
	@GetMapping("/{id}")
	public String cashAccountInfo(@PathVariable("id") int cashAccID, @AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		try {
			model.addAttribute("cashAccount", convertAccount(accountService.findById(userDetails.getUser().getId(), cashAccID)));
			return "/cashAccounts/infoCashAccount";
		} catch (NoSuchElementException ex) {
			throw new HomeAccountingException("Счет с таким id не найден в базе данных");
		}
	}
	@GetMapping("/new")
	public String createAccountForm(@ModelAttribute("cashAccount") AccountDTO cashAccount) {
		return "/cashAccounts/createCashAccount";
	}
	
	@PostMapping()
	public String addNewCashAccount(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@ModelAttribute("cashAccount") @Valid AccountDTO accountDTO, BindingResult bindingResult) {
		CashAccount cashAccount = convertDTO(accountDTO);
		cashAccount.setUser(userDetails.getUser());
		validator.validate(cashAccount, bindingResult);
		if(bindingResult.hasErrors()) {
			return "/cashAccounts/createCashAccount";
		}
		accountService.create(userDetails.getUser().getId(), cashAccount);
		return "redirect:/cashAccounts";
	}

	@DeleteMapping("/{id}")
	public String removeCashAccount(@PathVariable("id") int cashAccountID, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		accountService.remove(userDetails.getUser().getId(), cashAccountID);
		return "redirect:/cashAccounts";
	}

	@PatchMapping()
	public String editCashAccount(@AuthenticationPrincipal UserDetailsImpl userDetails, @ModelAttribute("cashAccount") @Valid AccountDTO accountDTO, BindingResult bindingResult) {
		CashAccount cashAccount = convertDTO(accountDTO);
		validator.validate(cashAccount, bindingResult);
		if(bindingResult.hasErrors()) {
			return "/cashAccounts/infoCashAccount";
		}
		cashAccount.setUser(userDetails.getUser());
		accountService.edit(cashAccount);
		return "redirect:/cashAccounts";
	}
	private AccountDTO convertAccount(CashAccount account) {
		return modelMapper.map(account, AccountDTO.class);
	}
	private CashAccount convertDTO(AccountDTO account) {
		return modelMapper.map(account, CashAccount.class);
	}
}
