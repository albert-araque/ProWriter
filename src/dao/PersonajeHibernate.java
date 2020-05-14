package dao;

import model.Personaje;

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

}
