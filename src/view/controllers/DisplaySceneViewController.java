package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Hibernate;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import model.Escena;
import model.Personaje;

/**
 * Controlador de la vista de una escena
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class DisplaySceneViewController implements Initializable {

	@FXML public BorderPane borderPane;
	@FXML public Label nameLabel;
	@FXML public Label chapterLabel;
	@FXML public Label locationLabel;
	@FXML public TextArea descriptionText;
	@FXML public ListView<Personaje> characterList;
	@FXML public Button closeButton;

	private static double xOffset;
	private static double yOffset;

	private Escena scene;

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setInformation();
			}
		});

		// Evento para poder mover la ventana, dado que no tiene barra de título
		borderPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = borderPane.getScene().getWindow().getX() - event.getScreenX();
				yOffset = borderPane.getScene().getWindow().getY() - event.getScreenY();
			}
		});
		borderPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				borderPane.getScene().getWindow().setX(event.getScreenX() + xOffset);
				borderPane.getScene().getWindow().setY(event.getScreenY() + yOffset);
			}
		});

		closeButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				borderPane.getScene().getWindow().hide();
			}
		});

	}

	/**
	 * Método para mostrar información sobre la escena
	 */
	private void setInformation() {

		nameLabel.setText("Nombre: " + scene.getNombre());
		chapterLabel.setText("Capitulo: " + scene.getCapitulo().getNombre());
		locationLabel.setText("Localizacion: " + scene.getLocalidad().getNombre());
		descriptionText.setText(scene.getDescripcion());
		characterList.getItems().addAll(scene.getPersonajes());

	}

	/**
	 * Método para seleccionar la escena
	 * 
	 * @param e Escena de entrada
	 */
	public void setScene(Escena e) {
		scene = e;
	}

}
