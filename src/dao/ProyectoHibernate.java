package dao;

import java.util.ArrayList;
import java.util.Arrays;

import model.Proyecto;

public class ProyectoHibernate implements ProyectoDAO {

	@Override
	public Integer addProyecto(Proyecto proyecto) {
		return CrudManager.add(proyecto);
	}

	@Override
	public Proyecto getProyeto(Integer idProyecto) {
		return (Proyecto) CrudManager.get(idProyecto, Proyecto.class);
	}

	@Override
	public void updateProyecto(Proyecto updatedProyecto) {
		CrudManager.update(updatedProyecto);
	}

	@Override
	public Integer removeProyecto(Integer idProyecto) {
		return CrudManager.remove(idProyecto, Proyecto.class);
	}

	@Override
	public ArrayList<Proyecto> getProyectos() {

		Object[] objectArray = CrudManager.getList("Proyecto", Proyecto.class);		
		
		return new ArrayList<Proyecto>(Arrays.asList(Arrays.copyOf(objectArray, objectArray.length, Proyecto[].class)));	
	}

}
