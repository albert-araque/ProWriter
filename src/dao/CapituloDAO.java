package dao;

import model.Capitulo;

public interface CapituloDAO {
	
	public Integer addCapitulo(Capitulo capitulo);
	
	public Capitulo getCapitulo(Integer idCapitulo);
	
	public void updateCapitulo(Capitulo updatedCapitulo);
	
	public Integer removeCapitulo(Integer idCapitulo);

}
