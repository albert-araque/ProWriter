package view.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * Controlador de la vista sobre la que se sobrepondr�n todas las dem�s vistas del programa
 * 
 * @author Albert Araque, Francisco Jos� Ruiz
 * @version 1.0
 */
public class MainViewController implements Initializable {

	@FXML
	public AnchorPane rootView;

	private BorderPane currentView;

	/**
	 * M�todo para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProjectView.fxml"));

		try {
			currentView = fxmlLoader.load();
		} catch (IOException e) {
		}

		ProjectViewController projectViewController = fxmlLoader.getController();
		projectViewController.setController(this);

		currentView.prefWidthProperty().bind(rootView.widthProperty());
		currentView.prefHeightProperty().bind(rootView.heightProperty());

		rootView.getChildren().add(currentView);

	}

	/**
	 * M�todo para seleccionar la vista en el panel
	 * 
	 * @param view Panel de entrada
	 */
	public void setView(Pane view) {
		if (view == null)
			return;

		currentView = (BorderPane) view;

		currentView.prefWidthProperty().bind(rootView.widthProperty());
		currentView.prefHeightProperty().bind(rootView.heightProperty());

		rootView.getChildren().clear();
		rootView.getChildren().add(currentView);
	}

}
