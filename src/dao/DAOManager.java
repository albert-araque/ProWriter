package dao;

public class DAOManager {

	private static CapituloDAO capitulo;
	private static EscenaDAO escena;
	private static LibroDAO libro;
	private static LocalidadDAO localidad;
	private static PersonajeDAO personaje;
	private static ProyectoDAO proyecto;
	
	public static CapituloDAO getCapituloDAO() {
		if (capitulo == null) capitulo = new CapituloHibernate();
		return capitulo;
	}
	
	public static EscenaDAO getEscenaDAO() {
		if (escena == null) escena = new EscenaHibernate();
		return escena;
	}
	
	public static LibroDAO getLibroDAO() {
		if (libro == null) libro = new LibroHibernate();
		return libro;
	}
	
	public static LocalidadDAO getLocalidadDAO() {
		if (localidad == null) localidad = new LocalidadHibernate();
		return localidad;
	}
	
	public static PersonajeDAO getPersonajeDAO() {
		if (personaje == null) personaje = new PersonajeHibernate();
		return personaje;
	}
	
	public static ProyectoDAO getProyectoDAO() {
		if (proyecto == null) proyecto = new ProyectoHibernate();
		return proyecto;
	}
}
