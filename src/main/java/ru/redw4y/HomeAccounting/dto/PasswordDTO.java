package ru.redw4y.HomeAccounting.dto;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Component
public class PasswordDTO {
	private String current;
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"
			, message = "Пароль должен иметь 1 цифру, 1 строчную, 1 прописную латинскую букву")
	@Size(min = 6, message = "Пароль должен быть длинной не менее 6 символов")
	private String updated;
	private String repeated;
	private String written;
	
	public static class Builder {
		private PasswordDTO model = new PasswordDTO();
		
		public Builder current(String current) {
			model.setCurrent(current);
			return this;
		}
		public Builder updated(String updated) {
			model.setUpdated(updated);
			return this;
		}
		public Builder repeated(String repeated) {
			model.setRepeated(repeated);
			return this;
		}
		public Builder written(String written) {
			model.setWritten(written);
			return this;
		}
		public PasswordDTO build() {
			return model;
		}
	}
	public String getCurrent() {
		return current;
	}
	public void setCurrent(String current) {
		this.current = current;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getRepeated() {
		return repeated;
	}
	public void setRepeated(String repeated) {
		this.repeated = repeated;
	}
	public String getWritten() {
		return written;
	}
	public void setWritten(String written) {
		this.written = written;
	}
}
