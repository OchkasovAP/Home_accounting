package ru.redw4y.HomeAccounting.services;

import java.util.Collections;
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
	
	public List<CashAccount> findAllByUser(int userId) {
		Optional<User> user = userRepository.findById(userId);
		if(user.isEmpty()) {
			return Collections.emptyList();
		}
		return user.get().getCashAccounts().stream().sorted().toList();
	}
	public CashAccount findById(int id) {
		return accountRepository.findById(id).get();
	}
	
	@Transactional
	public void edit(CashAccount cashAccount) {
		accountRepository.save(cashAccount);
	}
	@Transactional
	public void create(int userId, CashAccount cashAccount) {
		Optional<User> user = userRepository.findById(userId);
		if(user.isEmpty()) {
			return;
		}
		user.get().addCashAccount(cashAccount);
	}
	@Transactional
	public void remove(int id) {
		accountRepository.deleteById(id);
	}
	
} 
