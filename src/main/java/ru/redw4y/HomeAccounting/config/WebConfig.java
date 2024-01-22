package ru.redw4y.HomeAccounting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ru.redw4y.HomeAccounting.util.StringToOperationTypeConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToOperationTypeConverter());
	}
}
