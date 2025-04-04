package com.sandrarujas.tarea6dwessandrarujas.modelo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ejemplares")
public class Ejemplar implements Serializable {

	// Atributos
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private boolean disponible;

	@ManyToOne
	@JoinColumn(name = "idplanta")
	private Planta planta;

	@OneToMany(mappedBy = "ejemplar", cascade = CascadeType.ALL)
	private List<Mensaje> mensajes = new LinkedList<Mensaje>();

	@ManyToOne
	@JoinColumn(name = "id_pedido", nullable = true)
	private Pedido pedido;

	// Constructores

	public Ejemplar() {

	}

	public Ejemplar(String nombre, Planta planta) {
		this.nombre = nombre;
		this.planta = planta;
		this.disponible = true;
	}

	// Getters y Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Planta getPlanta() {
		return planta;
	}

	public void setPlanta(Planta planta) {
		this.planta = planta;
	}

	public List<Mensaje> getMensajes() {
		return mensajes;
	}

	public void setMensajes(List<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public boolean isDisponible() {
		return disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	// Métodos Equals y HashCode

	@Override
	public int hashCode() {
		return Objects.hash(id, nombre, planta);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ejemplar other = (Ejemplar) obj;
		return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(planta, other.planta);
	}

	// Método toString

	@Override
	public String toString() {
		return "Ejemplar ID: " + id + ", Nombre: " + nombre;
	}

}
