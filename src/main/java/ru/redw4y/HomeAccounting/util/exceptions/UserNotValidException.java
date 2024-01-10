package ru.redw4y.HomeAccounting.util.exceptions;

import org.springframework.validation.BindingResult;

public class UserNotValidException extends FieldsNotValidException{

	public UserNotValidException(BindingResult bindingResult) {
		super(bindingResult);
	}
	
}
