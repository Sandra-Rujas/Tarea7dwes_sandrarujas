package com.sandrarujas.tarea6dwessandrarujas.repositorios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Pedido;

import jakarta.transaction.Transactional;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	
	/*
     * Busca un pedido por el código de la planta y la cantidad especificada.
     * @param codigoPlanta Código de la planta.
     * @param cantidad     Cantidad del pedido.
     * @return El pedido encontrado o null si no existe.
     */
    Pedido findByPlanta_CodigoAndCantidad(String codigoPlanta, int cantidad);


}
