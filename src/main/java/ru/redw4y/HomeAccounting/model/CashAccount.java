package ru.redw4y.HomeAccounting.model;

import java.io.Serializable;
import javax.persistence.*;
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

	@ManyToOne
	private User user;

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

	
	@Override
	public String toString() {
		return String.format("Имя - %s, баланс - %s, учитывается в общем балансе - %s", name, balance, containInGenBalance?"да":"нет");
	}
	@Override
	public int compareTo(CashAccount o) {
		return id-o.getId();
	}
}