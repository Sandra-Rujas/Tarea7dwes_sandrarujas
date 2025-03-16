package com.sandrarujas.tarea6dwessandrarujas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/VerPlantas", "/CrearCliente", "/login", "/CSS/**", "/Images/**").permitAll()
				.requestMatchers("/ViveroAdmin").hasRole("ADMIN").requestMatchers("/ViveroCliente").hasRole("CLIENTE")
				.requestMatchers("/ViveroPersonal").hasRole("PERSONAL").anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
						.defaultSuccessUrl("/redirect", true).permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())
				.exceptionHandling(exception -> exception.accessDeniedPage("/login"));

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http, UserDetailsService userDetailsService)
			throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService)
				.passwordEncoder(org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance()).and()
				.build();
	}
}
