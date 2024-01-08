package ru.redw4y.HomeAccounting.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.DoNotMock;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.redw4y.HomeAccounting.dto.PasswordDTO;
import ru.redw4y.HomeAccounting.models.Role;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.RoleRepository;
import ru.redw4y.HomeAccounting.repository.UserRepository;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@InjectMocks
	private UserService service;
	@Mock
	private UserRepository userRepository;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private PasswordEncoder encoder;
	
	private PasswordEncoder nonMockEncoder;
	private List<User> users;
	private Map<String, Role> roles;
	
	@BeforeEach
	void setUp() {
		nonMockEncoder = new BCryptPasswordEncoder();
		users = new ArrayList<>();
		roles = new HashMap<>(Map.of("ADMIN", new Role(0, "ADMIN"), "USER", new Role(1, "USER")));
		users.add(new User.Builder()
							.id(0)
							.login("User0")
							.password("password0")
							.role(roles.get("ADMIN"))
							.build());
		users.add(new User.Builder()
							.id(1)
							.login("User1")
							.password("password1")
							.role(roles.get("USER"))
							.build());
		
	}
	@Test
	void create_CorrectUserAndCorrectPassword() {
		User user = new User.Builder()
			.id(2)
			.login("User2")
			.build();
		PasswordDTO password = new PasswordDTO.Builder().updated("password2").build();
		when(userRepository.save(user)).thenAnswer(a -> {
			users.add(user);
			return user;
		});
		when(roleRepository.findByName("USER")).thenReturn(Optional.of(roles.get("USER")));
		when(encoder.encode(password.getUpdated())).thenReturn(nonMockEncoder.encode(password.getUpdated()));
		service.create(user, password);
		assertEquals(user.getRole(), roles.get("USER"));
		assertTrue(nonMockEncoder.matches(password.getUpdated(), user.getPassword()));
		assertTrue(users.contains(user));
	}
	
	@Test
	void edit_CurrentLoginAndUpdatePassword() {
		User user = users.get(1);
		User editUser = new User.Builder().login(user.getLogin()).id(user.getId()).build();
		PasswordDTO password = new PasswordDTO.Builder().current("password1").updated("newpassword1").build();
		when(encoder.encode(password.getUpdated())).thenReturn(nonMockEncoder.encode(password.getUpdated()));
		when(userRepository.findById(1)).thenReturn(Optional.of(users.get(1)));
		when(userRepository.save(user)).thenReturn(user);
		service.edit(editUser, password);
		assertTrue(nonMockEncoder.matches(password.getUpdated(), users.get(1).getPassword()));
		assertEquals(users.get(1).getLogin(), editUser.getLogin());
	}
	@Test
	void edit_UpdateLoginAndCurrentPassword() {
		User user = users.get(1);
		User editUser = new User.Builder().login("NewUserLogin").id(user.getId()).build();
		PasswordDTO password = new PasswordDTO.Builder().build();
		when(userRepository.findById(1)).thenReturn(Optional.of(users.get(1)));
		when(userRepository.save(user)).thenReturn(user);
		service.edit(editUser, password);
		assertEquals(users.get(1).getLogin(), editUser.getLogin());
	}

}
