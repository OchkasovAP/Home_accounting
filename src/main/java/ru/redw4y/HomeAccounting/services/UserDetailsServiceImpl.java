package ru.redw4y.HomeAccounting.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.UserRepository;
import ru.redw4y.HomeAccounting.security.UserDetailsImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetailsImpl loadUserByUsername(String login) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByLogin(login);
		if(user.isEmpty()) {
			throw new UsernameNotFoundException("Пользователь не найден");
		}
		return new UserDetailsImpl(user.get());
	}
}
