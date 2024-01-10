package ru.redw4y.HomeAccounting.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.redw4y.HomeAccounting.dto.DateRange;
import ru.redw4y.HomeAccounting.dto.MainViewDTO;
import ru.redw4y.HomeAccounting.dto.OperationDTO;
import ru.redw4y.HomeAccounting.dto.OperationFilter;
import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.util.Category;
import ru.redw4y.HomeAccounting.util.Operation;
import ru.redw4y.HomeAccounting.util.OperationType;

@Service
public class HomePageService {
	@Autowired
	private CashAccountsService accountService;
	@Autowired
	private OperationsService operationService;
	
	private Map<Category, double[]> categoriesSum;
	private Map<String, Object> modelAttributes;
	private double amountInPeriod;
	
	public Map<String, Object> mainPageAttributes(Operation filter, DateRange dateRange, User user) {
		modelAttributes = new HashMap<>();
		List<? extends Operation> operations = operationService.findAll(filter, dateRange);
		List<CashAccount> cashAccounts = accountService.findAllByUser(user.getId());
		modelAttributes.put("predentedCategories", getPredentedCategories(operations));
		if (filter.getCashAccount() != null) {
			CashAccount cashAccount = accountService.findByNameAndUser(filter.getCashAccount().getName(), user).get();
			recalculateGeneralBalance(cashAccount);
		} else {
			modelAttributes.put("generalBalance", accountService.getGeneralBalance(cashAccounts));
			modelAttributes.put("disabledAccount", "Итого");
		}
		modelAttributes.put("cashAccounts", cashAccounts);
		return modelAttributes;
	}
	
	private Map<String, String> getPredentedCategories(List<? extends Operation> operations) {
		calculateAmoundCategories(operations);
		Map<String, String> predentedCategories = new HashMap<>();
		categoryViewFormatting(predentedCategories);
		return predentedCategories;
	}
	
	private void recalculateGeneralBalance(CashAccount cashAccount) {
		modelAttributes.put("disabledAccount", cashAccount.getName());
		modelAttributes.put("generalBalance", cashAccount.getBalance());
	}
	
	private void calculateAmoundCategories(List<? extends Operation> operations) {
		categoriesSum = new HashMap<>();
		amountInPeriod = 0;
		for (Operation operation : operations) {
			double operationSum = operation.getAmount().doubleValue();
			amountInPeriod += operationSum;
			Category operationCategory = operation.getCategory();
			if (!categoriesSum.containsKey(operationCategory)) {
				categoriesSum.put(operationCategory, new double[2]);
			}
			categoriesSum.get(operationCategory)[0]+=operationSum;
			categoriesSum.get(operationCategory)[1]+=operationSum;
		}
	}

	private void categoryViewFormatting(Map<String, String> predentedCategories) {
		for (Category category : categoriesSum.keySet()) {
			categoriesSum.get(category)[0]/=amountInPeriod;
			double percent = categoriesSum.get(category)[0];
			double categoryAmount = categoriesSum.get(category)[1];;
			String categotyDescript = String.format("%-20s|%15.2f\u0025%|%15.2f", category.getName(), percent*100,
					categoryAmount);
			predentedCategories.put(category.getName(), categotyDescript);
		}
	}
}
