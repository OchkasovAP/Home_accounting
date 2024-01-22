package ru.redw4y.HomeAccounting.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.redw4y.HomeAccounting.dto.MoneyTransferDTO;
import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.User;

@Service
@Transactional
public class MoneyTransferService {
	
	private final CashAccountsService accountsService;

	public MoneyTransferService(CashAccountsService accountsService) {
		super();
		this.accountsService = accountsService;
	}
	
	public void transferBetweenAccounts(MoneyTransferDTO transferInstance, User user) {
		CashAccount accountFrom = accountsService.findByNameAndUser(transferInstance.getAccountFrom(), user).get();
		CashAccount accountTo = accountsService.findByNameAndUser(transferInstance.getAccountTo(), user).get();
		accountFrom.setBalance(accountFrom.getBalance().subtract(transferInstance.getAmount()));
		accountTo.setBalance(accountTo.getBalance().add(transferInstance.getAmount()));
	}
}
