package ru.redw4y.HomeAccounting.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ru.redw4y.HomeAccounting.models.Income;
import ru.redw4y.HomeAccounting.models.IncomeCategory;
import ru.redw4y.HomeAccounting.models.Outcome;
import ru.redw4y.HomeAccounting.models.OutcomeCategory;

import static ru.redw4y.HomeAccounting.util.OperationType.*;

class OperationTypeTest {

	@Test
	void newEmptyOperation() {
		assertEquals(Income.class, INCOME.newEmptyOperation().getClass());
		assertEquals(Outcome.class, OUTCOME.newEmptyOperation().getClass());
	}
	
	@Test
	void newCategory() {
		Category outcomeCategory = OUTCOME.newCategory("OUTCOME");
		Category incomeCategory = INCOME.newCategory("INCOME");
		assertEquals(OutcomeCategory.class, outcomeCategory.getClass());
		assertEquals(IncomeCategory.class, incomeCategory.getClass());
		assertEquals("OUTCOME", outcomeCategory.getName());
		assertEquals("INCOME", incomeCategory.getName());
	}

}
