package dao;

import java.util.ArrayList;
import java.util.Arrays;

import model.Capitulo;

/**
 * Operaciones CRUD de Capítulo
 * @author Albert Araque, Francisco José Ruiz
 *
 */
public class CapituloHibernate implements CapituloDAO {

	@Override
	public Integer addCapitulo(Capitulo capitulo) {
		return CrudManager.add(capitulo);
	}

	@Override
	public Capitulo getCapitulo(Integer idCapitulo) {
		return (Capitulo) CrudManager.get(idCapitulo, Capitulo.class);
	}

	@Override
	public void updateCapitulo(Capitulo updatedCapitulo) {
		CrudManager.update(updatedCapitulo);
	}

	@Override
	public Integer removeCapitulo(Integer idCapitulo) {
		return CrudManager.remove(idCapitulo, Capitulo.class);
	}

	@Override
	public ArrayList<Capitulo> getCapitulos() {
		
		Object[] objectArray = CrudManager.getList("Capitulo", Capitulo.class);		
		
		return new ArrayList<Capitulo>(Arrays.asList(Arrays.copyOf(objectArray, objectArray.length, Capitulo[].class)));
	}

}
