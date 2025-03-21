package com.sandrarujas.tarea6dwessandrarujas.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Cliente;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Ejemplar;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Persona;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Planta;
import com.sandrarujas.tarea6dwessandrarujas.repositorios.ClienteRepository;
import com.sandrarujas.tarea6dwessandrarujas.repositorios.EjemplarRepository;
import com.sandrarujas.tarea6dwessandrarujas.repositorios.PlantaRepository;

@Service
public class ServiciosCliente {

	@Autowired
	ClienteRepository clienteRepository;

	@Autowired
	private EjemplarRepository ejemplarRepository;

	@Autowired
	private PlantaRepository plantaRepository;

	/*
	 * Método insertar cliente.
	 * 
	 * @param cliente. El cliete que queremos añadir.
	 */
	public void insertar(Cliente cliente) {
		clienteRepository.saveAndFlush(cliente);
	}

	/*
	 * Procesa la compra de ejemplares para una planta específica.
	 * 
	 * @param codigoPlanta Código de la planta.
	 * 
	 * @param cantidad Cantidad de ejemplares a comprar.
	 * 
	 * @return true si la compra se realizó correctamente; false en caso contrario.
	 */
	public boolean comprarEjemplares(Long plantaId, int cantidad) {
		List<Ejemplar> ejemplares = ejemplarRepository.findByPlantaIdOrderByIdAsc(plantaId);

		if (ejemplares.size() < cantidad) {
			return false; // No hay suficientes ejemplares para comprar
		}

		for (int i = 0; i < cantidad; i++) {
			ejemplarRepository.delete(ejemplares.get(i));
		}
		return true;
	}

	/*
	 * Encuentra un cliente por su nombre.
	 * 
	 * @param nombre Nombre del cliente.
	 * 
	 * @return Un Optional con el cliente encontrado, si existe.
	 */
	public Optional<Cliente> encontrarCliente(String nombre) {
		return clienteRepository.findByNombre(nombre);
	}

	/*
	 * Verifica si un cliente existe basado en su nombre, email o NIF.
	 * 
	 * @param nombre Nombre del cliente.
	 * 
	 * @param email Email del cliente.
	 * 
	 * @param nif NIF del cliente.
	 * 
	 * @return true si el cliente existe, false en caso contrario.
	 */
	public boolean clienteExistente(String nombre, String email, String nif) {
		return clienteRepository.existsByEmailOrNif(email, nif);
	}

	/*
	 * Valida los datos de un cliente.
	 * 
	 * @param cliente Cliente a validar.
	 * 
	 * @return true si el cliente es válido, false en caso contrario.
	 */
	public boolean validarCliente(Cliente cliente) {
		if (cliente == null) {
			return false;
		}
		// Validar nombre
		if (cliente.getNombre() == null || cliente.getNombre().isEmpty()) {
			return false;
		}

		// Validar email
		if (cliente.getEmail() == null || cliente.getEmail().isEmpty()) {
			return false;
		}
		if (cliente.getEmail().length() < 5 || !cliente.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
				|| cliente.getEmail().length() > 40) {
			return false;
		}

		// Validar fecha de nacimiento (suponiendo que sea un formato adecuado)
		if (cliente.getFechanac() == null) {
			return false;
		}

		// Validar NIF/NIE
		if (cliente.getNif() == null || cliente.getNif().isEmpty() || !cliente.getNif().matches("[A-Za-z0-9]+")) {
			return false;
		}

		// Validar dirección
		if (cliente.getDireccion() == null || cliente.getDireccion().isEmpty()) {
			return false;
		}

		// Validar teléfono (por ejemplo, que tenga 9 caracteres numéricos)
		if (cliente.getTelefono() == null || cliente.getTelefono().isEmpty()
				|| !cliente.getTelefono().matches("[0-9]{9}")) {
			return false;
		}

		return true;
	}

	public Cliente buscarClientePorId(Long id) {
		return clienteRepository.findById(id).orElse(null);
	}
	
	public Cliente buscarClientePorNombre(String nombre) {
		return clienteRepository.findByNombre(nombre).orElse(null);
	}
	
    // Método para obtener cliente por su credencial_id
    public Cliente obtenerClientePorCredencial(Long credencialId) {
        return clienteRepository.findByCredencialId(credencialId);
    }

}
