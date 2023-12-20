package ru.redw4y.HomeAccounting.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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

import jakarta.persistence.EntityManager;
import ru.redw4y.HomeAccounting.models.IncomeCategory;
import ru.redw4y.HomeAccounting.models.OutcomeCategory;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.UserRepository;
import ru.redw4y.HomeAccounting.util.Category;
import ru.redw4y.HomeAccounting.util.OperationType;
@ExtendWith(MockitoExtension.class)
class CategoriesServiceTest {
	@InjectMocks
	private CategoriesService service;
	@Mock
	private UserRepository userRepo;
	@Mock
	private EntityManager entityManager;
	
	private List<IncomeCategory> incomeList;
	private List<OutcomeCategory> outcomeList;
	private User user;
	@BeforeEach
	void setUp() {
		outcomeList = new ArrayList<>();
		incomeList = new ArrayList<>();
		user = new User.Builder().id(0).incomeCategories(incomeList).outcomeCategories(outcomeList).build();
		incomeList.addAll(List.of(
				new IncomeCategory.Builder().id(2).name("IncomeCategory2").user(user).build(),
				new IncomeCategory.Builder().id(1).name("IncomeCategory1").user(user).build(),
				new IncomeCategory.Builder().id(0).name("IncomeCategory0").user(user).build()
				));
		outcomeList.addAll(List.of(
				new OutcomeCategory.Builder().id(2).name("OutcomeCategory2").user(user).build(),
				new OutcomeCategory.Builder().id(1).name("OutcomeCategory1").user(user).build(),
				new OutcomeCategory.Builder().id(0).name("OutcomeCategory0").user(user).build()
				));
	}
	@Test
	void findAllByUser_OutcomeCategories() {
		when(userRepo.findById(0)).thenReturn(Optional.of(user));
		List<? extends Category> actual = service.findAllByUser(0, OperationType.OUTCOME);
		assertNotEquals(outcomeList, actual);
		assertEquals(outcomeList.stream().sorted(Comparator.comparing(c -> c.getId())).toList(), actual);
	}
	@Test
	void findAllByUser_IncomeCategories() {
		when(userRepo.findById(0)).thenReturn(Optional.of(user));
		List<? extends Category> actual = service.findAllByUser(0, OperationType.INCOME);
		assertNotEquals(incomeList, actual);
		assertEquals(incomeList.stream().sorted(Comparator.comparing(c -> c.getId())).toList(), actual);
	}
	@Test
	void findAllByUser_NullUser() {
		when(userRepo.findById(0)).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> service.findAllByUser(0, OperationType.INCOME));
	}
	@Test 
	void findAllByUser_NonCorrectType() {
		when(userRepo.findById(0)).thenReturn(Optional.of(user));
		assertEquals(Collections.emptyList(), service.findAllByUser(0, null));
	}
	@Test
	void create_CorrectArgs_OutcomeCategory() {
		when(userRepo.findById(0)).thenReturn(Optional.of(user));
		OutcomeCategory outcome = new OutcomeCategory.Builder().id(3).name("New Outcome").build();
		service.create(0, outcome);
		assertTrue(user.getOutcomeCategories().contains(outcome));
		assertTrue(outcome.getUser().equals(user));
	}
	@Test
	void create_CorrectArgs_IncomeCategory() {
		when(userRepo.findById(0)).thenReturn(Optional.of(user));
		IncomeCategory income = new IncomeCategory.Builder().id(3).name("NewIncome").build();
		service.create(0, income);
		assertTrue(user.getIncomeCategories().contains(income));
		assertTrue(income.getUser().equals(user));
	}
	@Test
	void create_NullUser_OutcomeCategory() {
		when(userRepo.findById(0)).thenReturn(Optional.empty());
		OutcomeCategory outcome = new OutcomeCategory.Builder().id(3).name("New Outcome").build();
		assertThrows(NoSuchElementException.class, () -> service.create(0, outcome));
	}
	@Test
	void create_NullUser_IncomeCategory() {
		when(userRepo.findById(0)).thenReturn(Optional.empty());
		IncomeCategory income = new IncomeCategory.Builder().id(3).name("New Income").build();
		assertThrows(NoSuchElementException.class, () -> service.create(0, income));
	}

	@Test
	void remove_CorrectArgs_OutcomeCategory() {
		when(userRepo.findById(0)).thenReturn(Optional.of(user));
		OutcomeCategory outcome = outcomeList.get(0);
		when(entityManager.find(OutcomeCategory.class, 0)).thenReturn(outcome);
		service.remove(0, 0, OperationType.OUTCOME);
		assertFalse(user.getOutcomeCategories().contains(outcome));
		assertNull(outcome.getUser());
	}
	@Test
	void remove_CorrectArgs_IncomeCategory() {
		when(userRepo.findById(0)).thenReturn(Optional.of(user));
		IncomeCategory income = incomeList.get(0);
		when(entityManager.find(IncomeCategory.class, 0)).thenReturn(income);
		service.remove(0, 0, OperationType.INCOME);
		assertFalse(user.getIncomeCategories().contains(income));
		assertNull(income.getUser());
	}
	@Test
	void remove_NullUser_OutcomeCategory() {
		when(userRepo.findById(0)).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> service.remove(0, 0, OperationType.OUTCOME));
	}
	@Test
	void remove_NullUser_IncomeCategory() {
		when(userRepo.findById(0)).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> service.remove(0, 0, OperationType.INCOME));
	}
}
