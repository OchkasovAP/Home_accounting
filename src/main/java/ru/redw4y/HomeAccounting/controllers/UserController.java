package ru.redw4y.HomeAccounting.controllers;


import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.UserService;
import ru.redw4y.HomeAccounting.util.PasswordModel;
import ru.redw4y.HomeAccounting.util.PasswordValidator;
import ru.redw4y.HomeAccounting.util.UserValidator;

@Controller
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserValidator userValidator;
	@Autowired
	private PasswordValidator passwordValidator;

	@GetMapping()
	public String showUsers(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		model.addAttribute("principal", userDetails.getUser());
		model.addAttribute("userList", userService.findAll());
		return "users/showUsers";
	}
	
	@GetMapping("/autorization")
	public String autorization() {
		return "users/autorization";
	}

	@GetMapping("/registration")
	public String createForm(@ModelAttribute("user") User user, @ModelAttribute("password") PasswordModel password) {
		return "users/registration";
	}

	@GetMapping("/{id}")
	public String editForm(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable("id") int id, @ModelAttribute("user") User user,
			Model model,@ModelAttribute("password") PasswordModel password) {
		User principal = userDetails.getUser();
		model.addAttribute("user", userService.findById(id));
		model.addAttribute("isRoleCorrector", principal.isAdmin() && principal.getId()!= id);
		return "users/editUser";
	}
	
	@PostMapping("/registration")
	public String create(@ModelAttribute("user") @Valid User user, BindingResult userBindingResult,
			@ModelAttribute("password") @Valid PasswordModel password, BindingResult passBindingResult) {
		userValidator.validate(user, userBindingResult);
		passwordValidator.validate(password, passBindingResult);
//		if(userBindingResult.hasErrors()||passBindingResult.hasErrors()) {
//			return "/users/registration";
//			
//		}
		userService.create(user, password);
		return "redirect:/users/autorization";
	}
	
	@PatchMapping("/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@ModelAttribute("isRoleCorrector") String isRoleCorrector,
			@ModelAttribute("user") @Valid User user, BindingResult userBindingResult,
			@ModelAttribute("password") @Valid PasswordModel password,BindingResult passBindingResult) {
		passwordValidator.validate(password, passBindingResult);
		userValidator.validate(user, userBindingResult);
		boolean passHasErrors = !(password.getWritten()==null||"".equals(password.getWritten()))
				&&passBindingResult.hasErrors();
		if(passHasErrors||userBindingResult.hasErrors()) {
			return "users/editUser";
		}
		userService.edit(user, password);
		if(userDetails.getUser().isAdmin()) {
			return "redirect:/users";
		}
		return "redirect:/menu";
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") int id) {
		userService.delete(id);
		return "redirect:/users";
	}
}
