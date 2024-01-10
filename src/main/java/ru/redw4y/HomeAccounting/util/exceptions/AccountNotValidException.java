package ru.redw4y.HomeAccounting.util.exceptions;

import org.springframework.validation.BindingResult;

public class AccountNotValidException extends FieldsNotValidException{

	public AccountNotValidException(BindingResult bindingResult) {
		super(bindingResult);
	}
	
}
