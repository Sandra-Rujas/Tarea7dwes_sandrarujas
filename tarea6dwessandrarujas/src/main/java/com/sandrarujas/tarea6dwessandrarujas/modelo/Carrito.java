package com.sandrarujas.tarea6dwessandrarujas.modelo;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "carritos")
public class Carrito implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_ejemplar", nullable = false)
    private String nombreEjemplar;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    // Relación ManyToOne con la clase Credencial
    @ManyToOne
    @JoinColumn(name = "credencial_id", nullable = false) // Se referencia al campo ID de la tabla 'credenciales'
    private Credencial credencial;  // Relación con Credencial en lugar de usar un campo de tipo String

    // Constructor vacío
    public Carrito() {
    }

    // Constructor con parámetros
    public Carrito(String nombreEjemplar, int cantidad, Credencial credencial) {
        this.nombreEjemplar = nombreEjemplar;
        this.cantidad = cantidad;
        this.credencial = credencial;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEjemplar() {
        return nombreEjemplar;
    }

    public void setNombreEjemplar(String nombreEjemplar) {
        this.nombreEjemplar = nombreEjemplar;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Credencial getCredencial() {
        return credencial;
    }

    public void setCredencial(Credencial credencial) {
        this.credencial = credencial;
    }
}
