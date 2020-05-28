package dao;

import java.util.ArrayList;

import model.Escena;

/**
 * Interfaz de Escena
 * 
 * @author Albert Araque, Francisco José Ruiz
 *
 */
public interface EscenaDAO {
	
	public Integer addEscena(Escena escena);
	
	public Escena getEscena(Integer idEscena);
	
	public void updateEscena(Escena updatedEscena);
	
	public Integer removeEscena(Integer idEscena);
	
	public ArrayList<Escena> getEscenas();

}
