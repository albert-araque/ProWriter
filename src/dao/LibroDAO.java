package dao;

import model.Libro;

public interface LibroDAO {
	
	public Integer addLibro(Libro libro);
	
	public Libro getLibro(Integer idLibro);
	
	public void updateLibro(Libro updatedLibro);

	public Integer removeLibro(Integer idLibro);
}
