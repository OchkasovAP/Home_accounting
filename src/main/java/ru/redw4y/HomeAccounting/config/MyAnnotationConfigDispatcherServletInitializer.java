package ru.redw4y.HomeAccounting.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


public class MyAnnotationConfigDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
//Эти 3 метода IDE сама предложит создать. Без их переопределения будет ошибка
//Настройку начинаем с создание SpringConfig файла. Добавляем его во второй метод
//и в третий метод добавляем маппинг(ссылку) по умолчанию.
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] {SpringConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] {"/"};
	}
//---------------------------------------------------------------------------------------------
	//Так как HTML не поддерживает методы PATCH и DELETE, то мы должны создать скрытое поле 
	//<input type="hidden" name="_method" value="patch"> для PATCH и value="delete" для DELETE, 
	//и чтобы контроллер отработал по аннотации @PatchMapping или @DeleteMapping,
	//необходимо установить в конфигурационном файле данный фильтр
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		 registerCharacterEncodingFilter(servletContext);
	        registerHiddenFieldFilter(servletContext);
	}
	/**
	 * @param servletContext
	 */
	private void registerHiddenFieldFilter(ServletContext aContext) {
        aContext.addFilter("hiddenHttpMethodFilter",
                new HiddenHttpMethodFilter()).addMappingForUrlPatterns(null, true, "/*");
    }

    private void registerCharacterEncodingFilter(ServletContext aContext) {
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);

        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        FilterRegistration.Dynamic characterEncoding = aContext.addFilter("characterEncoding", characterEncodingFilter);
        characterEncoding.addMappingForUrlPatterns(dispatcherTypes, true, "/*");
    }
//---------------------------------------------------------------------------------------------
}
