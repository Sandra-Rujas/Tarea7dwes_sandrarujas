package com.sandrarujas.tarea6dwessandrarujas.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Cliente;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	/*
     * Busca un cliente por su nombre.
     * @param nombre Nombre del cliente.
     * @return Un Optional que contiene el cliente si se encuentra.
     */
	 Optional<Cliente> findByNombre(String nombre);

	 
	 /*
	  * Verifica si existe un cliente con el correo electrónico o NIF especificado.
	  * @param email Correo electrónico del cliente.
	  * @param nif   NIF del cliente.
	  * @return true si existe un cliente con el email o NIF, false en caso contrario.
	  */
	boolean existsByEmailOrNif(String email, String nif);
	
    Cliente findByCredencialId(Long credencialId);

	
}
