package ru.redw4y.HomeAccounting.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordValidator implements Validator {
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public boolean supports(Class<?> clazz) {
		return PasswordModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PasswordModel password = (PasswordModel) target;
		if (!password.getUpdated().equals(password.getRepeated())) {
			errors.rejectValue("updated", null, "Неверно повторен пароль");
		}
		if (password.getCurrent() != null&&!password.getWritten().equals("")) {
			if (!encoder.matches(password.getWritten(), password.getCurrent())) {
				errors.rejectValue("current", null, "Неверный текущий пароль");
			}
		} 
	}
}
