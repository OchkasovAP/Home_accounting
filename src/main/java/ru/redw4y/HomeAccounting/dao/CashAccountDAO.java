package ru.redw4y.HomeAccounting.dao;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ru.redw4y.HomeAccounting.model.CashAccount;
import ru.redw4y.HomeAccounting.model.User;

@Component
public class CashAccountDAO {
	@Autowired
	private SessionFactory sessionFactory;

	@Transactional
	public void addACashAccout(int userID, CashAccount cashAccount) {
		Session session = sessionFactory.getCurrentSession();
		User user = session.find(User.class, userID);
		user.addCashAccount(cashAccount);
	}

	@Transactional
	public void removeCashAccount(int userID, int cashAccountID) {
		Session session = sessionFactory.getCurrentSession();
		User user = session.find(User.class, userID);
		CashAccount cashAccount = session.find(CashAccount.class, cashAccountID);
		user.removeCashAccount(cashAccount);
	}

	@Transactional(readOnly = true)
	public CashAccount getCashAccount(int cashAccID) {
		Session session = sessionFactory.getCurrentSession();
		return session.find(CashAccount.class, cashAccID);
	}

	@Transactional
	public void editCashAccount(CashAccount cashAccount) {
		Session session = sessionFactory.getCurrentSession();
		CashAccount editionAccount = session.find(CashAccount.class, cashAccount.getId());
		editionAccount.setName(cashAccount.getName());
		editionAccount.setBalance(cashAccount.getBalance());
		editionAccount.setContainInGenBalance(cashAccount.getContainInGenBalance());
	}

	@Transactional(readOnly = true)
	public List<CashAccount> getCashAccounts(int userID) {
		Session session = sessionFactory.getCurrentSession();
		User user = session.find(User.class, userID);
		List<CashAccount> cashAccounts = user.getCashAccounts().stream().sorted().toList();
		return cashAccounts;
	}
}
