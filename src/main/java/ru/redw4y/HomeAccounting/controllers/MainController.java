package ru.redw4y.HomeAccounting.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.redw4y.HomeAccounting.dto.MainViewDTO;
import ru.redw4y.HomeAccounting.dto.OperationDTO;
import ru.redw4y.HomeAccounting.dto.OperationFilterDTO;
import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.security.UserDetailsImpl;
import ru.redw4y.HomeAccounting.services.HomePageService;
import ru.redw4y.HomeAccounting.util.DateRange;
import ru.redw4y.HomeAccounting.util.Operation;
import ru.redw4y.HomeAccounting.util.OperationFilter;
import ru.redw4y.HomeAccounting.util.OperationType;


@Controller
@RequestMapping("/")
public class MainController {
	private final HomePageService homePageService;
	private final ModelMapper modelMapper;
	
	public MainController(HomePageService homePageService, ModelMapper modelMapper) {
		super();
		this.homePageService = homePageService;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/main/{type}")
	public String mainOperationsView(@PathVariable("type") OperationType type, Model model,
			@ModelAttribute("filter") OperationFilterDTO operationFilterDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		OperationFilter filter = convertFilterDTO(operationFilterDTO, type);
		model.addAllAttributes(homePageService.mainPageAttributes(filter, userDetails.getUser()));
		return "main/homepage"; 
	}

	@GetMapping("menu")
	public String showMenu(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
 		model.addAttribute("user", userDetails.getUser());
		return "main/mainMenu"; 
	}

	private OperationFilter convertFilterDTO(OperationFilterDTO filterDTO, OperationType type) {
		OperationFilter filter = modelMapper.map(filterDTO, OperationFilter.class);
		filter.setType(type);
		return filter;
	}
}
