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

	
	/*
	 * Configura los filtros de seguridad para las solicitudes HTTP.
	 * Define las reglas de autorización para diferentes rutas, incluyendo el acceso a las páginas públicas, 
	 * los roles requeridos para ciertas rutas y el manejo de la autenticación y el cierre de sesión.
	 * @param http El objeto HttpSecurity utilizado para configurar las reglas de seguridad HTTP.
	 * @return El objeto SecurityFilterChain configurado para aplicar las reglas de seguridad.
	 * @throws Exception Si ocurre algún error al configurar la seguridad HTTP.
	 */
	
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

	
	/*
	 * Devuelve una instancia del servicio que maneja la carga de detalles de usuario.
	 * Este servicio es utilizado por Spring Security para obtener la información del usuario 
	 * durante el proceso de autenticación.
	 * @return El servicio de detalles de usuario que implementa la lógica de carga de usuario.
	 */
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	
	/*
	 * Configura el gestor de autenticación para la seguridad HTTP.
	 * Establece el servicio de detalles de usuario y el codificador de contraseñas para el proceso de autenticación.
	 * @param http El objeto HttpSecurity utilizado para configurar la seguridad HTTP.
	 * @param userDetailsService El servicio que proporciona los detalles del usuario.
	 * @return El AuthenticationManager configurado para manejar la autenticación de los usuarios.
	 * @throws Exception Si ocurre algún error al configurar el gestor de autenticación.
	 */
	
	@Bean
	public AuthenticationManager authManager(HttpSecurity http, UserDetailsService userDetailsService)
			throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService)
				.passwordEncoder(org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance()).and()
				.build();
	}
}
