package ru.redw4y.HomeAccounting.controllers;



import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.CategoriesService;
import ru.redw4y.HomeAccounting.util.Category;
import ru.redw4y.HomeAccounting.util.OperationType;


@Controller
@RequestMapping("/categories")
public class CategoryController {
	@Autowired
	private CategoriesService service;

	@GetMapping()
	public String showCategories(@AuthenticationPrincipal UserDetailsImpl userDetails, @ModelAttribute("type") String typeName,
			Model model) {
			OperationType type = OperationType.getTypeFromName(typeName);
			model.addAttribute("categories", service.findAllByUser(userDetails.getUser().getId(), type));
			model.addAttribute("type", type.name().toLowerCase());
			return "categories/showCategories";
	}

	@GetMapping("/{id}/{type}")
	public String getCategoryInfo(@PathVariable("id") int id, @PathVariable("type") String typeName, Model model) {
			OperationType type = OperationType.getTypeFromName(typeName);
			model.addAttribute("category", service.findById(id, type.getCategoryClass()));
			model.addAttribute("type", typeName.toLowerCase());
			return "categories/singleInfo";
	}

	@GetMapping("/new")
	public String createCategoryForm(@ModelAttribute("type") String typeName, Model model) {
		model.addAttribute("type", typeName.toLowerCase());
		return "categories/createForm";
	}

	@PatchMapping("/{id}")
	public String editCategory(@PathVariable("id") int id, @ModelAttribute("type") String typeName,
			@ModelAttribute("name") String name, Model model) {
			OperationType type = OperationType.getTypeFromName(typeName);
			service.edit(name, type, id);
			return "redirect:/categories?type=" + typeName;
	}

	@PostMapping()
	public String addNewCategory(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request, Model model) {
			String typeName = request.getParameter("type");
			String name = request.getParameter("name");
			System.out.println(name);
			Category category = OperationType.getTypeFromName(typeName).newCategory(name);
			service.create(userDetails.getUser().getId(), category);
			return "redirect:/categories?type=" + typeName;
	}

	@DeleteMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable("id") int categoryID, @ModelAttribute("type") String typeName,
			@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
			OperationType type = OperationType.getTypeFromName(typeName);
			service.remove(userDetails.getUser().getId(), categoryID, type);
			return "redirect:/categories?type=" + typeName;
	}
}
