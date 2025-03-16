package com.sandrarujas.tarea6dwessandrarujas.servicios;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Ejemplar;
import com.sandrarujas.tarea6dwessandrarujas.repositorios.EjemplarRepository;

import jakarta.transaction.Transactional;


@Service
public class ServiciosEjemplar {

    @Autowired
    private EjemplarRepository ejemplarRepository;

    /*Método insertar ejemplar*/
    
    public void insertar(Ejemplar ejemplar) {
    	ejemplarRepository.save(ejemplar);
    }

    /*
     * Método para obtener todos los ejemplares en una colección.
     * @return Colección con todos los ejemplares.
     */
    
    public Collection<Ejemplar> verEjemplares() {
        return ejemplarRepository.findAll();
    }

    /*
     * Método para obtener un ejemplar a través de un id.
     * @param id ID del ejemplar 
     * @return objeto ejemplar.
     */
    
    public Ejemplar buscarPorID(long id) {
        Optional<Ejemplar> ejemplares = ejemplarRepository.findById(id);
        return ejemplares.orElse(null);
    }
    
    
    /*
     * Método para cambiar el nombre de un ejemplar.
     * @param idEjemplar id del ejemplar a modificar
     * @param nombre Nuevo nombre para el ejemplar
     * @return true si se ha modificado alguna fila.
     */
    
    @Transactional
    public boolean cambiarNombre(long idEjemplar, String nombre) {
        int filas = ejemplarRepository.editarNombre(idEjemplar, nombre);
        return filas > 0;
    }
   
    
    /*
     * Método para obtener los ejemplares con un tipo de planta concreto.
     * @param codigo Codigo de la planta
     * @return arraylist con todos los ejemplares con ese tipo de planta.
     */
    
    public ArrayList<Ejemplar> ejemplaresPorTipoPlanta(String codigo) {
        List<Ejemplar> ejemplares = ejemplarRepository.ejemplaresPorPlanta(codigo);
        return new ArrayList<>(ejemplares);
    }
    
    
    public int contarEjemplaresDisponibles(String codigoPlanta) {
        return ejemplarRepository.contarPorPlantaDisponible(codigoPlanta);
    }
    
    public int contarEjemplaresPorPlanta(String codigoPlanta) {
        // Suponiendo que el repositorio tiene una consulta para contar ejemplares por planta
        return ejemplarRepository.countByPlantaCodigo(codigoPlanta);
    }
    
    
    @Transactional
    public void eliminarEjemplares(Long id, int cantidad) {
    	
    	int eliminadosMensajes = ejemplarRepository.eliminarMensajesDeEjemplares(id, cantidad);
        System.out.println("Se eliminaron " + eliminadosMensajes + " mensajes.");
        
        int eliminados = ejemplarRepository.eliminarEjemplaresDisponibles(id, cantidad);
        System.out.println("Se eliminaron " + eliminados + " ejemplares.");
    }
    
    public Ejemplar ejemplarPorPlanta (String codigoPlanta) {
    	return ejemplarRepository.ejemplarPorPlanta(codigoPlanta);
    }
    
   
    
}



