package ru.redw4y.HomeAccounting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(r -> r
						.requestMatchers("/users/login", "/users/registration").permitAll()
						.anyRequest().authenticated())
				.formLogin(f -> f
						.loginPage("/users/login")
						.loginProcessingUrl("/some_page")
						.defaultSuccessUrl("/main/outcome", true)
						.failureUrl("/users/login?error"))
				.logout(l -> l
						.logoutUrl("/logout")
						.logoutSuccessUrl("/users/login"))
				.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
