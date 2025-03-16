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
	
    @Transactional
    public void guardarPedido(Pedido pedido) {
        pedidoRepository.save(pedido);
    }
    

    public void crearPedido(Pedido pedido, Planta planta, int cantidad) {
        pedido.setPlanta(planta);
        pedido.setCantidad(cantidad); 
        pedido.setFecha(new Date());
        pedidoRepository.save(pedido);
    }

}
