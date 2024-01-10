package ru.redw4y.HomeAccounting.util.validators;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.redw4y.HomeAccounting.dto.AuthenticationDTO;

import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.services.UserService;

@Component
public class UserValidator implements Validator {
	private final PasswordEncoder encoder;
	private final UserService service;
	private AuthenticationDTO validUser;

	@Autowired
	public UserValidator(PasswordEncoder encoder, UserService service) {
		super();
		this.encoder = encoder;
		this.service = service;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return AuthenticationDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		validUser = (AuthenticationDTO) target;
		if(validUser.getLogin()==null||validUser.getPassword()==null) {
			return;
		}
		if(!isNewUser()) {
			User user = service.findById(validUser.getId());
			if (!encoder.matches(validUser.getPassword(), user.getPassword())) {
				errors.rejectValue("password", null, "Неверный текущий пароль");
			}
		}
		if(nonCorrectRepeatPassword()) {
			errors.rejectValue("repeatedNewPassword", null, "Неверно повторен пароль");
		}
		if(loginExists()) {
			errors.rejectValue("login", null, "Пользователь с таким именем уже существует");
		}
	}
	private boolean isNewUser() {
		return validUser.getId()==0;
	}
	private boolean loginExists() {
		Optional<User> userFromDB = service.findByLogin(validUser.getLogin());
		return userFromDB.isPresent()&&userFromDB.get().getId() != validUser.getId(); 	
	}
	private boolean nonCorrectRepeatPassword() {
		if(isNewUser()) {
			return !validUser.getPassword().equals(validUser.getRepeatedNewPassword());
		} else {
			return validUser.getNewPassword()!=null&&!validUser.getNewPassword().equals(validUser.getRepeatedNewPassword());
		}
	}
}
