package view;
	
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import dao.DAOManager;
import dao.LibroDAO;
import dao.PersonajeDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Libro;
import model.Personaje;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
//		LibroDAO libroDAO = DAOManager.getLibroDAO();
//		PersonajeDAO personajeDAO = DAOManager.getPersonajeDAO();
//		
//		Personaje darrow = personajeDAO.getPersonaje(3);
//		Personaje cassio = personajeDAO.getPersonaje(4);
//		Set<Personaje> personajeSet = new HashSet<Personaje>();
//		personajeSet.add(darrow);
//		personajeSet.add(cassio);
//		
//		Libro libroPrueba = new Libro("Libro prueba", "pruebadescripcion", "pruebagenero", "pruebaimagen", personajeSet, null, null);
//	
//		libroDAO.addLibro(libroPrueba);		
//		ArrayList<Libro> libroArray = libroDAO.getLibros();
//		
//		for (Libro libro : libroArray) {
//			System.out.println(libro.toString());
//		}
		
		launch(args);
	}
}
