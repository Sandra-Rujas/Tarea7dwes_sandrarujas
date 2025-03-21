package com.sandrarujas.tarea6dwessandrarujas.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sandrarujas.tarea6dwessandrarujas.modelo.Ejemplar;
import com.sandrarujas.tarea6dwessandrarujas.modelo.Planta;

import jakarta.transaction.Transactional;


@Repository
public interface EjemplarRepository extends JpaRepository <Ejemplar, Long>{
	
	/*Método que nos permite a través de una consulta mostrar los ejemplares que hay en la base de datos con dicho código*/
	 @Query("SELECT e FROM Ejemplar e WHERE e.planta.codigo = :codigoPlanta")
	 List<Ejemplar> ejemplaresPorPlanta(@Param("codigoPlanta") String codigoPlanta);

	 /*Método que a través de una consulta nos permite modificar el nombre de la planta según el Id. 
	  * El @ Transactional se utiliza para todas aquellas consultas que nos permiten modificar un dato*/
	@Transactional
    @Modifying
    @Query("UPDATE Ejemplar e SET e.nombre = :nuevoNombre WHERE e.id = :idEjemplar")
    int editarNombre(@Param("idEjemplar") Long idEjemplar, @Param("nuevoNombre") String nuevoNombre);
	
	List<Ejemplar> findByPlanta(Optional<Planta> planta);
	
	// Encuentra los ejemplares de una planta ordenados por ID (para eliminar los más antiguos primero)
    List<Ejemplar> findByPlantaIdOrderByIdAsc(Long plantaId);
    
    
    /*
     * Cuenta la cantidad de ejemplares disponibles de una planta específica.
     * @param codigoPlanta Código de la planta.
     * @return Número de ejemplares disponibles.
     */
    @Query("SELECT COUNT(e) FROM Ejemplar e WHERE e.planta.codigo = :codigoPlanta AND e.disponible = true")
    int contarPorPlantaDisponible(@Param("codigoPlanta") String codigoPlanta);
    
    /*
     * Cuenta el total de ejemplares de una planta específica.
     * @param codigoPlanta Código de la planta.
     * @return Número total de ejemplares.
     */
    int countByPlantaCodigo(String codigoPlanta);
    
    /*
     * Elimina un número específico de ejemplares de la base de datos asociados a una planta dada.
     * @param id El identificador de la planta cuya colección de ejemplares se desea modificar.
     * @param cantidad El número de ejemplares a eliminar.
     * @return El número de filas afectadas por la eliminación. 
     */
    
    @Modifying
    @Transactional
    @Query(
      value = "DELETE FROM ejemplares WHERE id IN ( " +
              "  SELECT id FROM ( " +
              "    SELECT id FROM ejemplares " +
              "    WHERE idplanta = :id " +
              "    ORDER BY id ASC LIMIT :cantidad " +
              "  ) AS temp " +
              ")",
      nativeQuery = true
    )
    
    
    /*
     * Elimina una cantidad específica de ejemplares disponibles de una planta.
     * @param id       ID de la planta.
     * @param cantidad Cantidad de ejemplares a eliminar.
     * @return Número de ejemplares eliminados.
     */
    
    int eliminarEjemplaresDisponibles(@Param("id") Long id, @Param("cantidad") int cantidad);

    @Modifying
    @Transactional
    @Query(
        value = "DELETE FROM mensajes WHERE id_ejemplar IN ( " +
                "  SELECT id FROM ( " +
                "    SELECT id FROM ejemplares " +
                "    WHERE idplanta = :id " +
                "    ORDER BY id ASC LIMIT :cantidad " +
                "  ) AS temp " +
                ")",
        nativeQuery = true
    )
    
    
    /*
     * Elimina los mensajes asociados a una cantidad específica de ejemplares de una planta.
     * @param idPlanta ID de la planta.
     * @param cantidad Cantidad de ejemplares cuyos mensajes serán eliminados.
     * @return Número de mensajes eliminados.
     */
    
    int eliminarMensajesDeEjemplares(@Param("id") Long idPlanta, @Param("cantidad") int cantidad);
    
    
    /*
     * Obtiene un ejemplar asociado a una planta específica.
     * @param codigoPlanta Código de la planta.
     * @return Un ejemplar de la planta.
     */
    
    @Query("SELECT e FROM Ejemplar e WHERE e.planta.codigo = :codigoPlanta ORDER BY e.id ASC LIMIT 1")
    Ejemplar ejemplarPorPlanta(@Param("codigoPlanta") String codigoPlanta);
    
    
 // 1️⃣ Obtener los primeros "cantidad" ejemplares disponibles
    @Query("SELECT e.id FROM Ejemplar e WHERE e.planta.id = :plantaId AND e.disponible = true ORDER BY e.id ASC LIMIT :cantidad")
    List<Long> obtenerEjemplaresDisponibles(@Param("plantaId") Long plantaId, @Param("cantidad") int cantidad);

    // 2️⃣ Marcar como no disponibles solo los ejemplares seleccionados
    @Modifying
    @Query("UPDATE Ejemplar e SET e.disponible = false WHERE e.id IN :ids")
    void cambiarDisponibilidadAFalse(@Param("ids") List<Long> ids);
    
    @Modifying
    @Query("UPDATE Ejemplar e SET e.pedido.id = :pedidoId WHERE e.id = :ejemplarId")
    void asignarPedidoAEjemplar(@Param("ejemplarId") Long ejemplarId, @Param("pedidoId") Long pedidoId);
    
    @Modifying
    @Query("UPDATE Ejemplar e SET e.pedido.id = :pedidoId WHERE e.id IN :ejemplarIds")
    void asignarPedidoAEjemplares(@Param("pedidoId") Long pedidoId, @Param("ejemplarIds") List<Long> ejemplarIds);

    @Query("SELECT e.id FROM Ejemplar e WHERE e.planta.codigo = :codigoPlanta AND e.disponible = true ORDER BY e.id ASC LIMIT :cantidad")
    List<Long> obtenerIdsEjemplaresDisponibles(@Param("codigoPlanta") String codigoPlanta, @Param("cantidad") int cantidad);



}

