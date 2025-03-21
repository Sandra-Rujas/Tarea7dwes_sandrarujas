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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Cliente;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Credencial;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Ejemplar;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Mensaje;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Pedido;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Persona;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Planta;
import com.sandrarujas.tarea6dwessandrarujas.servicios.Controlador;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
public class ViveroClienteController {

	@Autowired
	Controlador controlador;

	@GetMapping("/ViveroCliente")
	public String mostrarMenuPedidos() {
		return "ViveroCliente";
	}

	/*
	 * Muestra la disponibilidad de ejemplares para una planta específica. Si se
	 * proporciona un código de planta, muestra la cantidad disponible de
	 * ejemplares.
	 * 
	 * @param codigoPlanta El código de la planta cuya disponibilidad se quiere
	 * consultar.
	 * 
	 * @param model El modelo de la vista que contiene los datos a mostrar.
	 * 
	 * @return El nombre de la vista que se renderizará.
	 */

	@GetMapping("/ComprarEjemplar")
	public String mostrarDisponibilidad(@RequestParam(name = "codigoPlanta", required = false) String codigoPlanta,
			Model model, Authentication authentication) {
		List<Planta> plantas = controlador.getServiciosPlanta().verPlantas();
		model.addAttribute("plantas", plantas);

		if (codigoPlanta != null && !codigoPlanta.isEmpty()) {
			int cantidadDisponible = controlador.getServiciosEjemplar().contarEjemplaresDisponibles(codigoPlanta);
			model.addAttribute("cantidadDisponible", cantidadDisponible);
			model.addAttribute("codigoPlanta", codigoPlanta);
		}

		return "ComprarEjemplar";
	}

	/*
	 * Permite realizar la compra de ejemplares de una planta específica. Verifica
	 * que la cantidad solicitada no exceda la cantidad disponible.
	 * 
	 * @param codigoPlanta El código de la planta que se va a comprar.
	 * 
	 * @param cantidad La cantidad de ejemplares que se desean comprar.
	 * 
	 * @param session La sesión HTTP que almacena los datos del carrito.
	 * 
	 * @param model El modelo de la vista que contiene los datos a mostrar.
	 * 
	 * @param authentication La autenticación del usuario que realiza la compra.
	 * 
	 * @param redirectAttributes Los atributos para redirigir con mensajes flash.
	 * 
	 * @return La redirección a la vista de compra de ejemplares.
	 */

	@PostMapping("/ComprarEjemplar")
	public String comprarEjemplar(@RequestParam String codigoPlanta, @RequestParam int cantidad, HttpSession session,
			Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
		Planta planta = controlador.getServiciosPlanta().buscarPorCodigo(codigoPlanta);
		String nombre = authentication.getName();

		if (planta != null) {
			int cantidadDisponible = controlador.getServiciosEjemplar().contarEjemplaresDisponibles(codigoPlanta);

			if (cantidad > cantidadDisponible) {
				redirectAttributes.addFlashAttribute("mensaje", "No hay suficientes ejemplares disponibles.");
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

				redirectAttributes.addFlashAttribute("mensaje", "Producto añadido al carrito.");
			}
		}

		return "redirect:/ComprarEjemplar";
	}

	/*
	 * Muestra los pedidos actuales en el carrito. Si hay un mensaje de error o
	 * confirmación, lo muestra al usuario.
	 * 
	 * @param session La sesión HTTP que almacena los datos del carrito.
	 * 
	 * @param model El modelo de la vista que contiene los datos a mostrar.
	 * 
	 * @return La vista con los pedidos en el carrito.
	 */

	@GetMapping("/VerPedido")
	public String verPedidos(HttpSession session, Model model) {
		List<Pedido> carrito = (List<Pedido>) session.getAttribute("carrito");
		String mensaje = (String) session.getAttribute("mensaje");

		if (mensaje != null) {
			model.addAttribute("mensaje", mensaje);
			session.removeAttribute("mensaje"); // Limpiar después de mostrarlo
		}

		model.addAttribute("carrito", carrito != null ? carrito : new ArrayList<>());

		return "VerPedido";
	}

	/*
	 * Confirma la compra de los pedidos en el carrito. Verifica la disponibilidad
	 * de los ejemplares y procesa cada pedido.
	 * 
	 * @param session La sesión HTTP que almacena los datos del carrito.
	 * 
	 * @param model El modelo de la vista que contiene los datos a mostrar.
	 * 
	 * @param authentication La autenticación del usuario que realiza la compra.
	 * 
	 * @return La redirección a la vista de pedidos con el mensaje de confirmación.
	 */

	@PostMapping("/VerPedido")
	public String confirmarCompra(HttpSession session, Model model, Authentication authentication) {
		List<Pedido> carrito = (List<Pedido>) session.getAttribute("carrito");
		String nombre = authentication.getName();
		long id = controlador.getServiciosCredencial().obtenerUserIdPorUsername(nombre);
		Credencial credencial = controlador.getServiciosCredencial().buscarPorUsuario(nombre);

		if (carrito == null || carrito.isEmpty()) {
			session.setAttribute("mensaje", "No hay pedidos para confirmar.");
			return "redirect:/VerPedido";
		}

		for (Pedido pedido : carrito) {
			Cliente cliente = controlador.getServiciosCliente().obtenerClientePorCredencial(credencial.getId());
			pedido.setCliente(cliente);

			String codigoPlanta = pedido.getPlanta().getCodigo();
			int cantidad = pedido.getCantidad();

			int cantidadDisponible = controlador.getServiciosEjemplar().contarEjemplaresDisponibles(codigoPlanta);

			if (cantidad > cantidadDisponible) {
				session.setAttribute("mensaje", "Error: No hay suficientes ejemplares de la planta " + codigoPlanta);
				return "redirect:/VerPedido";
			}

			controlador.getServiciosPedido().crearPedido(pedido, pedido.getPlanta(), cantidad);
			controlador.getServiciosEjemplar().asignarPedidoAMultiplesEjemplares(pedido.getId(), codigoPlanta,
					cantidad);

			for (int i = 0; i < cantidad; i++) {
				Ejemplar ejemplar = controlador.getServiciosEjemplar().ejemplarPorPlanta(codigoPlanta);

				if (ejemplar == null) {
					session.setAttribute("mensaje",
							"Error: No se pudo encontrar suficientes ejemplares para " + codigoPlanta);
					return "redirect:/VerPedido";
				}

				controlador.getServiciosEjemplar().asignarPedido(ejemplar.getId(), pedido.getId());
				String mensajeTexto = "El cliente " + nombre + " ha comprado: " + cantidad + " ejemplar de "
						+ ejemplar.getNombre() + " a las: " + LocalDateTime.now() + " del pedido: " + pedido.getId();
				LocalDateTime fechaHora = LocalDateTime.now();

				Mensaje mensaje = new Mensaje(fechaHora, mensajeTexto,
						controlador.getServiciosPersona().buscarNombrePorId(id), ejemplar);
				System.out.println(mensaje);

				controlador.getServiciosMensaje().addMensaje(mensaje);
			}
			controlador.getServiciosEjemplar().eliminarEjemplares(pedido.getPlanta().getId(), cantidad);
		}

		session.removeAttribute("carrito");
		session.setAttribute("mensaje", "¡Compra confirmada con éxito!");

		return "redirect:/VerPedido";
	}

	/*
	 * Elimina un pedido específico del carrito.
	 * 
	 * @param pedidoIndex El índice del pedido a eliminar.
	 * 
	 * @param session La sesión HTTP que almacena los datos del carrito.
	 * 
	 * @param model El modelo de la vista que contiene los datos a mostrar.
	 * 
	 * @return La redirección a la vista de pedidos con el mensaje correspondiente.
	 */

	@PostMapping("/VerPedido/eliminarPedido")
	public String eliminarPedido(@RequestParam("pedidoIndex") int pedidoIndex, HttpSession session, Model model) {
		List<Pedido> carrito = (List<Pedido>) session.getAttribute("carrito");

		if (carrito != null && !carrito.isEmpty() && pedidoIndex >= 0 && pedidoIndex < carrito.size()) {
			carrito.remove(pedidoIndex);

			session.setAttribute("carrito", carrito);

			session.setAttribute("mensaje", "Pedido eliminado correctamente.");
		} else {
			session.setAttribute("mensaje", "No se pudo eliminar el pedido.");
		}

		return "redirect:/VerPedido";
	}

	/*
	 * Elimina todos los pedidos del carrito.
	 * 
	 * @param session La sesión HTTP que almacena los datos del carrito.
	 * 
	 * @return La redirección a la vista de pedidos con el mensaje correspondiente.
	 */

	@PostMapping("/VerPedido/eliminarTodosPedidos")
	public String eliminarTodosPedidos(HttpSession session) {
		session.removeAttribute("carrito");

		session.setAttribute("mensaje", "Todos los pedidos han sido eliminados correctamente.");

		return "redirect:/VerPedido";
	}

}
