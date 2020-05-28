package dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import view.Main;
/**
 * Clase para obtener la sesion en la base de datos
 * 
 * @author Albert Araque, Francisco José Ruiz
 *
 */
public class SessionFactoryUtil {
	private static SessionFactory sessionFactory = null;

	static {
		try {
			// Crea el SessionFactory de hibernate.cfg.xml
			sessionFactory = new Configuration().configure().buildSessionFactory();

		} catch (Throwable ex) {
			showAlert();
		}
	}

	private static void showAlert() {
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setTitle("Base de datos en uso");
		errorAlert.setHeaderText("No ha sido posible cargar la base de datos");
		errorAlert.setContentText("No ha sido posible cargar la base de datos debido a que ya esta en uso, es posible que haya abierto el programa dos veces.");
		errorAlert.showAndWait();
		Main.getStage().hide();
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	
}