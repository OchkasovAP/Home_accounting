package ru.redw4y.HomeAccounting.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.CashAccountRepository;
import ru.redw4y.HomeAccounting.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CashAccountsServiceTest {
	@InjectMocks
	private CashAccountsService service;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CashAccountRepository accountRepository;
	
	private List<CashAccount> cashAccounts;
	@BeforeEach
	void setUp() {
		cashAccounts = new ArrayList<>();
		cashAccounts.add(new CashAccount.Builder()
				.id(2)
				.name("CashAccount0")
				.balance(new BigDecimal(100))
				.containInGenBalance(true)
				.build());
		cashAccounts.add(new CashAccount.Builder()
				.id(1)
				.name("CashAccount1")
				.balance(new BigDecimal(100))
				.containInGenBalance(true)
				.build());
		cashAccounts.add(new CashAccount.Builder()
				.id(0)
				.name("CashAccount2")
				.balance(new BigDecimal(100))
				.containInGenBalance(false)
				.build());
	}
	@Test
	void getGeneralBalance() {
		BigDecimal expected = new BigDecimal(200);
		BigDecimal actual = service.getGeneralBalance(cashAccounts);
		assertEquals(expected, actual);
	}
	
	@Test
	void findAllByUser_PresentUser() {
		when(userRepository.findById(0)).thenReturn(Optional.of(new User.Builder().cashAccounts(cashAccounts).build()));
		List<CashAccount> actual = service.findAllByUser(0);
		assertNotEquals(cashAccounts, actual);
		assertEquals(cashAccounts.stream().sorted(Comparator.comparing(ca -> ca.getId())).toList(), actual);
	}
	
	@Test
	void findAllByUser_NullUser() {
		when(userRepository.findById(0)).thenReturn(Optional.empty());
		assertEquals(Collections.emptyList(), service.findAllByUser(0));
	}
	@Test
	void create_CorrectArguments() {
		User user = new User.Builder().cashAccounts(cashAccounts).build();
		CashAccount cashAccount = cashAccounts.get(0);
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		service.create(0, cashAccount);
		assertEquals(user, cashAccount.getUser());
		assertTrue(user.getCashAccounts().contains(cashAccount));
	}
	
	@Test
	void create_UserDoesntExist() {
		when(userRepository.findById(0)).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> service.create(0, null));
	}

	@Test
	void create_NullCashAccount() {
		when(userRepository.findById(0)).thenReturn(Optional.of(new User.Builder().cashAccounts(cashAccounts).build()));
		assertThrows(NullPointerException.class, () -> service.create(0, null));
	}
	

}
