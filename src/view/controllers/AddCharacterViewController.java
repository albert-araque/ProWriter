package view.controllers;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.controlsfx.control.CheckListView;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import dao.DAOManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.Escena;
import model.Libro;
import model.Personaje;

public class AddCharacterViewController implements Initializable {

	@FXML public BorderPane borderPane;
	@FXML public TextField nameText;
	@FXML public Spinner<Integer> ageSpinner;
	@FXML public TextField firstSurnameText;
	@FXML public TextField secondSurnameText;
	@FXML public TextArea descriptionText;
	@FXML public TextField imagePath;
	@FXML public Button pathButton;
	@FXML public Button addButton;
	@FXML public Button cancelButton;
	@FXML public CheckListView<Libro> bookList;
	@FXML public CheckListView<Escena> sceneList;

	private static double xOffset;
	private static double yOffset;

	private Personaje characterToReturn = null;
	private Libro book;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999);		
		ageSpinner.setValueFactory(spinnerValueFactory);
	
		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				bookList.getItems().addAll(FXCollections.observableList(DAOManager.getLibroDAO().getLibros()));
				sceneList.getItems().addAll(FXCollections.observableList(DAOManager.getEscenaDAO().getEscenas()));

				for (Libro l : bookList.getItems()) {
					if (l.getId() == book.getId()) bookList.getCheckModel().check(l);
				}
			}
		});

		//inicializa la validacion para que el campo de nombre no se quede vacio
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText, Validator.createEmptyValidator("El personaje tiene que tener un nombre"));

		//eventos de click para poder mover la ventana dado que no tiene barra de titulo
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

		//evento de click para añadir el personaje
		addButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (validationSupport.isInvalid()) return;

				addCharacterToDB(nameText.getText(), firstSurnameText.getText(), secondSurnameText.getText(),
								ageSpinner.getValue(), descriptionText.getText(), imagePath.getText(), 
								new HashSet<Libro>(bookList.getCheckModel().getCheckedItems()), 
								new HashSet<Escena>(sceneList.getCheckModel().getCheckedItems()));
				borderPane.getScene().getWindow().hide();
			}
		});

		//evento de click para cerrar la ventana
		cancelButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				borderPane.getScene().getWindow().hide();
			}
		});

		//evento de click para mostrar el selector de archivo, con un filtro de extensiones de imagenes
		pathButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
				fileChooser.setTitle("Selecciona una imagen para tu personaje");
				ExtensionFilter imageFilter = new ExtensionFilter("Archivos de imagen (*.jpg, *.png, *.jpeg)", "*.jpg", "*.png", "*.jpeg", "*.JPG", "*.PNG", "*.JPEG");
				fileChooser.getExtensionFilters().add(imageFilter);
				imagePath.setText(fileChooser.showOpenDialog(borderPane.getScene().getWindow()).getAbsolutePath());
			}
		});	

	}

	private void addCharacterToDB(String name, String firstSurname, String secondSurname, int age, String description, String image, Set<Libro> books, Set<Escena> scenes) {

		characterToReturn = new Personaje(name, firstSurname, secondSurname, age, description, image, books, scenes);
		book.getPersonajes().add(characterToReturn);
		DAOManager.getPersonajeDAO().addPersonaje(characterToReturn);		
	}

	public void setBook(Libro l) {
		book = l;
	}
}
