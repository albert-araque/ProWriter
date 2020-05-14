package dao;

import model.Localidad;

public class LocalidadHibernate implements LocalidadDAO {

	@Override
	public Integer addLocalidad(Localidad localidad) {
		return CrudManager.add(localidad);
	}

	@Override
	public Localidad getLocalidad(Integer idLocalidad) {
		return (Localidad) CrudManager.get(idLocalidad, Localidad.class);
	}

	@Override
	public void updateLocalidad(Localidad updatedLocalidad) {
		CrudManager.update(updatedLocalidad);
	}

	@Override
	public Integer removeLocalidad(Integer idLocalidad) {
		return CrudManager.remove(idLocalidad, Localidad.class);
	}

}
