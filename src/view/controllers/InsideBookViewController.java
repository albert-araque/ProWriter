package view.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import model.Libro;
import model.Proyecto;

/**
 * Clase para seleccionar cap�tulos o personajes de un libro
 * 
 * @author Albert Araque, Francisco Jos� Ruiz
 * @version 1.0
 */
public class InsideBookViewController implements Initializable {

	@FXML
	public BorderPane bookViewPane;
	@FXML
	public Pane characterPane;
	@FXML
	public Pane chapterPane;
	@FXML
	public Label bookNameDisplay;
	@FXML
	public Button backButton;
	@FXML
	public ImageView characterPaneImage;
	@FXML
	public ImageView chapterPaneImage;

	private MainViewController mainViewController;
	private Libro book;
	private Proyecto project;

	/**
	 * M�todo para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				bookNameDisplay.setText(project.getNombre() + " > " + book.getNombre());

				characterPaneImage.setImage(new Image("resources/character_icon.png"));
				chapterPaneImage.setImage(new Image("resources/capitulo_icono.png"));
			}
		});

		backButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InsideProjectView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane = fxmlLoader.load();
				} catch (IOException e) {
				}

				InsideProjectViewController insideProjectViewController = fxmlLoader.getController();
				insideProjectViewController.setProyecto(project);
				insideProjectViewController.setController(mainViewController);

				mainViewController.setView(borderPane);
			}
		});

		characterPane.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CharacterInsideBookView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane = fxmlLoader.load();
				} catch (IOException e) {
				}

				CharacterInsideBookViewController characterInsideBookViewController = fxmlLoader.getController();
				characterInsideBookViewController.setBook(book);
				characterInsideBookViewController.setProject(project);
				characterInsideBookViewController.setController(mainViewController);

				mainViewController.setView(borderPane);
			}
		});

		chapterPane.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ChapterInsideBookView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane = fxmlLoader.load();
				} catch (IOException e) {
				}

				ChapterInsideBookViewController chapterInsideBookViewController = fxmlLoader.getController();
				chapterInsideBookViewController.setBook(book);
				chapterInsideBookViewController.setProject(project);
				chapterInsideBookViewController.setController(mainViewController);

				mainViewController.setView(borderPane);

			}
		});
	}

	/**
	 * M�todo para seleccionar el proyecto
	 * 
	 * @param p Proyecto de entrada
	 */
	public void setProject(Proyecto p) {
		project = p;
	}

	/**
	 * M�todo para seleccionar el libro
	 * 
	 * @param l Libro de entrada
	 */
	public void setBook(Libro l) {
		book = l;
	}

	/**
	 * M�todo para seleccionar el controlador
	 * 
	 * @param controller Controlador de entrada
	 */
	public void setController(MainViewController controller) {
		mainViewController = controller;
	}

}
