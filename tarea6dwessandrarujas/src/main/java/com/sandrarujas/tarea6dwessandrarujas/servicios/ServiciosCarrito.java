package com.sandrarujas.tarea6dwessandrarujas.servicios;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Carrito;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Credencial;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Pedido;
import com.sandrarujas.tarea6dwessandrarujas.repositorios.CarritoRepository;
import com.sandrarujas.tarea6dwessandrarujas.repositorios.CredencialRepository;

@Service
public class ServiciosCarrito {

	@Autowired
	private CarritoRepository carritoRepository;

	@Autowired
	private CredencialRepository credencialRepository;

	public void guardarEnCarrito(Carrito carrito) {

		carritoRepository.save(carrito);
	}

	public List<Carrito> obtenerCarritoPorUsuario(String username) {
		// Buscar las credenciales del usuario
		Credencial credencial = credencialRepository.findByUsuario(username);

		if (credencial != null) {
			// Obtener los carritos asociados a las credenciales del usuario
			return carritoRepository.findByCredencial(credencial);
		}
		return new ArrayList<>();
	}

	public void eliminarCarritoPorUsuario(String username) {
	    // Buscar todos los carritos del usuario
	    List<Carrito> carrito = carritoRepository.findByCredencialUsuario(username);
	    
	    // Eliminar todos los elementos del carrito
	    if (carrito != null && !carrito.isEmpty()) {
	        carritoRepository.deleteAll(carrito);
	    }
	}
	public void eliminarPedido(List<Pedido> carrito, Long id) {
	    if (carrito != null) {
	        // Buscar el pedido con el id especificado y eliminarlo
	        for (Iterator<Pedido> iterator = carrito.iterator(); iterator.hasNext();) {
	            Pedido pedido = iterator.next();
	            if (pedido.getId().equals(id)) {
	                iterator.remove(); // Elimina el pedido del carrito
	                break;
	            }
	        }
	    }
	}
	
	


}
