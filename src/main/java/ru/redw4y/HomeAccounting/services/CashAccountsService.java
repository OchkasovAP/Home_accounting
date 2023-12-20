package ru.redw4y.HomeAccounting.services;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.CashAccountRepository;
import ru.redw4y.HomeAccounting.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class CashAccountsService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CashAccountRepository accountRepository;
	
	public BigDecimal getGeneralBalance(List<CashAccount> cashAccounts) {
		BigDecimal generalBalance = new BigDecimal(0);
		for (CashAccount cashAccount : cashAccounts) {
			if (cashAccount.getContainInGenBalance()) {
				generalBalance = generalBalance.add(cashAccount.getBalance());
			}
		}
		return generalBalance;
	}
	
	public List<CashAccount> findAllByUser(int userId) {
		Optional<User> user = userRepository.findById(userId);
		if(user.isEmpty()) {
			return Collections.emptyList();
		}
		return user.get().getCashAccounts().stream().sorted(Comparator.comparing(a -> a.getId())).toList();
	}
	
	public CashAccount findById(int id) {
		return accountRepository.findById(id).get();
	}
	
	@Transactional
	public void edit(CashAccount cashAccount) {
		User user = accountRepository.findById(cashAccount.getId()).get().getUser();
		cashAccount.setUser(user);
		accountRepository.save(cashAccount);
	}
	@Transactional
	public void create(int userId, CashAccount cashAccount) {
		User user = userRepository.findById(userId).get();
		user.getCashAccounts().add(cashAccount);
		cashAccount.setUser(user);
	}
	@Transactional
	public void remove(int id) {
		accountRepository.deleteById(id);
	}
	
} 
