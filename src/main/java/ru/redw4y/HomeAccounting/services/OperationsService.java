package ru.redw4y.HomeAccounting.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import ru.redw4y.HomeAccounting.entityUtil.DateRange;
import ru.redw4y.HomeAccounting.entityUtil.Operation;
import ru.redw4y.HomeAccounting.entityUtil.OperationModel;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;
import ru.redw4y.HomeAccounting.entityUtil.OperationsFilter;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.CashAccountRepository;
import ru.redw4y.HomeAccounting.repository.UserRepository;
import ru.redw4y.HomeAccounting.util.DateUtil;

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
	public void create(OperationModel model) {
		User user = userRepository.findById(model.getUserID()).get();
		OperationType type = OperationType.getTypeFromName(model.getType());
		Operation operation = type.newEmptyOperation();
		fillOperationFromModel(operation, model);
		user.addOperation(operation);
	}
	@Transactional
	public void delete(int userId, OperationModel model) {
		User user = userRepository.findById(userId).get();
		Class<? extends Operation> operClass = OperationType.getTypeFromName(model.getType()).getOperationClass();
		Operation operation = entityManager.find(operClass, model.getId());
		user.removeOperation(operation);
	}
	//TODO
	@Transactional
	public void edit(int editOperationID, OperationModel operationModel) {
		OperationType operationType = OperationType.getTypeFromName(operationModel.getType());
		Operation editOperation = entityManager.find(operationType.getOperationClass(), editOperationID);
		User user = userRepository.findById(editOperation.getUser().getId()).get();
		//TODO Надо как-то перенос средств сделать не заменяя операции
		user.removeOperation(editOperation);
		fillOperationFromModel(editOperation, operationModel);
		user.addOperation(editOperation);
	}
	@Transactional(readOnly = true)
	public Operation findById(int id, Class<? extends Operation> itemClass) {
		return entityManager.find(itemClass, id);
	}
	@Transactional(readOnly = true)
	public List<Operation> findAllByUserInPeriod(OperationsFilter filter) {
		User currentUser = userRepository.findById(filter.getUserID()).get();
		OperationType operationType = OperationType.getTypeFromName(filter.getType());
		return currentUser.getOperations(operationType)
				.stream()
				.filter(o -> operationInDateInterval(filter, o)
						&&operationIncludeCategory(filter, o)
						&&operationIncludeCashAccount(filter, o))
				.sorted((o1, o2) -> {
					int dateCompare = o1.getDate().compareTo(o2.getDate());
					if (dateCompare != 0) {
						return dateCompare;
					}
					return o1.getId() - o2.getId();
				})
				.toList();
	}

	private void fillOperationFromModel(Operation operation, OperationModel model) {
		Date date = DateUtil.convertStringToDate(model.getDate());
		operation.setDate(date);
		operation.setCashAccount(accountRepository.findById(model.getCashAccountID()).get());
		OperationType type = operation.getType();
		operation.setCategory(entityManager.find(type.getCategoryClass(), model.getCategoryID()));
		operation.setAmount(new BigDecimal(model.getAmount()));
		operation.setComment(model.getComment());
	}
	
	private boolean operationInDateInterval(OperationsFilter filter, Operation operation) {
		DateRange dateRange = filter.getDateRange();
		Date operationDate = operation.getDate();
		return operationDate.compareTo(dateRange.getStartDate()) >= 0
				&& operationDate.compareTo(dateRange.getEndDate()) <= 0;
	}
	private boolean operationIncludeCategory(OperationModel filter, Operation operation) {
		if (filter.getCategoryID() == null || filter.getCategoryID().equals(operation.getCategory().getId())) {
			return true;
		}
		return false;
	}

	private boolean operationIncludeCashAccount(OperationModel filter, Operation operation) {
		if (filter.getCashAccountID() == null || filter.getCashAccountID().equals(operation.getCashAccount().getId())) {
			return true;
		}
		return false;
	}
}
