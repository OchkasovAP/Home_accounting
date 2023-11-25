package ru.redw4y.HomeAccounting.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * The persistent class for the cash_account database table.
 * 
 */
@Entity
@Table(name = "cash_account")
@NamedQuery(name = "CashAccount.findAll", query = "SELECT c FROM CashAccount c")
public class CashAccount implements Serializable, Comparable<CashAccount> {
	private static final long serialVersionUID = 1L;
	public static BigDecimal getGeneralBalance(List<CashAccount> cashAccounts) {
		BigDecimal generalBalance = new BigDecimal(0);
		for (CashAccount cashAccount : cashAccounts) {
			if (cashAccount.getContainInGenBalance()) {
				generalBalance = generalBalance.add(cashAccount.getBalance());
			}
		}
		return generalBalance;
	}
	@Id
	@SequenceGenerator(name = "cash_account_gen", sequenceName = "cash_account_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "cash_account_gen", strategy = GenerationType.SEQUENCE)
	private Integer id;

	private BigDecimal balance;

	@Column(name = "contain_in_gen_balance")
	private Boolean containInGenBalance;

	private String name;

	@Override
	public String toString() {
		return String.format("Имя - %s, баланс - %s, учитывается в общем балансе - %s", name, balance, containInGenBalance?"да":"нет");
	}

	@ManyToOne
	private User user;

	// bi-directional many-to-one association to Income
	@OneToMany(mappedBy = "cashAccount")
	private List<Income> incomes;

	// bi-directional many-to-one association to Outcome
	@OneToMany(mappedBy = "cashAccount")
	private List<Outcome> outcomes;

	public CashAccount() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getBalance() {
		return this.balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Boolean getContainInGenBalance() {
		return this.containInGenBalance;
	}

	public void setContainInGenBalance(Boolean containInGenBalance) {
		this.containInGenBalance = containInGenBalance;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Income> getIncomes() {
		return this.incomes;
	}

	public void setIncomes(List<Income> incomes) {
		this.incomes = incomes;
	}

	public Income addIncome(Income income) {
		getIncomes().add(income);
		income.setCashAccount(this);
		balance = balance.add(income.getIncome());
		return income;
	}

	public Income removeIncome(Income income) {
		getIncomes().remove(income);
		income.setCashAccount(null);
		balance = balance.subtract(income.getIncome());
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
		outcome.setCashAccount(this);
		balance = balance.subtract(outcome.getOutcome());
		return outcome;
	}

	public Outcome removeOutcome(Outcome outcome) {
		getOutcomes().remove(outcome);
		outcome.setCashAccount(null);
		balance = balance.add(outcome.getOutcome());
		return outcome;
	}
	@Override
	public int compareTo(CashAccount o) {
		return id-o.getId();
	}
}