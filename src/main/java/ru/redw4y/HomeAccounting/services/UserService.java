package ru.redw4y.HomeAccounting.services;

import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.RoleRepository;
import ru.redw4y.HomeAccounting.repository.UserRepository;
import ru.redw4y.HomeAccounting.util.PasswordModel;

@Service
@Transactional(readOnly = true)
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder encoder;

	@Transactional
	public void create(User user, PasswordModel password) {
		user.setRole(roleRepository.findByName("USER").get());
		user.setPassword(encoder.encode(password.getUpdated()));
		userRepository.save(user);
	}

	@Transactional
	public void edit(User user, PasswordModel password) {
		User userFromDB = userRepository.findById(user.getId()).get();
		Hibernate.initialize(userFromDB);
		userFromDB.setLogin(user.getLogin());
		if (!(password.getCurrent()==null||"".equals(password.getCurrent()))) {
			userFromDB.setPassword(encoder.encode(password.getUpdated()));
		} 
		userRepository.save(userFromDB);
	}

	@Transactional
	public void delete(int id) {
		userRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Transactional(readOnly = true)
	public User findById(int id) {
		return userRepository.findById(id).get();
	}
	
	@Transactional(readOnly = true)
	public Optional<User> findByLogin(String login) {
		return userRepository.findByLogin(login);
	}

	@Transactional
	public User getFullUser(int id) {
		User user = userRepository.findById(id).get();
		Hibernate.initialize(user);
		user.getCashAccounts().isEmpty();
		user.getIncomeCategories().isEmpty();
		user.getOutcomeCategories().isEmpty();
		user.getIncomes().isEmpty();
		user.getOutcomes().isEmpty();
		return user;
	}
}
