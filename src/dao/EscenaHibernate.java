package dao;

import model.Escena;

public class EscenaHibernate implements EscenaDAO {

	@Override
	public Integer addEscena(Escena escena) {
		return CrudManager.add(escena);
	}

	@Override
	public Escena getEscena(Integer idEscena) {
		return (Escena) CrudManager.get(idEscena, Escena.class);
	}

	@Override
	public void updateEscena(Escena updatedEscena) {
		CrudManager.update(updatedEscena);
	}

	@Override
	public Integer removeEscena(Integer idEscena) {
		return CrudManager.remove(idEscena, Escena.class);
	}

}
