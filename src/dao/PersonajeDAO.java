package dao;

import java.util.ArrayList;

import model.Personaje;
/**
 * Interfaz de Personaje
 * 
 * @author Albert Araque, Francisco José Ruiz
 *
 */
public interface PersonajeDAO {
	
	public Integer addPersonaje(Personaje personaje);
	
	public Personaje getPersonaje(Integer idPersonaje);
	
	public void updatePersonaje(Personaje updatedPersonaje);
	
	public Integer removePersonaje(Integer idPersonaje);
	
	public ArrayList<Personaje> getPersonajes();

}
