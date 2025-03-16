package com.sandrarujas.tarea6dwessandrarujas.repositorios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Pedido;

import jakarta.transaction.Transactional;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	

 

}
