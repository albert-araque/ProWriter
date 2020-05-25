package view;

import java.io.File;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.h2.tools.RunScript;
import org.h2.util.IOUtils;

import dao.CrudManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Clase principal
 * 
 * @author Albert Araque, Francisco Jos� Ruiz
 * @version 1.0
 */

public class Main extends Application {

	private static Stage mainStage;

	/**
	 * M�todo que prepara la vista principal del programa
	 */
	@Override
	public void start(Stage primaryStage) {
		try {

			createTables();

			mainStage = primaryStage;

			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));

			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setTitle("ProWriter");
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo que carga la base de datos
	 * 
	 * @throws URISyntaxException Devuelve una excepci�n si la cadena pasada por
	 *                            par�metro no puede ser parseada como referencia
	 *                            URI
	 * @throws IOException        Devuelve una excepci�n si se produce error de
	 *                            entrada o salida de datos
	 */
	private void createTables() throws URISyntaxException, IOException {

		File tempFile = new File("create_tables.sql");

		FileOutputStream output = new FileOutputStream(tempFile);
		InputStream input = getClass().getResourceAsStream("/resources/create_tables.sql");

		IOUtils.copy(input, output);

		FileReader fileReader = new FileReader(tempFile);

		try {
			Class.forName("org.h2.Driver");
			RunScript.execute(DriverManager.getConnection("jdbc:h2:~/prowriterdb", "root", ""), fileReader);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			output.close();
			input.close();
			fileReader.close();
		}

		tempFile.delete();

	}

	/**
	 * M�todo que cierra la sesi�n del CRUD Manager
	 */
	@Override
	public void stop() throws Exception {
		CrudManager.closeSession();
		super.stop();
	}

	/**
	 * M�todo que inicia el programa
	 * 
	 * @param args Par�metros de entrada
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * M�todo que devuelve el contenedor JavaFX principal
	 * 
	 * @return
	 */
	public static Stage getStage() {
		return mainStage;
	}
}
