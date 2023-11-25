package ru.redw4y.HomeAccounting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
@ComponentScan("ru.redw4y.HomeAccounting")
//@EnableWebMvc //Импортирует Spring MVC конфигурацию
public class SpringConfig {
//Создаем applicationContext и с помощью аннотации даем спрингу самому найти подходящий бин
	@Autowired
	private ApplicationContext applicationContext;
	
//Нижеизложенный код взят из документации Thymeleaf. Просто копируем как есть. Что можем менять, поясню
	
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(this.applicationContext);
		resolver.setPrefix("/WEB-INF/views/");//Здесь указываем путь до папки с нашими страницами, которые передаем клиенту
		resolver.setSuffix(".html");//Здесь указываем формат этих страниц. С thymeleaf отпадает нужда во всем кроме html
		resolver.setTemplateMode(TemplateMode.HTML);//Если ранне выбрали не html, то здесь тоже меняем
		resolver.setCharacterEncoding("UTF-8");
		resolver.setCacheable(true);//
		return resolver;
	}
	
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		return templateEngine;
	}
	
	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setCharacterEncoding("UTF-8");
		viewResolver.setTemplateEngine(templateEngine());
		return viewResolver;
	}
}
