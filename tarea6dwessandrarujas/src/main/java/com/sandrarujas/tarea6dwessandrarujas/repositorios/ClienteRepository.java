package com.sandrarujas.tarea6dwessandrarujas.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Cliente;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	 Optional<Cliente> findByNombre(String nombre);
}
