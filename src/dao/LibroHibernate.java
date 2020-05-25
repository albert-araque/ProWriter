package dao;

import java.util.ArrayList;
import java.util.Arrays;

import model.Libro;

/**
 * Operaciones CRUD de Libro
 * 
 * @author Albert Araque, Francisco José Ruiz
 *
 */
public class LibroHibernate implements LibroDAO {

	@Override
	public Integer addLibro(Libro libro) {
		return CrudManager.add(libro);
	}

	@Override
	public Libro getLibro(Integer idLibro) {
		return (Libro) CrudManager.get(idLibro, Libro.class);
	}

	@Override
	public void updateLibro(Libro updatedLibro) {
		CrudManager.update(updatedLibro);
	}

	@Override
	public Integer removeLibro(Integer idLibro) {
		return CrudManager.remove(idLibro, Libro.class);
	}

	@Override
	public ArrayList<Libro> getLibros() {

		Object[] objectArray = CrudManager.getList("Libro", Libro.class);		
		
		return new ArrayList<Libro>(Arrays.asList(Arrays.copyOf(objectArray, objectArray.length, Libro[].class)));		
	}

}
