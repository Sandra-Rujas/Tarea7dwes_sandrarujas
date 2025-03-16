package com.sandrarujas.tarea6dwessandrarujas.controladores;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Cliente;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Ejemplar;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Mensaje;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Pedido;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Persona;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Planta;
import com.sandrarujas.tarea6dwessandrarujas.servicios.Controlador;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ViveroClienteController {

	@Autowired
	Controlador controlador;


	@GetMapping("/ViveroCliente")
	public String mostrarMenuPedidos() {
		return "ViveroCliente";
	}

	@GetMapping("/ComprarEjemplar")
	public String mostrarDisponibilidad(@RequestParam(name = "codigoPlanta", required = false) String codigoPlanta,
			Model model) {
		List<Planta> plantas = controlador.getServiciosPlanta().verPlantas(); 
		model.addAttribute("plantas", plantas);

		if (codigoPlanta != null && !codigoPlanta.isEmpty()) {
			int cantidadDisponible = controlador.getServiciosEjemplar().contarEjemplaresDisponibles(codigoPlanta);
			model.addAttribute("cantidadDisponible", cantidadDisponible);
			model.addAttribute("codigoPlanta", codigoPlanta); 
		}

		return "ComprarEjemplar"; 
	}

	@PostMapping("/ComprarEjemplar")
	public String comprarEjemplar(@RequestParam String codigoPlanta, @RequestParam int cantidad, HttpSession session,
			HttpServletResponse response, Model model, Authentication authentication) {
		Planta planta = controlador.getServiciosPlanta().buscarPorCodigo(codigoPlanta);
		System.out.println("Planta seleccionada: " + planta.getCodigo()); // Debugging: verificar que la planta se está
																			// recuperando correctamente

		if (planta != null) {
			int cantidadDisponible = controlador.getServiciosEjemplar().contarEjemplaresDisponibles(codigoPlanta);
			System.out.println("Cantidad disponible de la planta: " + cantidadDisponible); // Debugging: verificar
																							// cantidad disponible

			if (cantidad > cantidadDisponible) {
				model.addAttribute("mensaje", "Error: No hay suficientes ejemplares disponibles.");
			} else {
				Pedido pedido = new Pedido();
				pedido.setPlanta(planta);
				pedido.setCantidad(cantidad);
				pedido.setFecha(new Date());

				List<Pedido> carrito = (List<Pedido>) session.getAttribute("carrito");
				if (carrito == null) {
					carrito = new ArrayList<>();
				}
				carrito.add(pedido);
				session.setAttribute("carrito", carrito);

				String nombre = authentication.getName();
				Optional<Cliente> clienteOpt = controlador.getServiciosCliente().encontrarCliente(nombre);
				if (clienteOpt.isPresent()) {
					Cliente cliente = clienteOpt.get();
					System.out.println("Cliente autenticado: " + cliente.getNombre());
					List<Long> productos = new ArrayList<>();
					for (Pedido p : carrito) {
						productos.add(p.getPlanta().getId());
						System.out.println("Producto añadido al carrito: " + p.getPlanta().getCodigo() + " Cantidad: "
								+ p.getCantidad());
					}

				}

				model.addAttribute("mensaje", "Producto añadido al carrito.");
			}
		}

		List<Planta> plantas = controlador.getServiciosPlanta().verPlantas();
		model.addAttribute("plantas", plantas);
		return "ComprarEjemplar";
	}

	@GetMapping("/VerPedido")
	public String verPedidos(HttpSession session, Model model, Authentication authentication) {
		List<Pedido> carrito = (List<Pedido>) session.getAttribute("carrito");

		if (carrito == null || carrito.isEmpty()) {
			model.addAttribute("mensaje", "No tienes productos en tu carrito.");
		} else {
			model.addAttribute("carrito", carrito); // Asegúrate de que el carrito no esté vacío
		}

		return "VerPedido";
	}

	@PostMapping("/VerPedido")
	public String confirmarCompra(HttpSession session, HttpServletResponse response, Model model,
			Authentication authentication) {
		List<Pedido> carrito = (List<Pedido>) session.getAttribute("carrito");
		String nombre = authentication.getName();
		Optional<Cliente> clienteOpt = controlador.getServiciosCliente().encontrarCliente(nombre);

		if (carrito == null || carrito.isEmpty()) {
			model.addAttribute("mensaje", "No hay pedidos para confirmar.");
			return "VerPedido";
		}

		for (Pedido pedido : carrito) {
			String codigoPlanta = pedido.getPlanta().getCodigo();
			int cantidad = pedido.getCantidad();

			controlador.getServiciosPedido().crearPedido(pedido, pedido.getPlanta(), cantidad);
			Persona persona = controlador.getServiciosPersona().buscarPorNombre(nombre);

			controlador.getServiciosEjemplar().eliminarEjemplares(pedido.getPlanta().getId(), cantidad);

			String mensajeTexto = "El cliente " + persona.getNombre() + " ha comprado " + cantidad + " ejemplares de "
					+ pedido.getPlanta().getCodigo() + " a las: " + LocalDateTime.now();
			LocalDateTime fechaHora = LocalDateTime.now();
			Ejemplar ejemplar = controlador.getServiciosEjemplar().ejemplarPorPlanta(codigoPlanta);

			controlador.getServiciosEjemplar().insertar(ejemplar);
			Mensaje mensaje = new Mensaje(fechaHora, mensajeTexto, persona, ejemplar);

			controlador.getServiciosMensaje().addMensaje(mensaje);
		}

		model.addAttribute("mensaje", "Compra confirmada y ejemplares eliminados del inventario.");

		return "redirect:/VerPedido";
	}
}
