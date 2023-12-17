package ru.redw4y.HomeAccounting.services;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.RoleRepository;
import ru.redw4y.HomeAccounting.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional
	public void create(User user) {
		user.setRole(roleRepository.findByName("USER").get());
		userRepository.save(user);
	}

	@Transactional
	public void edit(User user) {
		userRepository.save(user);
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
	@Transactional
	public User getFullUser(int id) {
		User user = userRepository.findById(id).get();
		Hibernate.initialize(user);
		user.getCashAccounts().isEmpty(); //Почему-то без вызова здесь методов с этими коллекциями, передает юзера с пустыми коллекциями
		user.getIncomeCategories().isEmpty();
		user.getOutcomeCategories().isEmpty();
		user.getIncomes().isEmpty();
		user.getOutcomes().isEmpty();
		return user;
	}
}
