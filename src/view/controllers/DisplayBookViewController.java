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
import model.Capitulo;
import model.Libro;
import model.Personaje;
import model.Proyecto;

/**
 * Clase que contiene la vista de un libro
 * 
 * @author Albert Araque, Francisco Jos� Ruiz
 * @version 1.0
 */
public class DisplayBookViewController implements Initializable {

	@FXML
	public BorderPane borderPane;
	@FXML
	public Label nameLabel;
	@FXML
	public Label genreLabel;
	@FXML
	public TextArea descriptionText;
	@FXML
	public ImageView imageView;
	@FXML
	public ListView<Personaje> characterList;
	@FXML
	public ListView<Proyecto> projectList;
	@FXML
	public ListView<Capitulo> chapterList;
	@FXML
	public Button closeButton;

	private static double xOffset;
	private static double yOffset;

	private Libro book;

	/**
	 * M�todo para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setInformation();
			}
		});

		// Evento para poder mover la ventana, dado que no tiene barra de t�tulo
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
	 * M�todo para mostrar informaci�n sobre el libro
	 */
	private void setInformation() {

		File imageFile = null;
		File errorFile = new File("./src/resources/libro.png");

		try {
			imageFile = new File(book.getImagen());
		} catch (Exception e) {
		}
		if (book.getImagen() == null || book.getImagen().equals("") || !imageFile.exists())
			imageView.setImage(new Image(errorFile.toURI().toString()));
		else {
			imageView.setImage(new Image(imageFile.toURI().toString()));
		}

		nameLabel.setText("Nombre: " + book.getNombre());
		genreLabel.setText("Genero: " + book.getGenero());
		descriptionText.setText(book.getDescripcion());
		characterList.getItems().addAll(book.getPersonajes());
		chapterList.getItems().addAll(book.getCapitulos());
		projectList.getItems().addAll(book.getProyectos());

	}

	/**
	 * M�todo para seleccionar el libro
	 * 
	 * @param l Libro de entrada
	 */
	public void setBook(Libro l) {
		book = l;
	}

}
