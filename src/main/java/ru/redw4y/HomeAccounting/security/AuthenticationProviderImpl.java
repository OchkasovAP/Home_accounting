package ru.redw4y.HomeAccounting.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import ru.redw4y.HomeAccounting.services.UserDetailsServiceImpl;
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider{
	@Autowired
	private UserDetailsServiceImpl service;
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		UserDetailsImpl user = service.loadUserByUsername(name);
		String password = authentication.getCredentials().toString();
		if(!password.equals(user.getPassword())) {
			throw new BadCredentialsException("Не верный пароль");
		}
		return new UsernamePasswordAuthenticationToken(user, password, Collections.emptyList());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

}
