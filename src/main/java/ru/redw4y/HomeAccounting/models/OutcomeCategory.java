package ru.redw4y.HomeAccounting.models;

import java.io.Serializable;
import jakarta.persistence.*;
import ru.redw4y.HomeAccounting.entityUtil.Category;
import ru.redw4y.HomeAccounting.entityUtil.Operation;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;
import ru.redw4y.HomeAccounting.exceptions.OperationTypeException;

import java.util.List;

/**
 * The persistent class for the outcome_category database table.
 * 
 */
@Entity
@Table(name = "outcome_category")
public class OutcomeCategory implements Serializable, Category {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "outcome_category_gen", sequenceName = "outcome_category_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "outcome_category_gen", strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String name;

	@ManyToOne
	private User user;

	// bi-directional many-to-one association to Outcome
	@OneToMany(mappedBy = "outcomeCategory")
	private List<Outcome> outcomes;

	public OutcomeCategory() {
	}

	@Override
	public String toString() {
		return "OutcomeCategory [name=" + name + "]";
	}

	public OutcomeCategory(String name) {
		super();
		this.name = name;
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

	public List<Outcome> getOutcomes() {
		return this.outcomes;
	}
	public <T extends Operation> List<T> getOperations() {
		return (List<T>) getOutcomes();
	}

	public void setOutcomes(List<Outcome> outcomes) {
		this.outcomes = outcomes;
	}

	@Override
	public int compareTo(Category o) {
		return id - o.getId();
	}

	@Override
	public OperationType getType() {
		return OperationType.OUTCOME;
	}
	

}