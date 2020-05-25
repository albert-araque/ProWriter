package view.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import model.Escena;
import model.Libro;
import model.Personaje;

/**
 * Controlador de la vista de un personaje
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class DisplayCharacterViewController implements Initializable {

	@FXML public BorderPane borderPane;
	@FXML public ImageView imageView;
	@FXML public Label nameLabel;
	@FXML public Label ageLabel;
	@FXML public TextArea descriptionText;
	@FXML public ListView<Libro> bookList;
	@FXML public ListView<Escena> sceneList;
	@FXML public Button closeButton;

	private static double xOffset;
	private static double yOffset;

	private Personaje character;

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
	 * Método para mostrar información sobre el personaje
	 */
	private void setInformation() {

		File imageFile = null;
		try { imageFile = new File(character.getImagen()); } catch (Exception e) {}		
		if (character.getImagen() == null || character.getImagen().equals("") || !imageFile.exists()) imageView.setImage(new Image("resources/character_icon.png"));
		else {			
			imageView.setImage(new Image(imageFile.toURI().toString()));
		}

		String apellido1 = character.getApellido1() == null ? "" : character.getApellido1();
		String apellido2 = character.getApellido2() == null ? "" : character.getApellido2();

		nameLabel.setText("Nombre: " + character.getNombre() + " " + apellido1 + " " + apellido2);
		ageLabel.setText(String.valueOf(character.getEdad()));
		descriptionText.setText(character.getDescripcion());
		bookList.getItems().addAll(character.getLibros());
		sceneList.getItems().addAll(character.getEscenas());

	}

	/**
	 * Método para seleccionar el personaje
	 * 
	 * @param p Personaje de entrada
	 */
	public void setCharacter(Personaje p) {
		character = p;
	}

}
