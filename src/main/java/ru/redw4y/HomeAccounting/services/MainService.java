package ru.redw4y.HomeAccounting.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.util.Category;
import ru.redw4y.HomeAccounting.util.DateRange;
import ru.redw4y.HomeAccounting.util.MainViewModel;
import ru.redw4y.HomeAccounting.util.Operation;
import ru.redw4y.HomeAccounting.util.OperationModel;

@Service
public class MainService {
	@Autowired
	private CashAccountsService accountService;
	@Autowired
	private OperationsService operationService;
	
	private MainViewModel model;
	private Map<Category, double[]> categoriesSum;
	
	public Map<String, Object> mainPageAttributes(MainViewModel viewModel, User user) {
		Map<String, Object> attributes = new HashMap<>();
		viewModel.setUserID(user.getId());
		List<? extends Operation> operations = findOperationsByViewModel(viewModel);
		List<CashAccount> cashAccounts = accountService.findAllByUser(user.getId());
		initViewModel(viewModel, operations);
		if (model.getCashAccountID() != null) {
			CashAccount cashAccount = accountService.findById(model.getCashAccountID());
			recalculateGeneralBalance(cashAccount);
		} else {
			viewModel.setGeneralBalance(accountService.getGeneralBalance(cashAccounts).doubleValue());
			viewModel.setCashAccountName("Итого");
		}
		attributes.put("filter", new OperationModel.Builder().userID(user.getId()).type(model.getType()).cashAccountID(model.getCashAccountID()).build());
		attributes.put("cashAccounts", cashAccounts);
		attributes.put("viewModel", model);
		attributes.put("operations", operations);
		return attributes;
	}
	
	private List<? extends Operation> findOperationsByViewModel(MainViewModel viewModel) {
		OperationModel operationModel = new OperationModel.Builder()
				.type(viewModel.getType())
				.userID(viewModel.getUserID())
				.cashAccountID(viewModel.getCashAccountID())
				.build();
		DateRange dateRange = new DateRange(viewModel.getStartDate(), viewModel.getEndDate());
		return operationService.findAllByUserInPeriod(operationModel, dateRange);
	}
	
	private void initViewModel(MainViewModel model, List<? extends Operation> operations) {
		this.model = model;
		categoriesSum = new HashMap<>();
		calculateAmoundCategories(operations);
		categoryViewFormatting();
		createLinkParameters();
	}
	
	private void recalculateGeneralBalance(CashAccount cashAccount) {
		model.setCashAccountName(cashAccount.getName());
		model.setCashAccountID(cashAccount.getId());
		model.setGeneralBalance(cashAccount.getBalance().doubleValue());
	}
	
	private void calculateAmoundCategories(List<? extends Operation> operations) {
		double amountInPeriod = model.getAmountInPeriod();
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
		model.setAmountInPeriod(amountInPeriod);
	}

	private void categoryViewFormatting() {
		Map<Integer, String> predentedCategories = new HashMap<>();
		for (Category category : categoriesSum.keySet()) {
			categoriesSum.get(category)[0]/=model.getAmountInPeriod();
			double percent = categoriesSum.get(category)[0];
			double categoryAmount = categoriesSum.get(category)[1];;
			String categotyDescript = String.format("%-20s|%15.2f\u0025%|%15.2f", category.getName(), percent*100,
					categoryAmount);
			predentedCategories.put(category.getId(), categotyDescript);
		}
		model.setPredentedCategories(predentedCategories);
	}
	private void createLinkParameters() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("startDate="+model.getStartDate());
		stringBuilder.append("&endDate="+model.getEndDate());
		stringBuilder.append("&userID="+model.getUserID());
		if(model.getCashAccountID()!=null) {
			stringBuilder.append("&cashAccountID="+model.getCashAccountID());
		}
		model.setLinkParameters(stringBuilder.toString());
	}

}
