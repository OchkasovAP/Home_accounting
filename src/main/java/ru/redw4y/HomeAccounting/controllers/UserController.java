package ru.redw4y.HomeAccounting.controllers;

import jakarta.validation.Valid;
import ru.redw4y.HomeAccounting.dto.AuthenticationDTO;
import ru.redw4y.HomeAccounting.dto.UserDTO;
import ru.redw4y.HomeAccounting.models.Role;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.UserService;
import ru.redw4y.HomeAccounting.util.exceptions.ForbiddenUsersActionException;
import ru.redw4y.HomeAccounting.util.exceptions.HomeAccountingException;
import ru.redw4y.HomeAccounting.util.exceptions.UserCannotBeRemovedException;
import ru.redw4y.HomeAccounting.util.exceptions.UserNotValidException;
import ru.redw4y.HomeAccounting.util.validators.UserValidator;
import ru.redw4y.HomeAccounting.util.validators.UserValidator;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@Controller
public class UserController extends AbstractHomeAccountingController {

	private final UserService userService;
	private final UserValidator userValidator;
	private final ModelMapper modelMapper;

	@Autowired
	public UserController(UserService userService, UserValidator userValidator, ModelMapper modelMapper) {
		super();
		this.userService = userService;
		this.userValidator = userValidator;
		this.modelMapper = modelMapper;
	}

	@GetMapping()
	public String showUsers(@AuthenticationPrincipal UserDetailsImpl userDetails,Model model) {
		model.addAttribute("users", userService.findAll().stream().map(this::convertUserToUserDTO).toList());
		model.addAttribute("principalId", userDetails.getUser().getId());
		return "/users/showUsers";
	}

	@GetMapping("/{id}")
	public String userInfo(@PathVariable("id") int id,
			@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		User principalUser = userDetails.getUser();
		if (principalUser.isAdmin() || userDetails.getUser().getId() == id) {
			try {
				AuthenticationDTO user = convertUserToAuthDTO(userService.findById(id));
				user.setPassword(null);
				model.addAttribute("user", user);
				model.addAttribute("isRoleCorrector", principalUser.isAdmin()&&principalUser.getId()!=id);
				return "/users/editUser";
			} catch (NoSuchElementException ex) {
				throw new HomeAccountingException("Пользователь с таким id не существует");
			}
		}
		throw new ForbiddenUsersActionException();
	}
	@GetMapping("/login")
	public String login(@ModelAttribute("user") AuthenticationDTO userDTO) {
		return "/users/autorization";
	}
	
	@GetMapping("/registration")
	public String createForm(@ModelAttribute("user") AuthenticationDTO user) {
		return "/users/registration";
	}

	@PostMapping("/registration")
	public String create(@ModelAttribute("user") @Valid AuthenticationDTO userDTO,
			BindingResult bindingResult) {
		userValidator.validate(userDTO, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/users/registration";
		}
		User user = convertDTO(userDTO);
		userService.create(user);
		return "redirect:/users/login";
	}

	@PatchMapping()//TODO Сделать валидацию пароля только при его изменении
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@ModelAttribute("user") @Valid AuthenticationDTO userDTO, BindingResult bindingResult) {
		User currentUser = userDetails.getUser();
		if (currentUser.isAdmin() || userDetails.getUser().getId() == userDTO.getId()) {
			userValidator.validate(userDTO, bindingResult);
			if (bindingResult.hasErrors()) {
				return "/users/editUser";
			}
			if(currentUser.getId()==userDTO.getId()) {
				userDTO.setRole(currentUser.getRole().getName());
			}
			User user = convertDTO(userDTO);
			if (userDTO.getNewPassword() != null) {
				user.setPassword(userDTO.getNewPassword());
			}
			userService.edit(user);
			return "redirect:/users/"+userDTO.getId();
		} else {
			throw new ForbiddenUsersActionException();
		}
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") int id,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		User authenticateUser = userDetails.getUser();
		if (authenticateUser.getId() == id && authenticateUser.isAdmin()) {
			throw new UserCannotBeRemovedException("Администратор не может удалить самого себя");
		} else if (!authenticateUser.isAdmin() && authenticateUser.getId() != id) {
			throw new UserCannotBeRemovedException("Пользователь в правами 'User' не может удалять других пользователей");
		}
		userService.delete(id);
		return "redirect:/users";
	}

	private User convertDTO(AuthenticationDTO userDTO) {
		User user = modelMapper.map(userDTO, User.class);
		user.setRole(new Role(0, userDTO.getRole()));
		return user;
	}

	private UserDTO convertUserToUserDTO(User user) {
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		userDTO.setRole(user.getRole().getName());
		return userDTO;
	}
	private AuthenticationDTO convertUserToAuthDTO(User user) {
		AuthenticationDTO userDTO = modelMapper.map(user, AuthenticationDTO.class);
		userDTO.setRole(user.getRole().getName());
		return userDTO;
	}
}
