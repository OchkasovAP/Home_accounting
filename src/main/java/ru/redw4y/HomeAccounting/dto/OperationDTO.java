package ru.redw4y.HomeAccounting.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.redw4y.HomeAccounting.util.DateUtil;
import ru.redw4y.HomeAccounting.util.Operation;

public class OperationDTO {
	private int id;
	@NotNull(message = "Поле не должно быть пустым")
	private String account;
	@NotNull(message = "Поле не должно быть пустым")
	private String category;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Поле не должно быть пустым")
	private Date date;
	@Size(max=50, message = "Комментарий не должен превышать 50 символов")
	private String comment;
	@NotNull(message = "Поле не должно быть пустым")
	private BigDecimal amount;
	
	public OperationDTO() {
		super();
	}
	
	public static class Builder{
		private OperationDTO operationDTO;
		public Builder() {
			operationDTO = new OperationDTO();
		}
		public Builder id(int id) {
			operationDTO.setId(id);
			return this;
		}
		public Builder cashAccount(String account) {
			operationDTO.setAccount(account);
			return this;
		}
		public Builder category(String category) {
			operationDTO.setCategory(category);
			return this;
		}
		public Builder date(Date date) {
			operationDTO.setDate(date);
			return this;
		}
		public Builder comment(String comment) {
			operationDTO.setComment(comment);
			return this;
		}
		public Builder amount(BigDecimal amount) {
			operationDTO.setAmount(amount);
			return this;
		}
		public OperationDTO build() {
			return operationDTO;
		}
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}


	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "[дата=" + DateUtil.convertDateToString(date) + ", расход = " + amount + ", счет: " + account + ", категория: "
				+ category + ", комментарий: "
						+ comment +"]";
	}
}
