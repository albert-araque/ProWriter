package dao;

import java.util.ArrayList;
import java.util.Arrays;

import model.Escena;

/**
 * Operaciones CRUD de Escena
 * 
 * @author Albert Araque, Francisco José Ruiz
 *
 */
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

	@Override
	public ArrayList<Escena> getEscenas() {
		
		Object[] objectArray = CrudManager.getList("Escena", Escena.class);		

		return new ArrayList<Escena>(Arrays.asList(Arrays.copyOf(objectArray, objectArray.length, Escena[].class)));
	}

}
