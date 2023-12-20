package ru.redw4y.HomeAccounting.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.services.UserService;

@Component
public class UserValidator implements Validator {
	@Autowired
	private UserService service;

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User user = (User) target;
		Optional<User> findingUser = service.findByLogin(user.getLogin());
		if (findingUser.isPresent()) {
			if (findingUser.get().getId()!=user.getId()) {
				errors.rejectValue("login", "", "Такой пользователь уже существует");
			}
		}
	}
}
