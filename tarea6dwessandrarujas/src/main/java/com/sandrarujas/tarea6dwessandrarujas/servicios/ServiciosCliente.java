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
	
	/*Método insertar cliente.
	 * @param cliente. El cliete que queremos añadir.
	 */
    public void insertar(Cliente cliente) {
    	clienteRepository.saveAndFlush(cliente);
    }

    

    /**
     * Procesa la compra de ejemplares para una planta específica.
     * @param codigoPlanta Código de la planta.
     * @param cantidad Cantidad de ejemplares a comprar.
     * @return true si la compra se realizó correctamente; false en caso contrario.
     */
    public boolean comprarEjemplares(Long plantaId, int cantidad) {
        // Buscar los ejemplares disponibles de la planta seleccionada (ordenados del más antiguo al más nuevo)
        List<Ejemplar> ejemplares = ejemplarRepository.findByPlantaIdOrderByIdAsc(plantaId);

        // Verificamos si hay suficientes ejemplares disponibles
        if (ejemplares.size() < cantidad) {
            return false; // No hay suficientes ejemplares para comprar
        }

        // Eliminamos los primeros "cantidad" ejemplares
        for (int i = 0; i < cantidad; i++) {
            ejemplarRepository.delete(ejemplares.get(i));
        }
        return true;
    }
    
    public Optional<Cliente> encontrarCliente(String nombre) {
    	return clienteRepository.findByNombre(nombre);
    }
    
    
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
        if (cliente.getEmail().length() < 5 || !cliente.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") || cliente.getEmail().length() > 40) {
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
        if (cliente.getTelefono() == null || cliente.getTelefono().isEmpty() || !cliente.getTelefono().matches("[0-9]{9}")) {
            return false;
        }    

        return true;
    }

}
