package model;
// Generated 23-may-2020 19:25:06 by Hibernate Tools 5.4.14.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Capitulo generated by hbm2java
 */
public class Capitulo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5628341751541487181L;
	private Integer id;
	private Libro libro;
	private String nombre;
	private Integer numero;
	private String descripcion;
	private Set<Escena> escenas = new HashSet<Escena>(0);

	public Capitulo() {
	}

	public Capitulo(Libro libro) {
		this.libro = libro;
	}

	public Capitulo(Libro libro, String nombre, Integer numero, String descripcion, Set<Escena> escenas) {
		this.libro = libro;
		this.nombre = nombre;
		this.numero = numero;
		this.descripcion = descripcion;
		this.escenas = escenas;
	}

	public Capitulo(Libro book, String name, int order, String description) {
		this.libro = book;
		this.nombre = name;
		this.numero = order;
		this.descripcion = description;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Libro getLibro() {
		return this.libro;
	}

	public void setLibro(Libro libro) {
		this.libro = libro;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Set<Escena> getEscenas() {
		return this.escenas;
	}

	public void setEscenas(Set<Escena> escenas) {
		this.escenas = escenas;
	}

	@Override
	public String toString() {
		return this.getNombre();
	}

}
