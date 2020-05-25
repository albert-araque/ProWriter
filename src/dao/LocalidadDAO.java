package dao;

import java.util.ArrayList;

import model.Localidad;
/**
 * Interfaz de Localidad
 * 
 * @author Albert Araque, Francisco José Ruiz
 *
 */
public interface LocalidadDAO {
	
	public Integer addLocalidad(Localidad localidad);
	
	public Localidad getLocalidad(Integer idLocalidad);
	
	public void updateLocalidad(Localidad updatedLocalidad);
	
	public Integer removeLocalidad(Integer idLocalidad);

	public ArrayList<Localidad> getLocalidades();
}
