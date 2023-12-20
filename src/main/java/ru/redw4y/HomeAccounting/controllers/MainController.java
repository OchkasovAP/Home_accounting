package ru.redw4y.HomeAccounting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.MainService;
import ru.redw4y.HomeAccounting.util.MainViewModel;


@Controller
public class MainController {
	@Autowired
	private MainService mainService;
	
	@GetMapping()
	public String mainOperationsView(Model model,
			@ModelAttribute("viewModel") MainViewModel viewModel, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		model.addAllAttributes(mainService.mainPageAttributes(viewModel, userDetails.getUser()));
		return "main/homepage"; 
	}

	@GetMapping("menu")
	public String showMenu(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
 		model.addAttribute("user", userDetails.getUser());
		return "main/mainMenu"; 
	}
}
