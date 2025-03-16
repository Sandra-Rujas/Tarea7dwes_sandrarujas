package com.sandrarujas.tarea6dwessandrarujas.controladores;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Cliente;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Credencial;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Persona;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Planta;
import com.sandrarujas.tarea6dwessandrarujas.servicios.Controlador;

import jakarta.servlet.http.HttpSession;

@Controller
public class ViveroInvitadoController {

	@Autowired
	Controlador controlador;

	/**
	 * Muestra la página de bienvenida del vivero invitado.
	 * 
	 * @param nombre El nombre a mostrar, por defecto es "Mundo".
	 * @param model  El objeto Model que se pasa a la vista.
	 * @return El nombre de la vista a mostrar ("ViveroInvitado").
	 */

	@GetMapping({ "/" })
	public String welcome() {
		return "ViveroInvitado";
	}

	/**
	 * Muestra el formulario de login.
	 * 
	 * @param error El mensaje de error, si existe.
	 * @param model El objeto Model que se pasa a la vista.
	 * @return El nombre de la vista de login.
	 */

	@GetMapping("/login")
	public String loginForm(@RequestParam(name = "error", required = false) String error, Model model) {
	    if (error != null) {
	        model.addAttribute("errorMessage", "Credenciales incorrectas, por favor intente nuevamente.");
	    }
	    return "login";
	}


	/**
	 * Procesa el formulario de login y autentica al usuario.
	 * 
	 * @param usuario  El nombre de usuario ingresado.
	 * @param password La contraseña ingresada.
	 * @param model    El objeto Model para pasar datos a la vista.
	 * @param session  La sesión HTTP donde se almacena la información del usuario
	 *                 autenticado.
	 * @return Redirige a la vista de administrador, personal o cliente según el ID
	 *         del usuario.
	 */
	@PostMapping("/login")
	public String login(@RequestParam("username") String usuario, @RequestParam("password") String password,
			Model model, HttpSession session) {
		System.out.println("Se ha recibido una solicitud de login");

		try {
			System.out.println("Intentando iniciar sesión para el usuario: " + usuario);

			Credencial credencial = controlador.getServiciosCredencial().buscarPorUsuario(usuario);

			if (credencial == null) {
				System.out.println("Usuario no encontrado en la base de datos: " + usuario);
				model.addAttribute("errorMessage", "El usuario no existe.");
				return "login"; 
			}

			System.out.println("Contraseña correcta, iniciando sesión...");
			session.setAttribute("usuarioAutenticado", usuario);
			session.setAttribute("userId", credencial.getPersona().getId());
			session.setAttribute("rol", credencial.getRol());

			// Redirigir según el rol
			if ("ADMIN".equalsIgnoreCase(credencial.getRol())) {
				System.out.println("Redirigiendo al área ADMIN");
				return "redirect:/ViveroAdmin";
			} else if ("PERSONAL".equalsIgnoreCase(credencial.getRol())) {
				System.out.println("Redirigiendo al área PERSONAL");
				return "redirect:/ViveroPersonal";
			} else if ("CLIENTE".equalsIgnoreCase(credencial.getRol())) {
				System.out.println("Redirigiendo al área CLIENTE");
				return "redirect:/ViveroCliente";
			} else {
				System.out.println("Rol no reconocido: " + credencial.getRol());
				model.addAttribute("errorMessage", "Rol no reconocido.");
				return "login"; // Redirige al formulario con mensaje de error
			}
		} catch (Exception e) {
			System.out.println("Error al intentar iniciar sesión: " + e.getMessage());
			model.addAttribute("errorMessage", "No se ha podido iniciar sesión: " + e.getMessage());
			return "login"; // Redirige al formulario con mensaje de error
		}
	}

	/**
	 * Muestra todas las plantas disponibles en el vivero.
	 * 
	 * @param model El objeto Model que se pasa a la vista.
	 * @return El nombre de la vista que muestra la lista de plantas.
	 */

	@GetMapping("/VerPlantas")
	public String mostrarPlantas(Model model, HttpSession session) {
		// Obtener el usuario autenticado desde Spring Security
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = null;
		String userRole = null;
		Integer userId = null;

		if (authentication != null && authentication.isAuthenticated()
				&& !(authentication.getPrincipal() instanceof String)) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserDetails) {
				username = ((UserDetails) principal).getUsername();
			}

			userId = controlador.getServiciosCredencial().obtenerUserIdPorUsername(username);
			userRole = controlador.getServiciosCredencial().obtenerUserRolePorUsername(username);

			session.setAttribute("userId", userId);
			session.setAttribute("userRole", userRole);
		}

		try {
			List<Planta> plantas = controlador.getServiciosPlanta().verPlantas();

			if (plantas == null || plantas.isEmpty()) {
				model.addAttribute("mensaje", "No hay plantas disponibles en la base de datos.");
			} else {
				model.addAttribute("plantas", plantas);
			}

			return "VerPlantas";
		} catch (Exception e) {
			model.addAttribute("mensaje", "Error al cargar las plantas: " + e.getMessage());
			return "VerPlantas";
		}
	}

	/**
	 * Cierra la aplicación de forma inmediata.
	 * 
	 * @return No devuelve un valor útil, ya que la aplicación se cierra antes de
	 *         ejecutar el retorno.
	 */
	@GetMapping("/salir")
	public String cerrarAplicacion() {
		System.exit(0);
		return "redirect:/";
	}

	@GetMapping("/CrearCliente")
	public String mostrarFormularioRegistro(Model model) {
		return "CrearCliente";
	}

	/**
	 * Registra una nueva persona en el sistema.
	 * 
	 * @param nombre     El nombre de la persona.
	 * @param email      El correo electrónico de la persona.
	 * @param usuario    El nombre de usuario de la persona.
	 * @param contraseña La contraseña de la persona.
	 * @param model      El modelo para la vista.
	 * @return El nombre de la vista después de registrar la persona.
	 */

	@PostMapping("/CrearCliente")
	public String registrarPersona(@RequestParam String nombre, @RequestParam String email,
	        @RequestParam String usuario, @RequestParam String contraseña, @RequestParam String nif,
	        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaNac, @RequestParam String telefono,
	        @RequestParam String direccion, Model model) {
	    try {
	        // Validación de email existente
	        if (controlador.getServiciosPersona().emailExistente(email)) {
	            model.addAttribute("mensajeError", "El email ya está registrado.");
	            return "CrearCliente";
	        }

	        // Validación de usuario existente o inválido
	        if (controlador.getServiciosCredencial().usuarioExistente(usuario) || usuario.length() < 3) {
	            model.addAttribute("mensajeError", "Usuario registrado o no válido.");
	            return "CrearCliente";
	        }

	        // Validación de usuario con espacios
	        if (usuario.contains(" ")) {
	            model.addAttribute("mensajeError", "Usuario con espacios.");
	            return "CrearCliente";
	        }

	        // Validación de contraseña
	        if (!controlador.getServiciosCredencial().validarPassword(contraseña)) {
	            model.addAttribute("mensajeError", "La contraseña debe tener entre 6 y 20 caracteres.");
	            return "CrearCliente";
	        }

	        // Validaciones de los datos del cliente
	        if (nombre == null || nombre.isEmpty()) {
	            model.addAttribute("mensajeError", "El nombre no puede estar vacío.");
	            return "CrearCliente";
	        }

	        if (email == null || email.isEmpty()) {
	            model.addAttribute("mensajeError", "El email no puede estar vacío.");
	            return "CrearCliente";
	        }
	        if (email.length() < 5 || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") || email.length() > 40) {
	            model.addAttribute("mensajeError", "Email no válido.");
	            return "CrearCliente";
	        }

	        if (fechaNac == null) {
	            model.addAttribute("mensajeError", "La fecha de nacimiento no puede estar vacía.");
	            return "CrearCliente";
	        }

	        if (nif == null || nif.isEmpty() || !nif.matches("[A-Za-z0-9]+")) {
	            model.addAttribute("mensajeError", "NIF/NIE no válido.");
	            return "CrearCliente";
	        }

	        if (direccion == null || direccion.isEmpty()) {
	            model.addAttribute("mensajeError", "La dirección no puede estar vacía.");
	            return "CrearCliente";
	        }

	        if (telefono == null || telefono.isEmpty() || !telefono.matches("[0-9]{9}")) {
	            model.addAttribute("mensajeError", "Teléfono no válido.");
	            return "CrearCliente";
	        }

	        Persona persona = new Persona();
	        persona.setNombre(nombre);
	        persona.setEmail(email);

	        Cliente cliente = new Cliente();
	        cliente.setNombre(nombre);
	        cliente.setNif(nif);
	        cliente.setFechanac(fechaNac);
	        cliente.setDireccion(direccion);
	        cliente.setTelefono(telefono);
	        cliente.setEmail(email);

	        controlador.getServiciosCliente().insertar(cliente);

	        Credencial credencial = new Credencial();
	        credencial.setUsuario(usuario);
	        credencial.setPassword(contraseña);
	        credencial.setPersona(persona);
	        credencial.setRol("CLIENTE");
	        persona.setCredencial(credencial);

	        if (!controlador.getServiciosPersona().validarPersona(persona)) {
	            model.addAttribute("mensajeError", "Los datos de la persona no son válidos.");
	            return "CrearCliente";
	        }

	        controlador.getServiciosPersona().insertar(persona);

	        model.addAttribute("mensajeExito", "Usuario registrado correctamente.");

	    } catch (Exception ex) {
	        model.addAttribute("mensajeError", "Error al registrar usuario: " + ex.getMessage());
	    }

	    return "CrearCliente";
	}


	@GetMapping("/redirect")
	public String redirectUser(Authentication authentication) {
		if (authentication != null) {
			for (GrantedAuthority auth : authentication.getAuthorities()) {
				if (auth.getAuthority().equals("ROLE_ADMIN")) {
					return "redirect:/ViveroAdmin";
				} else if (auth.getAuthority().equals("ROLE_CLIENTE")) {
					return "redirect:/ViveroCliente";
				} else if (auth.getAuthority().equals("ROLE_PERSONAL")) {
					return "redirect:/ViveroPersonal";
				}
			}
		}
		return "redirect:/";
	}
	
	@GetMapping("/403")
    public String accesoDenegado() {
        return "403"; 
    }

}
