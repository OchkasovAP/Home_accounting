package ru.redw4y.HomeAccounting.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.RoleRepository;
import ru.redw4y.HomeAccounting.repository.UserRepository;
import ru.redw4y.HomeAccounting.util.Category;
import ru.redw4y.HomeAccounting.util.OperationType;

@Service
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final CategoriesService categoriesService;
	private final CashAccountsService accountService;
	private final PasswordEncoder encoder;

	@Autowired
	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, CategoriesService categoriesService, CashAccountsService accountService) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.categoriesService = categoriesService;
		this.accountService = accountService;
		this.encoder = encoder;
	}

	@Transactional
	public void create(User user) {
		user.setRole(roleRepository.findByName("USER").get());
		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user);
		categoriesService.addDefaultCategories(user.getId());
		user.setCashAccounts(new ArrayList<>());
		accountService.create(user.getId(), new CashAccount.Builder()
				.balance(new BigDecimal(0))
				.containInGenBalance(true)
				.name("Основной").build());
	}

	@Transactional
	public void edit(User user) {
		User userFromDB = userRepository.findById(user.getId()).get();
		Hibernate.initialize(userFromDB);
		userFromDB.setLogin(user.getLogin());
		if(!user.getPassword().isBlank()) {
			userFromDB.setPassword(encoder.encode(user.getPassword()));
		}
		if (user.getRole() != null) {
			String roleName = user.getRole().getName().toUpperCase();
			userFromDB.setRole(roleRepository.findByName(roleName.equals("ADMIN") ? "ADMIN" : "USER").get());
		}
		userRepository.save(userFromDB);
	}

	@Transactional
	public void delete(int id) {
		userRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	
	

}
