package ru.redw4y.HomeAccounting.model;

import java.io.Serializable;
import jakarta.persistence.*;
import ru.redw4y.HomeAccounting.entityUtil.Category;
import ru.redw4y.HomeAccounting.entityUtil.Operation;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;
import ru.redw4y.HomeAccounting.util.DateUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * The persistent class for the outcomes database table.
 * 
 */
@Entity
@Table(name = "outcomes")
@NamedQuery(name = "Outcome.findAll", query = "SELECT o FROM Outcome o")
public class Outcome implements Serializable, Operation {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "outcome_gen", sequenceName = "outcomes_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "outcome_gen", strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String comment;

	private Date date;

	private BigDecimal outcome;

	// bi-directional many-to-one association to CashAccount
	@ManyToOne
	@JoinColumn(name = "cash_account_id")
	private CashAccount cashAccount;

	// bi-directional many-to-one association to OutcomeCategory
	@ManyToOne
	@JoinColumn(name = "category_id")
	private OutcomeCategory outcomeCategory;

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Outcome() {
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

	public BigDecimal getOutcome() {
		return this.outcome;
	}

	@Override
	public BigDecimal getAmount() {
		return getOutcome();
	}

	public void setOutcome(BigDecimal outcome) {
		this.outcome = outcome;
	}

	@Override
	public void setAmount(BigDecimal amount) {
		setOutcome(amount);
	}

	public CashAccount getCashAccount() {
		return this.cashAccount;
	}

	public void setCashAccount(CashAccount cashAccount) {
		this.cashAccount = cashAccount;
	}

	public OutcomeCategory getOutcomeCategory() {
		return this.outcomeCategory;
	}

	@Override
	public Category getCategory() {
		return getOutcomeCategory();
	}

	public void setOutcomeCategory(OutcomeCategory outcomeCategory) {
		this.outcomeCategory = outcomeCategory;
	}

	@Override
	public void setCategory(Category category) {
		if(category instanceof OutcomeCategory) {
			setOutcomeCategory((OutcomeCategory) category);
		}
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public OperationType getType() {
		return OperationType.OUTCOME;
	}

	@Override
	public String toString() {
		return "Расход [дата=" + DateUtil.convertDateToString(date) + ", расход = " + outcome + ", счет: " + cashAccount.getName() + ", категория: "
				+ outcomeCategory.getName() + ", комментарий: "
						+ comment +"]";
	}
	
	
}