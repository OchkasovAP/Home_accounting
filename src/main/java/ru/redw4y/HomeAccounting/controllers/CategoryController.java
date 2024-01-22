package ru.redw4y.HomeAccounting.controllers;

import jakarta.validation.Valid;
import ru.redw4y.HomeAccounting.dto.CategoryDTO;
import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.CategoriesService;
import ru.redw4y.HomeAccounting.util.Category;
import ru.redw4y.HomeAccounting.util.OperationType;
import ru.redw4y.HomeAccounting.util.exceptions.CategoryNotValidException;
import ru.redw4y.HomeAccounting.util.exceptions.HomeAccountingException;
import ru.redw4y.HomeAccounting.util.validators.CategoryValidator;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/categories")
public class CategoryController extends AbstractHomeAccountingController{

	private final CategoriesService service;
	private final CategoryValidator validator;
	private final ModelMapper modelMapper;

	@Autowired
	public CategoryController(CategoriesService service, CategoryValidator validator, ModelMapper modelMapper) {
		super();
		this.service = service;
		this.validator = validator;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/{type}")
	public String showCategories(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@PathVariable("type") OperationType type, Model model) {
		List<CategoryDTO> categories = service.findAllByUser(userDetails.getUser().getId(), type)
				.stream()
				.map(this::convertCategory)
				.toList();
		model.addAttribute("categories",categories);
		model.addAttribute("type", type.name().toLowerCase());
		return "/categories/showCategories";
	}
	
	@GetMapping("/{type}/new")
	public String createForm(@PathVariable("type") OperationType type, @ModelAttribute("category") CategoryDTO category, Model model) {
		model.addAttribute("type", type.name().toLowerCase());
		return "/categories/createForm";
	}

	@GetMapping("/{type}/{id}")
	public String showCategoryInfo(@PathVariable("id") int id,
			@PathVariable("type") OperationType type, @AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		try {
			CategoryDTO category = convertCategory(service.findById(userDetails.getUser().getId(), id, type.getCategoryClass()));
			model.addAttribute("category", category);
			model.addAttribute("type", type.name().toLowerCase());
			return "/categories/singleInfo";
		} catch (NullPointerException ex) {
			throw new HomeAccountingException("Категории с таким id не существует");
		}
	}

	@PostMapping("/{type}")
	public String addNewCategory(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable("type") OperationType type,
			@ModelAttribute("category") @Valid CategoryDTO categoryDTO, BindingResult bindingResult, Model model) {
		Category category = convertDTO(categoryDTO, type);
		category.setUser(userDetails.getUser());
		validator.validate(category, bindingResult);
		if(bindingResult.hasErrors()) {
			model.addAttribute("type", type.name().toLowerCase());
			return "categories/createForm";
		}
		service.create(userDetails.getUser().getId(), category);
		return "redirect:/categories/"+type.name().toLowerCase();
	}

	@PatchMapping("/{type}")
	public String editCategory(@ModelAttribute("category") @Valid CategoryDTO categoryDTO, BindingResult bindingResult, @PathVariable("type") OperationType type, @AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		Category category = convertDTO(categoryDTO, type);
		category.setUser(userDetails.getUser());
		validator.validate(category, bindingResult);
		if(bindingResult.hasErrors()) {
			model.addAttribute("type", type.name().toLowerCase());
			return "/categories/singleInfo";					
		}
		service.edit(category);
		return "redirect:/categories/"+type.name().toLowerCase();
	}

	@DeleteMapping("/{type}/{id}")
	public String deleteCategory(@PathVariable("id") int categoryID, @PathVariable("type") OperationType type,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		service.remove(userDetails.getUser().getId(), categoryID, type);
		return "redirect:/categories/"+type.name().toLowerCase();
	}

	private CategoryDTO convertCategory(Category category) {
		return modelMapper.map(category, CategoryDTO.class);
	}

	private Category convertDTO(CategoryDTO categoryDTO, OperationType type) {
		Class<Category> categoryClass = type.getCategoryClass();
		return modelMapper.map(categoryDTO, categoryClass);
	}
}
