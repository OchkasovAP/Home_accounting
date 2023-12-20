package ru.redw4y.HomeAccounting.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.EntityManager;
import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.Income;
import ru.redw4y.HomeAccounting.models.IncomeCategory;
import ru.redw4y.HomeAccounting.models.Outcome;
import ru.redw4y.HomeAccounting.models.OutcomeCategory;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.CashAccountRepository;
import ru.redw4y.HomeAccounting.repository.UserRepository;
import ru.redw4y.HomeAccounting.util.DateUtil;
import ru.redw4y.HomeAccounting.util.OperationModel;
import ru.redw4y.HomeAccounting.util.OperationType;

@ExtendWith(MockitoExtension.class)
class OperationsServiceTest {
	@InjectMocks
	private OperationsService service;
	@Mock
	private EntityManager entityManager;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CashAccountRepository accountRepository;
	
	private OperationModel model;
	private List<Income> incomes;
	private List<Outcome> outcomes;
	private User user;
	private CashAccount account;
	private OutcomeCategory outcomeCategory;
	private IncomeCategory incomeCategory;
	@BeforeEach
	void setUp() {
		account = new CashAccount.Builder().id(0).balance(new BigDecimal(300)).build();
		incomeCategory = new IncomeCategory.Builder().id(0).build();
		outcomeCategory = new OutcomeCategory.Builder().id(0).build();
		incomes = new ArrayList<>();
		outcomes = new ArrayList<>();
		user = new User.Builder()
				.id(0)
				.incomes(incomes)
				.outcomes(outcomes)
				.cashAccounts(new ArrayList<CashAccount>(List.of(account)))
				.incomeCategories(new ArrayList<IncomeCategory>(List.of(incomeCategory)))
				.outcomeCategories(new ArrayList<OutcomeCategory>(List.of(outcomeCategory)))
				.build();
		fillIncomes();
		fillOutcomes();
	}
	void fillIncomes() { 
		incomes.addAll(List.of(
				new Income.Builder().id(0).date(new Date()).amount(new BigDecimal(200))
				.cashAccount(account).category(incomeCategory).user(user).build(),
				new Income.Builder().id(1).date(new GregorianCalendar(2022,05,05).getTime()).amount(new BigDecimal(200))
				.cashAccount(account).category(incomeCategory).user(user).build(),
				new Income.Builder().id(2).date(new GregorianCalendar(2023,12,05).getTime()).amount(new BigDecimal(200))
				.cashAccount(account).category(incomeCategory).user(user).build()
				));
	}
	void fillOutcomes() { 
		outcomes.addAll(List.of(
				new Outcome.Builder().id(0).date(new Date()).amount(new BigDecimal(200))
				.cashAccount(account).category(incomeCategory).user(user).build(),
				new Outcome.Builder().id(1).date(new GregorianCalendar(2022,05,05).getTime()).amount(new BigDecimal(200))
				.cashAccount(account).category(incomeCategory).user(user).build(),
				new Outcome.Builder().id(2).date(new GregorianCalendar(2023,12,05).getTime()).amount(new BigDecimal(200))
				.cashAccount(account).category(incomeCategory).user(user).build()
				));
	}
	
	void createSetUp() {
		model = new OperationModel.Builder()
				.id(0)
				.userID(0)
				.type("outcome")
				.date("2023-08-20")
				.cashAccountID(0)
				.categoryID(0)
				.amount(200.0)
				.comment("Comment")
				.build();
	}
 	@Test
	void create_correctArgs_Outcome() {
 		createSetUp();
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		when(accountRepository.findById(0)).thenReturn(Optional.of(account));
		when(entityManager.find(OutcomeCategory.class, 0)).thenReturn(outcomeCategory);
		service.create(model);
		List<Outcome> userOutcomes = user.getOutcomes();
		Outcome lastOutcome = userOutcomes.get(userOutcomes.size()-1);
		assertEquals(outcomeCategory, lastOutcome.getCategory());
		assertEquals(account, lastOutcome.getCashAccount());
		assertEquals("Comment", lastOutcome.getComment());
		assertEquals(new GregorianCalendar(2023,07,20).getTime(), lastOutcome.getDate());
		assertEquals(100, account.getBalance().doubleValue());
		assertEquals(0, lastOutcome.getId());
		assertEquals(user, lastOutcome.getUser());
	}
 	@Test
	void create_correctArgs_Income() {
		createSetUp();
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		when(accountRepository.findById(0)).thenReturn(Optional.of(account));
		when(entityManager.find(IncomeCategory.class, 0)).thenReturn(incomeCategory);
		model.setType("income");
		service.create(model);
		List<Income> userIncomes = user.getIncomes();
		Income lastIncome = userIncomes.get(userIncomes.size()-1);
		assertEquals(incomeCategory, lastIncome.getCategory());
		assertEquals(account, lastIncome.getCashAccount());
		assertEquals("Comment", lastIncome.getComment());
		assertEquals(new GregorianCalendar(2023,07,20).getTime(), lastIncome.getDate());
		assertEquals(500, account.getBalance().doubleValue());
		assertEquals(0, lastIncome.getId());
		assertEquals(user, lastIncome.getUser());
	}
 	@Test
 	void create_nonCorrectType() {
 		createSetUp();
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		when(accountRepository.findById(0)).thenReturn(Optional.of(account));
 		when(entityManager.find(OutcomeCategory.class, 0)).thenReturn(outcomeCategory);
 		model.setType("gjfbdgkdfngkd");
		service.create(model);
		List<Outcome> userOutcomes = user.getOutcomes();
		Outcome lastOutcome = userOutcomes.get(userOutcomes.size()-1);
		assertEquals(outcomeCategory, lastOutcome.getCategory());
		assertEquals(account, lastOutcome.getCashAccount());
		assertEquals("Comment", lastOutcome.getComment());
		assertEquals(new GregorianCalendar(2023,07,20).getTime(), lastOutcome.getDate());
		assertEquals(100, account.getBalance().doubleValue());
		assertEquals(0, lastOutcome.getId());
		assertEquals(user, lastOutcome.getUser());
 	}
 	@Test
 	void create_nonCorrectDate() {
 		createSetUp();
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		when(accountRepository.findById(0)).thenReturn(Optional.of(account));
 		when(entityManager.find(OutcomeCategory.class, 0)).thenReturn(outcomeCategory);
 		model.setDate("43536-636");
		service.create(model);
		List<Outcome> userOutcomes = user.getOutcomes();
		Outcome lastOutcome = userOutcomes.get(userOutcomes.size()-1);
		assertEquals(outcomeCategory, lastOutcome.getCategory());
		assertEquals(account, lastOutcome.getCashAccount());
		assertEquals("Comment", lastOutcome.getComment());
		assertEquals(DateUtil.convertDateToString(new GregorianCalendar().getTime()),
				DateUtil.convertDateToString(lastOutcome.getDate()));
		assertEquals(100, account.getBalance().doubleValue());
		assertEquals(0, lastOutcome.getId());
		assertEquals(user, lastOutcome.getUser());
 	}
	@Test
 	void create_futureDate() {
		createSetUp();
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		when(accountRepository.findById(0)).thenReturn(Optional.of(account));
 		when(entityManager.find(OutcomeCategory.class, 0)).thenReturn(outcomeCategory);
 		model.setDate("2024-10-05");
		service.create(model);
		List<Outcome> userOutcomes = user.getOutcomes();
		Outcome lastOutcome = userOutcomes.get(userOutcomes.size()-1);
		assertEquals(outcomeCategory, lastOutcome.getCategory());
		assertEquals(account, lastOutcome.getCashAccount());
		assertEquals("Comment", lastOutcome.getComment());
		assertEquals(DateUtil.convertDateToString(new GregorianCalendar().getTime()),
				DateUtil.convertDateToString(lastOutcome.getDate()));
		assertEquals(100, account.getBalance().doubleValue());
		assertEquals(0, lastOutcome.getId());
		assertEquals(user, lastOutcome.getUser());
 	}
	@Test
	void create_NotFoundUser() {
		createSetUp();
		when(userRepository.findById(0)).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class,() -> service.create(model));
	}
	@Test 
	void create_NotFoundCategory() {
		createSetUp();
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		when(accountRepository.findById(0)).thenReturn(Optional.of(account));
		when(entityManager.find(OutcomeCategory.class, 0)).thenReturn(null);
		assertThrows(NoSuchElementException.class,() -> service.create(model));
	}
	@Test
	void create_NotFoundAccount() {
		createSetUp();
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		when(accountRepository.findById(0)).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class,() -> service.create(model));
	}
	@Test
	void delete_CorrectModel() {
		model = new OperationModel.Builder().id(0).type("outcome").build();
		Outcome outcome = outcomes.get(0);
		when(entityManager.find(Outcome.class, 0)).thenReturn(outcome);
		service.delete(model);
		assertNull(outcome.getUser());
		assertFalse(user.getOutcomes().contains(outcome));
	}
	@Test
	void delete_OperationNotFound() {
		model = new OperationModel.Builder().id(0).type("outcome").build();
		when(entityManager.find(Outcome.class, 0)).thenReturn(null);
		assertThrows(NullPointerException.class, ()-> service.delete(model));
	}
	@Test 
	void edit_correctArgs() {
		
	}

}
