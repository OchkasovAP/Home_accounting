package ru.redw4y.HomeAccounting.model;

import java.io.Serializable;
import javax.persistence.*;
import ru.redw4y.HomeAccounting.entityUtil.Category;
import ru.redw4y.HomeAccounting.entityUtil.Operation;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;
import ru.redw4y.HomeAccounting.util.DateUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * The persistent class for the incomes database table.
 * 
 */
@Entity
@Table(name = "incomes")
@NamedQuery(name = "Income.findAll", query = "SELECT i FROM Income i")
public class Income implements Serializable, Operation {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "income_gen", sequenceName = "incomes_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "income_gen", strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String comment;

	private Date date;

	private BigDecimal income;

	// bi-directional many-to-one association to CashAccount
	@ManyToOne
	@JoinColumn(name = "cash_account_id")
	private CashAccount cashAccount;

	// bi-directional many-to-one association to IncomeCategory
	@ManyToOne
	@JoinColumn(name = "category_id")
	private IncomeCategory incomeCategory;

	// bi-directional many-to-one association to User
	@ManyToOne
	private User user;

	public Income() {
	}

	public Income(String comment, Date date, BigDecimal income) {
		super();
		this.comment = comment;
		this.date = date;
		this.income = income;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getIncome() {
		return this.income;
	}

	@Override
	public BigDecimal getAmount() {
		return getIncome();
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	@Override
	public void setAmount(BigDecimal amount) {
		setIncome(amount);
	}

	public CashAccount getCashAccount() {
		return this.cashAccount;
	}

	public void setCashAccount(CashAccount cashAccount) {
		this.cashAccount = cashAccount;
	}

	public IncomeCategory getIncomeCategory() {
		return this.incomeCategory;
	}

	@Override
	public Category getCategory() {
		return getIncomeCategory();
	}

	public void setIncomeCategory(IncomeCategory incomeCategory) {
		this.incomeCategory = incomeCategory;
	}

	@Override
	public void setCategory(Category category) {
		if (category instanceof IncomeCategory) {
			setIncomeCategory((IncomeCategory) category);
		}
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Доход [дата=" + DateUtil.convertDateToString(date) + ", доход = " + income + ", счет: " + cashAccount.getName() + ", категория: "
				+ incomeCategory.getName() + ", комментарий: "
						+ comment +"]";
	}

	@Override
	public OperationType getType() {
		return OperationType.INCOME;
	}
	
}