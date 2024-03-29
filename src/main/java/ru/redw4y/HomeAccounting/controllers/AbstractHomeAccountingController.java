package ru.redw4y.HomeAccounting.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ru.redw4y.HomeAccounting.util.exceptions.ForbiddenUsersActionException;
import ru.redw4y.HomeAccounting.util.exceptions.HomeAccountingException;

public abstract class AbstractHomeAccountingController {
	@ExceptionHandler
	protected ResponseEntity<Map<String, Object>> handleException(HomeAccountingException ex) {
		return new ResponseEntity<>(Map.of("Exception", ex.getMessage(),"time", System.currentTimeMillis()),HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler
	protected ResponseEntity<Map<String, Object>> handleException(ForbiddenUsersActionException ex) {
		return new ResponseEntity<>(Map.of("Exception", ex.getMessage(),"time", System.currentTimeMillis()),HttpStatus.FORBIDDEN);
	}
	
}
