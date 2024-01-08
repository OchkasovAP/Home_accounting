package ru.redw4y.HomeAccounting.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import ru.redw4y.HomeAccounting.dto.OperationDTO;
import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.Income;
import ru.redw4y.HomeAccounting.models.Outcome;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.CashAccountRepository;
import ru.redw4y.HomeAccounting.repository.UserRepository;
import ru.redw4y.HomeAccounting.util.Category;
import ru.redw4y.HomeAccounting.util.DateRange;
import ru.redw4y.HomeAccounting.util.DateUtil;
import ru.redw4y.HomeAccounting.util.Operation;
import ru.redw4y.HomeAccounting.util.OperationType;

@Service
@Transactional(readOnly = true)
public class OperationsService {
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CashAccountRepository accountRepository;
	
	@Transactional
	public void create(OperationDTO model) {
		User user = userRepository.findById(model.getUserID()).get();
		OperationType type = OperationType.getTypeFromName(model.getType());
		Operation operation = type.newEmptyOperation();
		fillOperationFromModel(operation, model);
		if(OperationType.INCOME.equals(type)) {
			addToAccountBalance(operation);
			user.getIncomes().add((Income)operation);
		} else if(OperationType.OUTCOME.equals(type)) {
			subtractFromAccountBalance(operation);
			user.getOutcomes().add((Outcome)operation);
		}
		operation.setUser(user);
	}
	
	@Transactional
	public void delete(OperationDTO model) {
		Class<? extends Operation> operClass = OperationType.getTypeFromName(model.getType()).getOperationClass();
		Operation operation = entityManager.find(operClass, model.getId());
		User user = operation.getUser();
		if(OperationType.INCOME.equals(operation.getType())) {
			subtractFromAccountBalance(operation);
			user.getIncomes().remove(operation);
		} else if(OperationType.OUTCOME.equals(operation.getType())) {
			addToAccountBalance(operation);
			user.getOutcomes().remove(operation);
		}
		operation.setUser(null);
	}

	@Transactional
	public void edit(OperationDTO model) {
		OperationType operationType = OperationType.getTypeFromName(model.getType());
		Operation editOperation = entityManager.find(operationType.getOperationClass(), model.getId());
		if(OperationType.INCOME.equals(operationType)) {
			subtractFromAccountBalance(editOperation);
			fillOperationFromModel(editOperation, model);
			addToAccountBalance(editOperation);
		} else if(OperationType.OUTCOME.equals(operationType)) {
			addToAccountBalance(editOperation);
			fillOperationFromModel(editOperation, model);
			subtractFromAccountBalance(editOperation);
		}
	}
	
	@Transactional(readOnly = true)
	public Operation findById(int id, Class<? extends Operation> itemClass) {
		return Optional.ofNullable(entityManager.find(itemClass, id)).get();
	}
	
	@Transactional(readOnly = true)
	public List<? extends Operation> findAllByUserInPeriod(OperationDTO model, DateRange dateRange) {
		return findAllByUser(model)
				.stream()
				.filter(o -> operationInDateInterval(dateRange, o)
						&&operationIncludeCategory(model, o)
						&&operationIncludeCashAccount(model, o))
				.sorted((o1, o2) -> {
					int dateCompare = o1.getDate().compareTo(o2.getDate());
					if (dateCompare != 0) {
						return dateCompare;
					}
					return o1.getId() - o2.getId();
				})
				.toList();
	}
	
	private List<? extends Operation> findAllByUser(OperationDTO operation) {
		OperationType operationType = OperationType.getTypeFromName(operation.getType());
		User user = userRepository.findById(operation.getUserID()).get();
		if(OperationType.INCOME.equals(operationType)) {
			return user.getIncomes(); 
		} else if(OperationType.OUTCOME.equals(operationType)) {
			return user.getOutcomes();
		}
		throw new IllegalArgumentException("Non correct operation type");
	}
	private void subtractFromAccountBalance(Operation operation) {
		CashAccount account = operation.getCashAccount();
		BigDecimal accountBalance = account.getBalance();
		account.setBalance(accountBalance.subtract(operation.getAmount()));
	}
	private void addToAccountBalance(Operation operation) {
		CashAccount cashAccount = operation.getCashAccount();
		BigDecimal accountBalance = cashAccount.getBalance();
		cashAccount.setBalance(accountBalance.add(operation.getAmount()));
	}
	private void fillOperationFromModel(Operation operation, OperationDTO model) {
		Date date = DateUtil.convertStringToDate(model.getDate());
		operation.setDate(date);
		operation.setCashAccount(accountRepository.findById(model.getCashAccountID()).get());
		OperationType type = operation.getType();
		Optional<Category> category = Optional.ofNullable(entityManager.find(type.getCategoryClass(), model.getCategoryID()));
		operation.setCategory(category.get());
		operation.setAmount(new BigDecimal(model.getAmount()));
		operation.setComment(model.getComment());
	}
	
	private boolean operationInDateInterval(DateRange dateRange, Operation operation) {
		Date operationDate = operation.getDate();
		return operationDate.compareTo(dateRange.getStartDate()) >= 0
				&& operationDate.compareTo(dateRange.getEndDate()) <= 0;
	}
	private boolean operationIncludeCategory(OperationDTO operationDTO, Operation operation) {
		if (operationDTO.getCategoryID() == null || operationDTO.getCategoryID().equals(operation.getCategory().getId())) {
			return true;
		}
		return false;
	}

	private boolean operationIncludeCashAccount(OperationDTO operationDTO, Operation operation) {
		if (operationDTO.getCashAccountID() == null || operationDTO.getCashAccountID().equals(operation.getCashAccount().getId())) {
			return true;
		}
		return false;
	}
}
