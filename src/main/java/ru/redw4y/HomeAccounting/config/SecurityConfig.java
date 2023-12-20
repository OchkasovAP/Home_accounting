package ru.redw4y.HomeAccounting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(r -> r
						.requestMatchers("/users").hasAnyRole("ADMIN")
						.requestMatchers("/users/autorization", "/users/registration").permitAll()
						.anyRequest().authenticated())
				.formLogin(f -> f
						.loginPage("/users/autorization")
						.loginProcessingUrl("/some_page")
						.defaultSuccessUrl("/", true)
						.failureUrl("/users/autorization?error"))
				.logout(l -> l
						.logoutUrl("/logout")
						.logoutSuccessUrl("/users/autorization"))
				.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
