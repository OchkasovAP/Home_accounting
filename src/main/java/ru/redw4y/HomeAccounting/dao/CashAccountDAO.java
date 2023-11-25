package ru.redw4y.HomeAccounting.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.redw4y.HomeAccounting.entity.CashAccount;
import ru.redw4y.HomeAccounting.entity.User;
import ru.redw4y.HomeAccounting.util.Actioner;

@Component
public class CashAccountDAO extends AbstractDAO {
	@Autowired
	private UserDAO userDAO;

	public void addACashAccout(int userID, CashAccount cashAccount) {
		executeTransaction(new Actioner() {
			@Override
			public void action() throws Exception {
				User user = getCurrentSession().find(User.class, userID);
				user.addCashAccount(cashAccount);
			}
		});
	}

	public void removeCashAccount(int userID, int cashAccountID) {
		executeTransaction(new Actioner() {
			@Override
			public void action() throws Exception {
				Session session = getCurrentSession();
				User user = session.find(User.class, userID);
				CashAccount cashAccount = session.find(CashAccount.class, cashAccountID);
				user.removeCashAccount(cashAccount);
			}
		});
	}

	public CashAccount getCashAccount(int cashAccID) {
		return getItem(cashAccID, CashAccount.class);
	}

	public void editCashAccount(CashAccount cashAccount) {
		executeTransaction(new Actioner() {
			@Override
			public void action() throws Exception {
				CashAccount editionAccount = getCurrentSession().find(CashAccount.class, cashAccount.getId());
				editionAccount.setName(cashAccount.getName());
				editionAccount.setBalance(cashAccount.getBalance());
				editionAccount.setContainInGenBalance(cashAccount.getContainInGenBalance());
			}
		});
	}

	public List<CashAccount> getCashAccounts(int userID) {
		List<CashAccount> cashAccounts = userDAO.getUser(userID).getCashAccounts();
		Collections.sort(cashAccounts);
		return cashAccounts;
	}
}
