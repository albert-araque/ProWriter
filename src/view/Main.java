package view;

import java.io.File;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.h2.tools.RunScript;
import org.h2.util.IOUtils;

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

	private void createTables() {
		
		File tempFile = null;
		FileOutputStream output = null;
		InputStream input = null;
		FileReader fileReader = null;
		try {
			tempFile = new File("create_tables.sql");		
			output = new FileOutputStream(tempFile);
			input = getClass().getResourceAsStream("/resources/create_tables.sql");
			IOUtils.copy(input, output);
			fileReader = new FileReader(tempFile);
			
			Class.forName ("org.h2.Driver");		
			RunScript.execute(DriverManager.getConnection("jdbc:h2:~/prowriterdb", "root", ""), fileReader);
			
		} catch (ClassNotFoundException | IOException | SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
				input.close();
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			tempFile.delete();
		}

	}

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
