package dao;

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

}
