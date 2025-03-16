package com.sandrarujas.tarea6dwessandrarujas.config;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Credencial;
import com.sandrarujas.tarea6dwessandrarujas.repositorios.CredencialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private CredencialRepository credencialRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("🔍 Buscando usuario en la BD: " + username);
		Credencial credencial = credencialRepository.findByUsuario(username);

		if (credencial == null) {
			System.out.println("❌ Usuario no encontrado en la BD");
			throw new UsernameNotFoundException("Usuario no encontrado");
		}

		System.out.println("✅ Usuario encontrado: " + credencial.getUsuario());
		System.out.println("🔑 Contraseña en BD: " + credencial.getPassword());
		System.out.println("🎭 Rol encontrado: " + credencial.getRol());

		// Aseguramos que el rol se guarde correctamente
		String role = "ROLE_" + credencial.getRol().toUpperCase();

		return new User(credencial.getUsuario(), credencial.getPassword(),
				Collections.singletonList(new SimpleGrantedAuthority(role)));
	}
}
