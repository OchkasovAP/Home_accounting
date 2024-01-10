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
			@PathVariable("type") String typeName, Model model) {
		OperationType type = OperationType.getTypeFromName(typeName);
		List<CategoryDTO> categories = service.findAllByUser(userDetails.getUser().getId(), type)
				.stream()
				.map(this::convertCategory)
				.toList();
		model.addAttribute("categories",categories);
		model.addAttribute("type", typeName);
		return "/categories/showCategories";
	}
	
	@GetMapping("/new/{type}")
	public String createForm(@PathVariable("type") String type, @ModelAttribute("category") CategoryDTO category, Model model) {
		type = type.equals("income")?type:"outcome";
		model.addAttribute("type", type);
		return "/categories/createForm";
	}

	@GetMapping("/{type}/{id}")
	public String showCategoryInfo(@PathVariable("id") int id,
			@PathVariable("type") String typeName, @AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		OperationType type = OperationType.getTypeFromName(typeName);
		try {
			CategoryDTO category = convertCategory(service.findById(userDetails.getUser().getId(), id, type.getCategoryClass()));
			model.addAttribute("category", category);
			model.addAttribute("type", typeName);
			return "/categories/singleInfo";
		} catch (NullPointerException ex) {
			throw new HomeAccountingException("Категории с таким id не существует");
		}
	}

	@PostMapping("/{type}")
	public String addNewCategory(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable("type") String typeName,
			@ModelAttribute("category") @Valid CategoryDTO categoryDTO, BindingResult bindingResult) {
		Category category = convertDTO(categoryDTO, typeName);
		category.setUser(userDetails.getUser());
		validator.validate(category, bindingResult);
		if(bindingResult.hasErrors()) {
			return "categories/createForm";
		}
		service.create(userDetails.getUser().getId(), category);
		return "redirect:/categories/"+typeName;
	}

	@PatchMapping("/{type}")
	public String editCategory(@ModelAttribute("category") @Valid CategoryDTO categoryDTO, BindingResult bindingResult, @PathVariable("type") String typeName, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Category category = convertDTO(categoryDTO, typeName);
		category.setUser(userDetails.getUser());
		validator.validate(category, bindingResult);
		if(bindingResult.hasErrors()) {
			return "/categories/singleInfo";					
		}
		service.edit(category);
		return "redirect:/categories/"+typeName;
	}

	@DeleteMapping("/{type}/{id}")
	public String deleteCategory(@PathVariable("id") int categoryID, @PathVariable("type") String typeName,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		OperationType type = OperationType.getTypeFromName(typeName);
		service.remove(userDetails.getUser().getId(), categoryID, type);
		return "redirect:/categories/"+typeName;
	}

	private CategoryDTO convertCategory(Category category) {
		return modelMapper.map(category, CategoryDTO.class);
	}

	private Category convertDTO(CategoryDTO categoryDTO, String typeName) {
		Class<Category> categoryClass = OperationType.getTypeFromName(typeName).getCategoryClass();
		return modelMapper.map(categoryDTO, categoryClass);
	}
}
