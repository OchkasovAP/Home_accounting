package ru.redw4y.HomeAccounting.util;

import org.springframework.core.convert.converter.Converter;

public class StringToOperationTypeConverter implements Converter<String, OperationType> {
	@Override
	public OperationType convert(String source) {
		return OperationType.valueOf(source.toUpperCase());
	}
}
