package dao;

import model.Libro;

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

}
