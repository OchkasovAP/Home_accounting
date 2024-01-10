package ru.redw4y.HomeAccounting.util.validators;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.redw4y.HomeAccounting.dto.OperationDTO;
import ru.redw4y.HomeAccounting.models.Income;
import ru.redw4y.HomeAccounting.models.Outcome;
import ru.redw4y.HomeAccounting.util.DateUtil;
import ru.redw4y.HomeAccounting.util.Operation;
@Component
public class OperationValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return OperationDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		OperationDTO operation = (OperationDTO) target;
		Date operationDate = operation.getDate();
		if(operationDate!=null&&operationDate.getTime()>System.currentTimeMillis()) {
			errors.rejectValue("date", null, "Дата не может быть позже текущей");
		}
		
	}
	
}
