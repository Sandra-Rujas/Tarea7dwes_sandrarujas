package com.sandrarujas.tarea6dwessandrarujas.controladores;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Credencial;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Ejemplar;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Mensaje;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Persona;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Planta;
import com.sandrarujas.tarea6dwessandrarujas.servicios.Controlador;

import jakarta.servlet.http.HttpSession;

@Controller
public class ViveroAdminController {

	@Autowired
	Controlador controlador;

	/*
	 * Muestra el menú principal de administración.
	 * @param model El modelo para la vista.
	 * @return El nombre de la vista a mostrar.
	 */

	@GetMapping("/ViveroAdmin")
	public String mostrarMenuAdmin(Model model) {
		return "ViveroAdmin";
	}

	// GESTIÓN PERSONAS
	/*
	 * Muestra el formulario para crear una nueva persona.
	 * @param model El modelo para la vista.
	 * @return El nombre de la vista para crear una persona.
	 */

	@GetMapping("/CrearPersona")
	public String mostrarFormularioRegistro(Model model) {
		return "CrearPersona";
	}

	/*
	 * Registra una nueva persona en el sistema.
	 * @param nombre     El nombre de la persona.
	 * @param email      El correo electrónico de la persona.
	 * @param usuario    El nombre de usuario de la persona.
	 * @param contraseña La contraseña de la persona.
	 * @param model      El modelo para la vista.
	 * @return El nombre de la vista después de registrar la persona.
	 */

	@PostMapping("/CrearPersona")
	public String registrarPersona(@RequestParam String nombre, @RequestParam String email,
			@RequestParam String usuario, @RequestParam String contraseña, @RequestParam String rol, // Nuevo parámetro
																										// para el rol
			Model model) {
		try {
			if (controlador.getServiciosPersona().emailExistente(email)) {
				model.addAttribute("mensajeError", "El email ya está registrado.");
				return "CrearPersona";
			}

			if (controlador.getServiciosCredencial().usuarioExistente(usuario) || usuario.length() < 3) {
				model.addAttribute("mensajeError", "Usuario registrado o no válido.");
				return "CrearPersona";
			}

			if (usuario.contains(" ")) {
				model.addAttribute("mensajeError", "Usuario con espacios.");
				return "CrearPersona";
			}
			if (nombre.contains(" ")) {
				model.addAttribute("mensajeError", "Nombre con espacios.");
				return "CrearPersona";
			}
			if (email.contains(" ")) {
				model.addAttribute("mensajeError", "Email con espacios.");
				return "CrearPersona";
			}

			if (contraseña.contains(" ")) {
				model.addAttribute("mensajeError", "Contraseña con espacios.");
				return "CrearPersona";
			}

			if (!controlador.getServiciosCredencial().validarPassword(contraseña)) {
				model.addAttribute("mensajeError", "La contraseña debe tener entre 6 y 20 caracteres.");
				return "CrearPersona";
			}

			Persona persona = new Persona();
			persona.setNombre(nombre);
			persona.setEmail(email);

			Credencial credencial = new Credencial();
			credencial.setUsuario(usuario);
			credencial.setPassword(contraseña);
			credencial.setPersona(persona);
			persona.setCredencial(credencial);
			credencial.setRol(rol);

			if (!controlador.getServiciosPersona().validarPersona(persona)) {
				model.addAttribute("mensajeError", "Los datos no son válidos.");
				return "CrearPersona";
			}

			controlador.getServiciosPersona().insertar(persona);
			model.addAttribute("mensajeExito", "Usuario registrado correctamente.");

		} catch (Exception ex) {
			model.addAttribute("mensajeError", "Error al registrar usuario: " + ex.getMessage());
		}

		return "CrearPersona";
	}

	/*
	 * Muestra todas las personas registradas.
	 * @param model El modelo para la vista.
	 * @return El nombre de la vista con las personas.
	 */

	@GetMapping("/VerPersonas")
	public String mostrarPersonas(Model model) {
		ArrayList<Persona> personas = (ArrayList<Persona>) controlador.getServiciosPersona().totalPersonas();

		if (personas == null || personas.isEmpty()) {
			model.addAttribute("personas", new ArrayList<>());
		} else {
			for (Persona persona : personas) {

				persona.getCredenciales().getRol();

			}
			model.addAttribute("personas", personas);
		}

		return "VerPersonas";
	}

	// GESTIÓN PLANTAS

	/*
	 * Muestra el formulario para crear una nueva planta.
	 * @return El nombre de la vista para crear una planta.
	 */

	@GetMapping("/CrearPlanta")
	public String crearPlanta() {
		return "CrearPlanta";
	}

	/*
	 * Registra una nueva planta en el sistema.
	 * @param codigo           El código de la planta.
	 * @param nombreComun      El nombre común de la planta.
	 * @param nombreCientifico El nombre científico de la planta.
	 * @param model            El modelo para la vista.
	 * @return El nombre de la vista después de registrar la planta.
	 */

	@PostMapping("/CrearPlanta")
	public String crearPlanta(@RequestParam("codigo") String codigo, @RequestParam("nombreComun") String nombreComun,
			@RequestParam("nombreCientifico") String nombreCientifico, Model model) {
		try {
			codigo = codigo.trim().toUpperCase();
			nombreComun = nombreComun.trim();
			nombreCientifico = nombreCientifico.trim();

			boolean correcto = controlador.getServiciosPlanta().validarCodigoPlanta(codigo);
			boolean existe = controlador.getServiciosPlanta().codigoExistente(codigo);

			if (!correcto) {
				model.addAttribute("mensajeError", "El formato del código no es correcto.");
				return "CrearPlanta";
			}

			if (existe) {
				model.addAttribute("mensajeError", "Código de planta ya existente.");
				return "CrearPlanta";
			}

			Planta nuevaPlanta = new Planta();
			nuevaPlanta.setCodigo(codigo);
			nuevaPlanta.setNombreComun(nombreComun);
			nuevaPlanta.setNombreCientifico(nombreCientifico);

			boolean datosPlanta = controlador.getServiciosPlanta().validarPlanta(nuevaPlanta);
			if (!datosPlanta) {
				model.addAttribute("mensajeError", "Los datos que has introducido no son correctos.");
				return "CrearPlanta";
			}

			controlador.getServiciosPlanta().insertar(nuevaPlanta);

			model.addAttribute("mensajeExito", "Planta registrada exitosamente.");
			return "CrearPlanta";

		} catch (Exception e) {
			model.addAttribute("mensajeError", "Error al registrar la planta: " + e.getMessage());
			return "CrearPlanta";
		}
	}

	/*
	 * Muestra el formulario para modificar una planta.
	 * @param model  El modelo para la vista.
	 * @param opcion La opción seleccionada para modificar.
	 * @return El nombre de la vista para modificar la planta.
	 */
	@GetMapping("/ModificarPlanta")
	public String mostrarMenuModificarPlanta(Model model,
			@RequestParam(name = "codigo", required = false) String codigo,
			@RequestParam(name = "opcion", required = false) Integer opcion) {

		List<Planta> plantas = controlador.getServiciosPlanta().verPlantas();
		model.addAttribute("plantas", plantas);

		if (codigo != null && opcion != null) {
			model.addAttribute("codigo", codigo);
			model.addAttribute("opcion", opcion);
		}

		return "ModificarPlanta";
	}

	/*
	 * Método para actualizar los datos de una planta según la opción seleccionada.
	 * @param codigo           Código de la planta a modificar.
	 * @param nombreComun      Nuevo nombre común de la planta (opcional).
	 * @param nombreCientifico Nuevo nombre científico de la planta (opcional).
	 * @param opcion           Opción de actualización (1 para nombre común, 2 para nombre científico).
	 * @param model            Modelo de Spring para pasar atributos a la vista.
	 * @return Redirección a la página de modificación de plantas.
	 */

	@PostMapping("/ModificarPlanta")
	public String actualizarPlanta(@RequestParam("codigo") String codigo,
			@RequestParam(name = "nombreComun", required = false) String nombreComun,
			@RequestParam(name = "nombreCientifico", required = false) String nombreCientifico,
			@RequestParam("opcion") Integer opcion, Model model) {
		try {
			boolean valido = controlador.getServiciosPlanta().validarCodigoPlanta(codigo);
			if (!valido) {
				model.addAttribute("mensaje", "El código de la planta no es válido.");
				return "ModificarPlanta";
			}

			boolean existe = controlador.getServiciosPlanta().codigoExistente(codigo);
			if (!existe) {
				model.addAttribute("mensaje", "El código de la planta no existe en la base de datos.");
				return "ModificarPlanta";
			}

			boolean actualizado = false;
			if (opcion == 1 && nombreComun != null) {
				actualizado = controlador.getServiciosPlanta().actualizarNombreComun(codigo, nombreComun);
				if (actualizado) {
					model.addAttribute("mensajeExito", "El nombre común de la planta ha sido actualizado.");
				} else {
					model.addAttribute("mensaje", "Error al actualizar el nombre común.");
				}
			} else if (opcion == 2 && nombreCientifico != null) {
				actualizado = controlador.getServiciosPlanta().actualizarNombreCientifico(codigo, nombreCientifico);
				if (actualizado) {
					model.addAttribute("mensajeExito", "El nombre científico de la planta ha sido actualizado.");
				} else {
					model.addAttribute("mensaje", "Error al actualizar el nombre científico.");
				}
			} else {
				model.addAttribute("mensaje", "Opción no válida.");
			}
		} catch (Exception e) {
			model.addAttribute("mensaje", "Error al intentar actualizar la planta: " + e.getMessage());
		}

		return "redirect:/ModificarPlanta";
	}

	// GESTIÓN MENSAJES
	/*
	 * Muestra el formulario para crear un nuevo mensaje.
	 * @param model El modelo para la vista.
	 * @return El nombre de la vista para crear un mensaje.
	 */
	@GetMapping("/CrearMensaje")
	public String mostrarFormularioCrearMensaje(Model model, HttpSession session) {
		// Obtener el usuario autenticado desde Spring Security
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return "redirect:/login"; // Redirige si no hay usuario autenticado
		}

		Object principal = authentication.getPrincipal();
		String username = null;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}

		Integer userId = controlador.getServiciosCredencial().obtenerUserIdPorUsername(username);
		String userRole = controlador.getServiciosCredencial().obtenerUserRolePorUsername(username);

		session.setAttribute("userId", userId);
		session.setAttribute("userRole", userRole);

		List<Ejemplar> ejemplares = (List<Ejemplar>) controlador.getServiciosEjemplar().verEjemplares();
		if (ejemplares == null || ejemplares.isEmpty()) {
			model.addAttribute("mensajeError", "No hay ejemplares disponibles.");
		} else {
			model.addAttribute("ejemplares", ejemplares);
		}

		return "CrearMensaje";
	}

	/*
	 * Selecciona un ejemplar por su ID y lo agrega al modelo para su uso en la vista.
	 * @param idEjemplar ID del ejemplar a seleccionar.
	 * @param model      Modelo de Spring para pasar atributos a la vista.
	 * @param session    Sesión HTTP para manejar datos del usuario.
	 * @return Nombre de la vista "CrearMensaje".
	 */
	@PostMapping("/SeleccionarEjemplar")
	public String seleccionarEjemplar(@RequestParam Long idEjemplar, Model model, HttpSession session) {
		Ejemplar ejemplar = controlador.getServiciosEjemplar().buscarPorID(idEjemplar);
		if (ejemplar == null) {
			model.addAttribute("mensajeError", "No se encontró un ejemplar con ese ID.");
			return "CrearMensaje";
		}

		model.addAttribute("ejemplarSeleccionado", ejemplar);
		List<Ejemplar> ejemplares = (List<Ejemplar>) controlador.getServiciosEjemplar().verEjemplares();
		model.addAttribute("ejemplares", ejemplares);

		return "CrearMensaje";
	}

	/*
	 * Crea un nuevo mensaje asociado a un ejemplar, validando la autenticación del usuario.
	 * @param idEjemplar  ID del ejemplar al que se asociará el mensaje.
	 * @param mensajeTexto Contenido del mensaje a crear.
	 * @param model       Modelo de Spring para pasar atributos a la vista.
	 * @param session     Sesión HTTP para manejar datos del usuario.
	 * @return Nombre de la vista "CrearMensaje".
	 */
	@PostMapping("/CrearMensaje")
	public String crearMensaje(@RequestParam Long idEjemplar, @RequestParam String mensajeTexto, Model model,
			HttpSession session) {
		boolean exito = false;

		if (mensajeTexto != null && !mensajeTexto.trim().isEmpty()) {
			try {
				Ejemplar ejemplar = controlador.getServiciosEjemplar().buscarPorID(idEjemplar);
				if (ejemplar != null) {
					Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
					String usuarioAutenticado = authentication.getName();
					Persona p = controlador.getServiciosPersona().buscarPorNombre(usuarioAutenticado);
					if (p == null) {
						model.addAttribute("mensajeError", "No se ha encontrado la persona autenticada.");
					} else {
						Mensaje nuevoMensaje = new Mensaje(LocalDateTime.now(), mensajeTexto, p, ejemplar);
						controlador.getServiciosMensaje().addMensaje(nuevoMensaje);
						exito = true;
					}
				} else {
					model.addAttribute("mensajeError", "No se encontró un ejemplar con ese ID.");
				}
			} catch (Exception e) {
				model.addAttribute("mensajeError", "Error al crear el mensaje: " + e.getMessage());
			}
		} else {
			model.addAttribute("mensajeError", "El mensaje no puede estar vacío.");
		}

		if (exito) {
			model.addAttribute("mensajeExito", "Mensaje añadido con éxito.");
		}

		List<Ejemplar> ejemplares = (List<Ejemplar>) controlador.getServiciosEjemplar().verEjemplares();
		model.addAttribute("ejemplares", ejemplares);

		return "CrearMensaje";
	}

}
