package dao;

import java.util.ArrayList;
import java.util.Arrays;

import model.Personaje;
/**
 * Operaciones CRUD de Personaje
 * 
 * @author Albert Araque, Francisco José Ruiz
 *
 */
public class PersonajeHibernate implements PersonajeDAO {

	@Override
	public Integer addPersonaje(Personaje personaje) {
		return CrudManager.add(personaje);
	}

	@Override
	public Personaje getPersonaje(Integer idPersonaje) {
		return (Personaje) CrudManager.get(idPersonaje, Personaje.class);
	}

	@Override
	public void updatePersonaje(Personaje updatedPersonaje) {
		CrudManager.update(updatedPersonaje);
	}

	@Override
	public Integer removePersonaje(Integer idPersonaje) {
		return CrudManager.remove(idPersonaje, Personaje.class);
	}

	@Override
	public ArrayList<Personaje> getPersonajes() {

		Object[] objectArray = CrudManager.getList("Personaje", Personaje.class);		
		
		return new ArrayList<Personaje>(Arrays.asList(Arrays.copyOf(objectArray, objectArray.length, Personaje[].class)));	
	}

}
