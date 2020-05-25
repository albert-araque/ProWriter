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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.Libro;
import model.Personaje;
import model.Proyecto;

/**
 * Controlador de la vista para modificar un libro
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class UpdateBookViewController implements Initializable {

	@FXML public TextField nameText;
	@FXML public TextField imagePath;
	@FXML public TextArea descriptionText;
	@FXML public TextField genreText;
	@FXML public CheckListView<Personaje> characterList;
	@FXML public CheckListView<Proyecto> projectList;
	@FXML public Button addButton;
	@FXML public Button cancelButton;
	@FXML public Button pathButton;
	@FXML public BorderPane borderPane;

	private static double xOffset;
	private static double yOffset;

	private Libro book;

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				characterList.getItems()
						.addAll(FXCollections.observableList(DAOManager.getPersonajeDAO().getPersonajes()));
				projectList.getItems().addAll(FXCollections.observableList(DAOManager.getProyectoDAO().getProyectos()));

				nameText.setText(book.getNombre());
				descriptionText.setText(book.getDescripcion());
				genreText.setText(book.getGenero());
				imagePath.setText(book.getImagen());

				checkExistingCharacters();
				checkExistingProjects();
			}
		});

		// Inicializa la validación para que el campo de nombre no quede vacío
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText, Validator.createEmptyValidator("El libro debe tener un nombre"));

		// Evento para poder mover la ventana dado que no tiene barra de
		// titulo
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

				updateBook(nameText.getText(), descriptionText.getText(), genreText.getText(), imagePath.getText(),
						new HashSet<Personaje>(characterList.getCheckModel().getCheckedItems()),
						new HashSet<Proyecto>(projectList.getCheckModel().getCheckedItems()));
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
		fileChooser.setTitle("Selecciona una imagen para tu libro");
		ExtensionFilter imageFilter = new ExtensionFilter("Archivos de imagen (*.jpg, *.png, *.jpeg)", "*.jpg", "*.png",
				"*.jpeg", "*.JPG", "*.PNG", "*.JPEG");
		fileChooser.getExtensionFilters().add(imageFilter);
		imagePath.setText(fileChooser.showOpenDialog(borderPane.getScene().getWindow()).getAbsolutePath());
	}

	/**
	 * Método para actualizar el libro en la base de datos
	 * 
	 * @param name        Nombre del libro
	 * @param description Descripción del libro
	 * @param genre       Género del libro
	 * @param imagePath   Imagen del libro
	 * @param characters  Personajes del libro
	 * @param projects    Proyectos del libro
	 */
	private void updateBook(String name, String description, String genre, String imagePath, Set<Personaje> characters,
			Set<Proyecto> projects) {

		book.setNombre(name);
		book.setDescripcion(description);
		book.setGenero(genre);
		book.setImagen(imagePath);
		book.setPersonajes(characters);
		book.setProyectos(projects);

		DAOManager.getLibroDAO().updateLibro(book);
	}

	/**
	 * Método para tildar personajes
	 */
	private void checkExistingCharacters() {
		for (Personaje character : characterList.getItems()) {
			for (Personaje existingCharacters : book.getPersonajes()) {
				if (character.getId() == existingCharacters.getId()) {
					characterList.getCheckModel().check(character);
				}
			}
		}
	}

	/**
	 * Método para tildar proyectos
	 */
	private void checkExistingProjects() {
		for (Proyecto proyecto : projectList.getItems()) {
			for (Proyecto existingProyects : book.getProyectos()) {
				if (proyecto.getId() == existingProyects.getId()) {
					projectList.getCheckModel().check(proyecto);
				}
			}
		}
	}

	/**
	 * Método para seleccionar el libro
	 * 
	 * @param l Libro de entrada
	 */
	public void setBook(Libro l) {
		book = l;
	}
}
