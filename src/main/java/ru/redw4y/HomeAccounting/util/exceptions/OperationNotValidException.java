package ru.redw4y.HomeAccounting.util.exceptions;

import org.springframework.validation.BindingResult;

public class OperationNotValidException extends FieldsNotValidException{

	public OperationNotValidException(BindingResult bindingResult) {
		super(bindingResult);
	}
	
}