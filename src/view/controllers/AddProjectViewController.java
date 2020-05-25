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
import model.Proyecto;

/**
 * Clase para añadir un proyecto
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class AddProjectViewController implements Initializable {

	@FXML
	public TextField nameText;
	@FXML
	public TextField imagePath;
	@FXML
	public TextArea descriptionText;
	@FXML
	public CheckListView<Libro> bookList;
	@FXML
	public Button addButton;
	@FXML
	public Button cancelButton;
	@FXML
	public Button pathButton;
	@FXML
	public BorderPane borderPane;

	private static double xOffset;
	private static double yOffset;

	private Proyecto projectToReturn = null;

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				bookList.getItems().addAll(FXCollections.observableList(DAOManager.getLibroDAO().getLibros()));
			}
		});

		// Inicializa la validación para que el campo de nombre no quede vacío
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText,
				Validator.createEmptyValidator("El proyecto debe tener un nombre"));

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

		// Evento para cerrar la ventana
		cancelButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				borderPane.getScene().getWindow().hide();
			}
		});

		// Evento para añadir el contenido
		addButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (validationSupport.isInvalid())
					return;

				addProjectToDB(nameText.getText(), descriptionText.getText(), imagePath.getText(),
						new HashSet<Libro>(bookList.getCheckModel().getCheckedItems()));
				borderPane.getScene().getWindow().hide();
			}
		});

		// Evento para mostrar el selector de archivo, con un filtro de extensiones de
		// imágenes
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
		fileChooser.setTitle("Selecciona una imagen para tu proyecto");
		ExtensionFilter imageFilter = new ExtensionFilter("Archivos de imagen (*.jpg, *.png, *.jpeg)", "*.jpg", "*.png",
				"*.jpeg", "*.JPG", "*.PNG", "*.JPEG");
		fileChooser.getExtensionFilters().add(imageFilter);
		imagePath.setText(fileChooser.showOpenDialog(borderPane.getScene().getWindow()).getAbsolutePath());
	}

	/**
	 * Método para añadir el proyecto a la base de datos
	 * 
	 * @param name        Nombre del proyecto
	 * @param description Descripción del proyecto
	 * @param imagePath   Imagen del proyecto
	 * @param books       Libros que contiene el proyecto
	 */
	private void addProjectToDB(String name, String description, String imagePath, Set<Libro> books) {

		projectToReturn = new Proyecto(name, description, imagePath, books);

		DAOManager.getProyectoDAO().addProyecto(projectToReturn);
	}
}
