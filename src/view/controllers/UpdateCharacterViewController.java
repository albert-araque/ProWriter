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
import model.Libro;
import model.Personaje;

/**
 * Controlador de la vista para modificar un personaje
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class UpdateCharacterViewController implements Initializable {

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

	private static double xOffset;
	private static double yOffset;

	private Personaje personaje;

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999);
		ageSpinner.setValueFactory(spinnerValueFactory);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				bookList.getItems().addAll(FXCollections.observableList(DAOManager.getLibroDAO().getLibros()));

				nameText.setText(personaje.getNombre());
				ageSpinner.getValueFactory().setValue(personaje.getEdad());
				firstSurnameText.setText(personaje.getApellido1());
				secondSurnameText.setText(personaje.getApellido2());
				descriptionText.setText(personaje.getDescripcion());
				imagePath.setText(personaje.getImagen());

				checkExistingBooks();
			}
		});

		// Inicializa la validación para que el campo de nombre no quede vacío
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText,
				Validator.createEmptyValidator("El personaje debe tener un nombre"));

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

		// Evento para añadir el contenido
		addButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (validationSupport.isInvalid())
					return;

				updateCharacter(nameText.getText(), firstSurnameText.getText(), secondSurnameText.getText(),
						ageSpinner.getValue(), descriptionText.getText(), imagePath.getText(),
						new HashSet<Libro>(bookList.getCheckModel().getCheckedItems()));
				borderPane.getScene().getWindow().hide();
			}
		});

		// Evento para cerrar la ventana
		cancelButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				borderPane.getScene().getWindow().hide();
			}
		});

		pathButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				chooseFileDialog();
			}
		});
	}

	/**
	 * Método que permite seleccionar la imagen asociada
	 */
	private void chooseFileDialog() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setTitle("Selecciona una imagen para tu personaje");
		ExtensionFilter imageFilter = new ExtensionFilter("Archivos de imagen (*.jpg, *.png, *.jpeg)", "*.jpg", "*.png",
				"*.jpeg", "*.JPG", "*.PNG", "*.JPEG");
		fileChooser.getExtensionFilters().add(imageFilter);
		imagePath.setText(fileChooser.showOpenDialog(borderPane.getScene().getWindow()).getAbsolutePath());
	}

	/**
	 * Método para actualizar el personaje en la base de datos
	 * 
	 * @param name          Nombre del personaje
	 * @param firstSurname  Primer apellido del personaje
	 * @param secondSurname Segundo apellido del personaje
	 * @param age           Edad del personaje
	 * @param description   Descripción del personaje
	 * @param image         Imagen del personaje
	 * @param books         Libros del personaje
	 */
	private void updateCharacter(String name, String firstSurname, String secondSurname, int age, String description,
			String image, Set<Libro> books) {

		personaje.setNombre(name);
		personaje.setEdad(age);
		personaje.setApellido1(firstSurname);
		personaje.setApellido2(secondSurname);
		personaje.setDescripcion(description);
		personaje.setImagen(image);
		personaje.setLibros(books);

		DAOManager.getPersonajeDAO().updatePersonaje(personaje);
	}

	/**
	 * Método para tildar libros
	 */
	private void checkExistingBooks() {
		for (Libro libro : bookList.getItems()) {
			for (Libro existingBooks : personaje.getLibros()) {
				if (libro.getId() == existingBooks.getId()) {
					bookList.getCheckModel().check(libro);
				}
			}
		}
	}

	/**
	 * Método para seleccionar el personaje
	 * 
	 * @param p Personaje de entrada
	 */
	public void setPersonaje(Personaje p) {
		personaje = p;
	}
}
