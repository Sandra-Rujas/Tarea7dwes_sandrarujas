package com.sandrarujas.tarea6dwessandrarujas.servicios;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Pedido;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Planta;
import com.sandrarujas.tarea6dwessandrarujas.repositorios.PedidoRepository;

import jakarta.transaction.Transactional;

@Service
public class ServiciosPedido {

	@Autowired
	PedidoRepository pedidoRepository;
	
	/*
     * Guarda un pedido en la base de datos.
     * @param pedido El pedido a guardar.
     */
    @Transactional
    public void guardarPedido(Pedido pedido) {
        pedidoRepository.save(pedido);
    }
    
    /*
     * Crea y guarda un nuevo pedido con la planta y cantidad especificadas.
     * @param pedido   El objeto pedido a actualizar.
     * @param planta   La planta asociada al pedido.
     * @param cantidad La cantidad de plantas pedidas.
     */
    public void crearPedido(Pedido pedido, Planta planta, int cantidad) {
        pedido.setPlanta(planta);
        pedido.setCantidad(cantidad); 
        pedido.setFecha(new Date());
        pedidoRepository.save(pedido);
    }
    
    /*
     * Cancela un pedido basado en el código de la planta y la cantidad.
     * @param codigoPlanta El código de la planta del pedido a cancelar.
     * @param cantidad     La cantidad de plantas en el pedido.
     * @throws IllegalArgumentException Si el pedido no se encuentra.
     */
    @Transactional
    public void cancelarPedido(String codigoPlanta, int cantidad) {
        Pedido pedido = pedidoRepository.findByPlanta_CodigoAndCantidad(codigoPlanta, cantidad);

        if (pedido != null) {
            pedidoRepository.delete(pedido);
        } else {
            throw new IllegalArgumentException("Pedido no encontrado");
        }
    }

}
