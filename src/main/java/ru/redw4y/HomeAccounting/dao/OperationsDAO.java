package ru.redw4y.HomeAccounting.dao;

import java.util.Date;
import java.util.LinkedList;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import ru.redw4y.HomeAccounting.entityUtil.DateRange;
import ru.redw4y.HomeAccounting.entityUtil.Operation;
import ru.redw4y.HomeAccounting.entityUtil.OperationModel;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;
import ru.redw4y.HomeAccounting.entityUtil.OperationsFilter;
import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.util.DateUtil;

@Component
public class OperationsDAO {
	@Autowired
	private EntityManager entityManager;
	@Transactional
	public void addOperation(OperationModel operationInfo) {
		Session session = entityManager.unwrap(Session.class);
		User user = session.find(User.class, operationInfo.getUserID());
		OperationType type = OperationType.getTypeFromName(operationInfo.getType());
		Operation operation = type.newEmptyOperation();
		fillOperationFromModel(operation, operationInfo);
		user.addOperation(operation);
	}
	@Transactional
	public void removeOperation(int userID, int operationID, Class<? extends Operation> operationClass) {
		Session session = entityManager.unwrap(Session.class);
		User user = session.find(User.class, userID);
		Operation operation = session.find(operationClass, operationID);
		user.removeOperation(operation);
	}
	@Transactional
	public void editOperation(int editOperationID, OperationModel operationInfo) {
		Session session = entityManager.unwrap(Session.class);
		OperationType operationType = OperationType.getTypeFromName(operationInfo.getType());
		Operation editOperation = session.find(operationType.getOperationClass(), editOperationID);
		User user = session.find(User.class, editOperation.getUser().getId());
		user.removeOperation(editOperation);
		fillOperationFromModel(editOperation, operationInfo);
		user.addOperation(editOperation);
	}
	@Transactional(readOnly = true)
	public Operation getOperation(int id, Class<? extends Operation> itemClass) {
		Session session = entityManager.unwrap(Session.class);
		return session.find(itemClass, id);
	}
	@Transactional(readOnly = true)
	public <T extends Operation> List<T> getUsersOperationsInPeriod(OperationsFilter filter) {
		Session session = entityManager.unwrap(Session.class);
		User currentUser = session.find(User.class, filter.getUserID());
		OperationType operationType = OperationType.getTypeFromName(filter.getType());
		List<T> operations = (List<T>) getFilteredOperations(currentUser.getOperations(operationType), filter);
		sortOperationList(operations);
		return operations;
	}

	private <T extends Operation> List<T> getFilteredOperations(List<T> allOperations, OperationsFilter filter) {
		DateRange dateRange = filter.getDateRange();
		List<T> listInDateInterval = new LinkedList<T>(allOperations);
		for (Operation operation : allOperations) {
			Date operationDate = operation.getDate();
			boolean operationInDateInterval = operationDate.compareTo(dateRange.getStartDate()) >= 0
					&& operationDate.compareTo(dateRange.getEndDate()) <= 0;
			if (!operationInDateInterval || !operationIncludeCategory(filter, operation)
					|| !operationIncludeCashAccount(filter, operation)) {
				listInDateInterval.remove(operation);
			}
		}
		return listInDateInterval;
	}

	private void fillOperationFromModel(Operation operation, OperationModel operationModel) {
		Session session = entityManager.unwrap(Session.class);
		Date date = DateUtil.convertStringToDate(operationModel.getDate());
		operation.setDate(date);
		operation.setCashAccount(session.find(CashAccount.class, operationModel.getCashAccountID()));
		OperationType type = operation.getType();
		operation.setCategory(session.find(type.getCategoryClass(), operationModel.getCategoryID()));
		operation.setAmount(new BigDecimal(operationModel.getAmount()));
		operation.setComment(operationModel.getComment());
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

	private void sortOperationList(List<? extends Operation> operations) {
		Collections.sort(operations, new Comparator<Operation>() {
			@Override
			public int compare(Operation o1, Operation o2) {
				int dateCompare = o1.getDate().compareTo(o2.getDate());
				if (dateCompare != 0) {
					return dateCompare;
				}
				return o1.getId() - o2.getId();
			}
		});
	}

}
