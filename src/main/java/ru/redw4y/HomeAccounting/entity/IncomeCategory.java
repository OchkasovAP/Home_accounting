package ru.redw4y.HomeAccounting.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import ru.redw4y.HomeAccounting.entityUtil.Category;
import ru.redw4y.HomeAccounting.entityUtil.Operation;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the income_category database table.
 * 
 */
@Entity
@Table(name="income_category")
@NamedQuery(name="IncomeCategory.findAll", query="SELECT i FROM IncomeCategory i")
public class IncomeCategory implements Serializable, Category {
	private static final long serialVersionUID = 1L;
	

	public IncomeCategory(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "IncomeCategory [name=" + name + "]";
	}

	@Id
	@SequenceGenerator(name = "income_category_gen", sequenceName = "income_category_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "income_category_gen", strategy =GenerationType.SEQUENCE )
	private Integer id;

	private String name;
	
	@ManyToOne
	private User user;

	//bi-directional many-to-one association to Income
	@OneToMany(mappedBy="incomeCategory")
	private List<Income> incomes;

	public IncomeCategory() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Income> getIncomes() {
		return this.incomes;
	}
	
	public <T extends Operation> List<T> getOperations() {
		return (List<T>) getIncomes();
	}

	public void setIncomes(List<Income> incomes) {
		this.incomes = incomes;
	}

	public Income addIncome(Income income) {
		getIncomes().add(income);
		income.setIncomeCategory(this);

		return income;
	}

	public Income removeIncome(Income income) {
		getIncomes().remove(income);
		income.setIncomeCategory(null);

		return income;
	}

	@Override
	public int compareTo(Category o) {
		return id-o.getId();
	}
	
	@Override
	public OperationType getType() {
		return OperationType.INCOME;
	}
	
}