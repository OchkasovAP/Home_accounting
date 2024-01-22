package ru.redw4y.HomeAccounting.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import ru.redw4y.HomeAccounting.dto.CategoryDTO;
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql (value = {"/CreateTableScript.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class CategoryControllerTest  {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	private CategoryDTO editedCategory;
	private CategoryDTO newCategory;
	private String[] types;
	@BeforeEach
	void setUp() {
		editedCategory = CategoryDTO.builder()
				.id(1)
				.name("TestCategory1")
				.build();
		newCategory = CategoryDTO.builder()
				.name("NewCategory")
				.build();
		types = new String[] {"income","outcome"};
	}
	
	@Test
	void showCategories_WithoutDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(get("/categories/{type}", type))
			.andExpect(status().isFound());
		}
	}
	@Test
	@WithUserDetails("user")
	void showCategories_UserDetails() throws Exception{
		for(String type:types) {
			mockMvc.perform(get("/categories/{type}", type))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(content().string(containsString("/categories/"+type+"/1")))
			.andExpect(content().string(containsString("/categories/"+type+"/2")))
			.andExpect(xpath("//div[@id='CategoriesList']/div").nodeCount(2))
			.andExpect(xpath("//a[@id='1']").string("TestCategory1"))
			.andExpect(xpath("//a[@id='2']").string("TestCategory2"));
		}
	}
	
	@Test
	void showCategoryInfo_WithoutDetails() throws Exception{
		for(String type:types) {
			mockMvc.perform(get("/categories/{type}/{id}", type, 1))
			.andExpect(status().isForbidden());
		}
	}
	
	@Test
	@WithUserDetails("user")
	void showCategoryInfo_UserDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(get("/categories/{type}/{id}", type, 1))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.name").value("TestCategory1"));
		}
	}
	
	@Test
	@WithUserDetails("user")
	void showCategoryInfo_NotExistsCategory() throws Exception {
		for(String type:types) {
			mockMvc.perform(get("/categories/{type}/{id}", type, 3))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.Exception").value("Категории с таким id не существует"));
		}
	}
	
	@Test
	@WithUserDetails("admin")
	void showCategoryInfo_AnotherUserDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(get("/categories/{type}/{id}", type, 1))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.Exception").value("Пользователь не обладает правами совершать это действие"));
		}
	}
	
	@Test
	void addNewCategory_WithoutDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(post("/categories/{type}", type)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(newCategory)))
			.andExpect(status().isForbidden());
		}
	}
	
	@Test
	@WithUserDetails("user")
	void addNewCategory_UserDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(post("/categories/{type}",type)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(newCategory)))
			.andExpect(status().isCreated());
			
			mockMvc.perform(get("/categories/{type}/{id}",type,3))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(3))
			.andExpect(jsonPath("$.name").value("NewCategory"));
		}
	}
	
	@Test
	@WithUserDetails("user")
	void addNewCategory_FailNameValid() throws Exception {
		for(String type:types) {
			newCategory.setName("TestCategory1");
			addNewCategory_FailNameValid(type, "Категория с таким именем уже существует");
			for(String emptyName:new String[] {null,""}) {
				newCategory.setName(emptyName);
				addNewCategory_FailNameValid(type, "Поле не должно быть пустым");
			}
			newCategory.setName("Very very very very large name");
			addNewCategory_FailNameValid(type, "Поле не должно превышать 20 символов");
		}
	}
	void addNewCategory_FailNameValid(String type, String exceptionMessage) throws Exception {
		mockMvc.perform(post("/categories/{type}", type)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCategory)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.Exception").value("Field - name, error - "+exceptionMessage+";"));
	}
	
	@Test
	@WithUserDetails("admin")
	void addNewCategory_ExistsNameAnotherUser() throws Exception{
		newCategory.setName("TestCategory1");
		for(String type:types) {
			mockMvc.perform(post("/categories/{type}", type)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(newCategory)))
			.andExpect(status().isCreated());
			
			mockMvc.perform(get("/categories/{type}/{id}",type,3))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(3))
			.andExpect(jsonPath("$.name").value("TestCategory1"));
		}
	}
	
	@Test
	void editCategory_WithoutDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(patch("/categories/{type}",type)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(editedCategory)))
			.andExpect(status().isForbidden());
		}
	}
	
	@Test
	@WithUserDetails("user")
	void editCategory_UserDetails() throws Exception {
		for(String type:types) {
			editCategory_UserDetails(type);
			editedCategory.setName("EditName");
			editCategory_UserDetails(type);
		}
	}
	
	void editCategory_UserDetails(String type) throws Exception {
		mockMvc.perform(patch("/categories/{type}",type)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editedCategory)))
		.andExpect(status().isOk());
		
		mockMvc.perform(get("/categories/{type}/{id}","income", editedCategory.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(editedCategory.getId()))
		.andExpect(jsonPath("$.name").value(editedCategory.getName()));
	}
	
	@Test
	@WithUserDetails("admin")
	void editCategory_AnotherUserDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(patch("/categories/{type}", type)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(editedCategory)))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.Exception").value("Пользователь не обладает правами совершать это действие"));
		}
	}
	
	@Test
	void deleteCategory_WithoutDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(delete("/categories/{type}/{id}", type, 2))
			.andExpect(status().isForbidden());
		}
	}
	
	@Test
	@WithUserDetails("user")
	void deleteCategory_UserDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(delete("/categories/{type}/{id}", type, 2))
			.andExpect(status().isNoContent());
			
			mockMvc.perform(get("/categories/{type}/{id}", type, 2))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.Exception").value("Категории с таким id не существует"));
		}
	}
	
	@Test
	@WithUserDetails("admin")
	void deleteCategory_AnotherUserDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(delete("/categories/{type}/{id}", type, 2))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.Exception").value("Пользователь не обладает правами совершать это действие"));
		}
	}
}
