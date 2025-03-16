package com.sandrarujas.tarea6dwessandrarujas.modelo;


import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

	// Atributos
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50, nullable = false)
	private String nombre;

	@Column(nullable = false)
	private Date fechanac;

	@Column(length = 10, nullable = false)
	private String nif;

	@Column(length = 500, nullable = false)
	private String direccion;
	
	@Column(length = 50, nullable = false)
	private String email;

	@Column(length = 15, nullable = false)
	private String telefono;
	
	// Constructores
	
	public Cliente() {
		// TODO Auto-generated constructor stub
	}

	public Cliente(Long id, String nombre, Date fechanac, String nif, String direccion, String email,
			String telefono) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.fechanac = fechanac;
		this.nif = nif;
		this.direccion = direccion;
		this.email = email;
		this.telefono = telefono;
	}
	
	
	// Getter y Setter

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

	public Date getFechanac() {
		return fechanac;
	}

	public void setFechanac(Date fechanac) {
		this.fechanac = fechanac;
	}

	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}