package dao;

import model.Localidad;

public interface LocalidadDAO {
	
	public Integer addLocalidad(Localidad localidad);
	
	public Localidad getLocalidad(Integer idLocalidad);
	
	public void updateLocalidad(Localidad updatedLocalidad);
	
	public Integer removeLocalidad(Integer idLocalidad);

}
