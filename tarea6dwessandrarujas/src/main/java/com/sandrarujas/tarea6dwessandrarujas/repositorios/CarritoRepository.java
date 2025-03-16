package com.sandrarujas.tarea6dwessandrarujas.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Carrito;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Credencial;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
	
    List<Carrito> findByCredencial(Credencial credencial);

    void deleteByCredencial(Credencial credencial);
   
    List<Carrito> findByCredencialUsuario(String username);    

}
