package ru.redw4y.HomeAccounting.util.exceptions;

import org.springframework.validation.BindingResult;

public class CategoryNotValidException extends FieldsNotValidException{

	public CategoryNotValidException(BindingResult bindingResult) {
		super(bindingResult);
	}

}
