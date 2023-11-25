package ru.redw4y.HomeAccounting.controllers;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.redw4y.HomeAccounting.entity.Role;
import ru.redw4y.HomeAccounting.entity.User;
import ru.redw4y.HomeAccounting.entityUtil.Operation;

@Controller
@RequestMapping(value="/test", produces = "application/json; charset=utf-8")
public class TestController {
	@GetMapping("/1")
	public String test1(Model model, @ModelAttribute("user") User user, @RequestParam("name") String name) {
		System.out.println(name);
		List<String> numbers = Arrays.asList("1 2 3 4 5 6 7 8 9 10".split(" "));
		model.addAttribute("numbers", numbers);
		return "test/testView";
	}
	@PostMapping(value = "/2", produces = "application/json; charset=UTF-8") 
	public String test2(@ModelAttribute("user") User user) {
		System.out.println(user);
		return "test/testView";
	}

}
