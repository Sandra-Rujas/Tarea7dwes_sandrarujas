package com.sandrarujas.tarea6dwessandrarujas.servicios;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Credencial;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Persona;
import com.sandrarujas.tarea6dwessandrarujas.repositorios.CredencialRepository;
import com.sandrarujas.tarea6dwessandrarujas.repositorios.PersonaRepository;

import jakarta.transaction.Transactional;

@Service
public class ServiciosCredencial {
	
	@Autowired
	CredencialRepository credencialRepository;
	
	@Autowired
	PersonaRepository personaRepository;
	
	
	/*
     * Método para comprobar un usuario por su contraseña y nombre.
     * @param usuario Usuario
     * @param password Contraseña 
     * @return true si las credenciales son correctas, false si no.
     */
	 public boolean autenticar(String usuario, String password) {
	    return credencialRepository.existsByUsuarioAndPassword(usuario, password);
	    }

    /*
     * Método para comprobar si el usuario ya existe.
     * @param usuario Usuario.
     * @return true si el usuario existe.
     */
	 
    public boolean usuarioExistente(String usuario) {
        return credencialRepository.existsByUsuario(usuario);
    }

    /*
     * Método para insertar un nuevo registro.
     * @param Usuario.
     * @param Contraseña.
     * @param id de la persona.
     */
    public void insertar(String usuario, String password, Long idPersona) {
        Persona p = personaRepository.findById(idPersona).orElse(null);
        Credencial credencial = new Credencial();
        credencial.setUsuario(usuario);
        credencial.setPassword(password);
        credencial.setPersona(p);
        credencialRepository.save(credencial);
    }

    /*
     * Método para validar la contraseña.
     * @param password Contraseña .
     * @return true si la contraseña es válida
     */
    
    public boolean validarPassword(String password) {
        return password.matches("^[A-Za-z0-9]{6,20}$");
    }
    
    /*
     * Método para encontrar el Id del User por su nombre de Usuario.
     * @param username Nombre usuario.
     * @return 1 si el id del admin, 2 todos los demás.
     */
    public Integer obtenerUserIdPorUsername(String username) {
        Credencial credencial = credencialRepository.findByUsuario(username);
        if (credencial != null && credencial.getPersona() != null) {
            return credencial.getPersona().getId().intValue();
        }
        return null;
    }
    
    /**
     * Buscar las credenciales de un usuario por su nombre de usuario.
     * @param usuario El nombre de usuario.
     * @return Las credenciales del usuario o null si no se encuentra.
     */
    public Credencial buscarPorUsuario(String usuario) {
        return credencialRepository.findByUsuario(usuario);
    }
    
    public String obtenerUserRolePorUsername(String username) {
        return credencialRepository.findByUsuario(username).getRol(); // O ajusta según tu entidad de usuario
    }

 
}