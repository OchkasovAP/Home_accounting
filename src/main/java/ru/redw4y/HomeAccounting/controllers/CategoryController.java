package ru.redw4y.HomeAccounting.controllers;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import ru.redw4y.HomeAccounting.dao.CategoriesDAO;
import ru.redw4y.HomeAccounting.entity.IncomeCategory;
import ru.redw4y.HomeAccounting.entity.OutcomeCategory;
import ru.redw4y.HomeAccounting.entityUtil.Category;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;


@Controller
@RequestMapping("/categories")
public class CategoryController {
	@Autowired
	private CategoriesDAO categoriesDAO;

	@GetMapping()
	public String showCategories(@SessionAttribute("userID") int userID, @RequestParam("type") String typeName,
			Model model) {
			OperationType type = OperationType.getTypeFromName(typeName);
			model.addAttribute("categories", categoriesDAO.getCategoryList(userID, type));
			model.addAttribute("type", type.name().toLowerCase());
			return "categories/showCategories";
	}

	@GetMapping("/{id}/{type}")
	public String getCategoryInfo(@PathVariable("id") int id, @PathVariable("type") String typeName, Model model) {
			OperationType type = OperationType.getTypeFromName(typeName);
			model.addAttribute("category", categoriesDAO.getItem(id, type.getCategoryClass()));
			model.addAttribute("type", typeName.toLowerCase());
			return "categories/singleInfo";
	}

	@GetMapping("/new")
	public String createCategoryForm(@RequestParam("type") String typeName, Model model) {
		model.addAttribute("type", typeName.toLowerCase());
		return "categories/createForm";
	}

	@PatchMapping("/{id}")
	public String editCategory(HttpSession session, @PathVariable("id") int id, @RequestParam("type") String typeName,
			@RequestParam("name") String name, Model model) {
			OperationType type = OperationType.getTypeFromName(typeName);
			categoriesDAO.editCategory(name, type, id);
			return "redirect:/categories?type=" + typeName;
	}

	@PostMapping()
	public String addNewCategory(@SessionAttribute("userID") int userID, HttpServletRequest request, Model model) {
			String typeName = request.getParameter("type");
			String name = request.getParameter("name");
			System.out.println(name);
			Category category = OperationType.getTypeFromName(typeName).newCategory(name);
			categoriesDAO.addCategory(userID, category);
			return "redirect:/categories?type=" + typeName;
	}

	@DeleteMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable("id") int categoryID, @RequestParam("type") String typeName,
			@SessionAttribute("userID") int userID, Model model) {
			OperationType type = OperationType.getTypeFromName(typeName);
			categoriesDAO.removeCategory(userID, categoryID, type);
			return "redirect:/categories?type=" + typeName;
	}
}
