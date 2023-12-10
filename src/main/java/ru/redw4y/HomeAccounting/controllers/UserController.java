package ru.redw4y.HomeAccounting.controllers;

import javax.servlet.http.HttpSession;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import ru.redw4y.HomeAccounting.dao.UserDAO;
import ru.redw4y.HomeAccounting.exceptions.UserException;
import ru.redw4y.HomeAccounting.model.User;

@Controller
@RequestMapping("/users")
public class UserController {
	private UserDAO userDAO;
	
	
	@Autowired
	public UserController(UserDAO userDAO) {
		super();
		this.userDAO = userDAO;
	}

	@GetMapping()
	public String showUsers(@SessionAttribute("userID") Integer userID, Model model) {
		User currentUser = userDAO.getUser(userID);
		model.addAttribute("thisUser", currentUser);
		if (currentUser.isAdmin()) {
			model.addAttribute("userList", userDAO.getUserList());
			return "users/showUsers";
		}
		return "redirect:/";
	}
	
	@GetMapping("/autorization")
	public String autorization(HttpSession session, @ModelAttribute("user") User user, Model model) {
		if (session.getAttribute("userID") == null) {
			try {
				if (user.getLogin() != null) {
					session.setAttribute("userID", userDAO.checkUser(user).getId());
					return "redirect:/";
				}
			} catch (UserException ex) {
				model.addAttribute("error", ex.getMessage());
			}
			return "users/autorization";
		}
		return "redirect:/";
	}

	@GetMapping("/registration")
	public String createForm(@ModelAttribute("user") User user) {
		return "users/registration";
	}

	@GetMapping("/{id}")
	public String editForm(@SessionAttribute("userID") int userID, @PathVariable("id") int id, Model model) {
		User editionUser = userDAO.getUser(id);
		model.addAttribute("editionUser", editionUser);
		model.addAttribute("userID", id);
		User currentUser = userDAO.getUser(userID);
		model.addAttribute("isRoleCorrector", currentUser.isAdmin() && userID != id);
		return "users/editUser";
	}

	@PostMapping()
	public String create(@ModelAttribute("user") User user, Model model) {
		try {
			userDAO.addNewUser(user);
		} catch (ConstraintViolationException ex) {
			model.addAttribute("error", "This user already exists");
			return "redirect:/users/registration";
		}
		return "redirect:/users/autorization";
	}

	@PatchMapping("/{id}")
	public String edit(@SessionAttribute("userID") int userID, @PathVariable("id") int id, @ModelAttribute("user") User user) {
		userDAO.editUser(id, user);
		if(userDAO.getUser(userID).isAdmin()) {
			return "redirect:/users";
		}
		return "redirect:/";
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") int id) {
		userDAO.deleteUser(id);
		return "redirect:/users";
	}
}
