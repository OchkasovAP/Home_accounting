package ru.redw4y.HomeAccounting.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;
import ru.redw4y.HomeAccounting.entityUtil.Category;
import ru.redw4y.HomeAccounting.entityUtil.Operation;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;
import ru.redw4y.HomeAccounting.exceptions.OperationTypeException;


import java.util.List;

/**
 * The persistent class for the users database table.
 * 
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

	@Id
	@GeneratedValue(generator = "user_gen", strategy = GenerationType.IDENTITY)
	private Integer id;

	private String login;

	private String password;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Income> incomes;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Outcome> outcomes;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CashAccount> cashAccounts;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<IncomeCategory> incomeCategories;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OutcomeCategory> outcomeCategories;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@Override
	public String toString() {
		return "User [login=" + login + ", cashAccounts=" + cashAccounts + ", incomeCategories=" + incomeCategories
				+ ", outcomeCategories=" + outcomeCategories + ", role=" + role + "]";
	}

	public User() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Income> getIncomes() {
		return this.incomes;
	}
	

	public void setIncomes(List<Income> incomes) {
		this.incomes = incomes;
	}

	public Income addIncome(Income income) {
		getIncomes().add(income);
		income.setUser(this);
		CashAccount cashAccount = income.getCashAccount();
		BigDecimal accountBalance = cashAccount.getBalance();
		cashAccount.setBalance(accountBalance.add(income.getIncome()));
		return income;
	}

	public Income removeIncome(Income income) {
		getIncomes().remove(income);
		income.setUser(null);
		CashAccount cashAccount = income.getCashAccount();
		BigDecimal accountBalance = cashAccount.getBalance();
		cashAccount.setBalance(accountBalance.subtract(income.getIncome()));
		return income;
	}

	public List<Outcome> getOutcomes() {
		return this.outcomes;
	}


	public void setOutcomes(List<Outcome> outcomes) {
		this.outcomes = outcomes;
	}

	public Outcome addOutcome(Outcome outcome) {
		getOutcomes().add(outcome);
		outcome.setUser(this);
		CashAccount cashAccount = outcome.getCashAccount();
		BigDecimal accountBalance = cashAccount.getBalance();
		cashAccount.setBalance(accountBalance.subtract(outcome.getOutcome()));
		return outcome;
	}

	public Outcome removeOutcome(Outcome outcome) {
		getOutcomes().remove(outcome);
		outcome.setUser(null);
		CashAccount cashAccount = outcome.getCashAccount();
		BigDecimal accountBalance = cashAccount.getBalance();
		cashAccount.setBalance(accountBalance.add(outcome.getOutcome()));
		return outcome;
	}

	public List<CashAccount> getCashAccounts() {
		return this.cashAccounts;
	}

	public void setCashAccounts(List<CashAccount> cashAccounts) {
		this.cashAccounts = cashAccounts;
	}

	public CashAccount addCashAccount(CashAccount cashAccount) {
		getCashAccounts().add(cashAccount);
		cashAccount.setUser(this);

		return cashAccount;
	}

	public CashAccount removeCashAccount(CashAccount cashAccount) {
		getCashAccounts().remove(cashAccount);
		cashAccount.setUser(null);
		return cashAccount;
	}

	public List<IncomeCategory> getIncomeCategories() {
		return incomeCategories;
	}

	public void setIncomeCategories(List<IncomeCategory> incomeCategories) {
		this.incomeCategories = incomeCategories;
	}

	public IncomeCategory addIncomeCategory(IncomeCategory incomeCategory) {
		getIncomeCategories().add(incomeCategory);
		incomeCategory.setUser(this);
		return incomeCategory;
	}

	public IncomeCategory removeIncomeCategory(IncomeCategory incomeCategory) {
		getIncomeCategories().remove(incomeCategory);
		incomeCategory.setUser(null);

		return incomeCategory;
	}

	public List<OutcomeCategory> getOutcomeCategories() {
		return outcomeCategories;
	}

	public void setOutcomeCategories(List<OutcomeCategory> outcomeCategories) {
		this.outcomeCategories = outcomeCategories;
	}
	
	public List<? extends Category> getCategories(OperationType type) {
		if (type.equals(OperationType.INCOME)) {
			return getIncomeCategories();
		} else if (type.equals(OperationType.OUTCOME)) {
			return getOutcomeCategories();
		}
		throw new OperationTypeException();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Operation> List<T> getOperations(OperationType type) {
		if (type.equals(OperationType.INCOME)) {
			return (List<T>) getIncomes();
		} else if (type.equals(OperationType.OUTCOME)) {
			return (List<T>) getOutcomes();
		}
		throw new OperationTypeException();
	}

	public OutcomeCategory addOutcomeCategory(OutcomeCategory outcomeCategory) {
		getOutcomeCategories().add(outcomeCategory);
		outcomeCategory.setUser(this);

		return outcomeCategory;
	}

	public OutcomeCategory removeOutcomeCategory(OutcomeCategory outcomeCategory) {
		getOutcomeCategories().remove(outcomeCategory);
		outcomeCategory.setUser(null);

		return outcomeCategory;
	}

	public Operation addOperation(Operation operation) {
		if (operation instanceof Income) {
			return addIncome((Income) operation);
		} else if (operation instanceof Outcome) {
			return addOutcome((Outcome) operation);
		}
		throw new OperationTypeException();
	}

	public Operation removeOperation(Operation operation) {
		if (operation instanceof Outcome) {
			return removeOutcome((Outcome) operation);
		} else if (operation instanceof Income) {
			return removeIncome((Income) operation);
		} 
		throw new OperationTypeException();
	}
	
	public Category addCategory(Category category) {
		if (category instanceof IncomeCategory) {
			return addIncomeCategory((IncomeCategory) category);
		} else if (category instanceof OutcomeCategory) {
			return addOutcomeCategory((OutcomeCategory) category);
		}
		throw new OperationTypeException();
	}

	public Category removeCategory(Category category) {
		if (category instanceof IncomeCategory) {
			return removeIncomeCategory((IncomeCategory) category);
		} else if (category instanceof OutcomeCategory) {
			return removeOutcomeCategory((OutcomeCategory) category);
		}
		throw new OperationTypeException();
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isAdmin() {
		return role.getName().equals(Roles.ADMIN.toString());
	}
}