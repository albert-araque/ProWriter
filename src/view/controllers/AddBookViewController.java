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
 * Clase para a�adir un libro
 * 
 * @author Albert Araque, Francisco Jos� Ruiz
 * @version 1.0
 */
public class AddBookViewController implements Initializable {

	@FXML
	public TextField nameText;
	@FXML
	public TextField imagePath;
	@FXML
	public TextArea descriptionText;
	@FXML
	public TextField genreText;
	@FXML
	public CheckListView<Personaje> characterList;
	@FXML
	public CheckListView<Proyecto> projectList;
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

	private Libro bookToReturn = null;

	private Proyecto project;

	/**
	 * M�todo para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				characterList.getItems()
						.addAll(FXCollections.observableList(DAOManager.getPersonajeDAO().getPersonajes()));
				projectList.getItems().addAll(FXCollections.observableList(DAOManager.getProyectoDAO().getProyectos()));

				if (project != null) {
					for (Proyecto p : projectList.getItems()) {
						if (p.getId() == project.getId()) {
							projectList.getCheckModel().check(p);
						}
					}
				}
			}
		});

		// Inicializa la validaci�n para que el campo de nombre no quede vac�o
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText, Validator.createEmptyValidator("El libro debe tener un nombre"));

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

		// Evento para cerrar la ventana
		cancelButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				borderPane.getScene().getWindow().hide();
			}
		});

		// Evento para a�adir el contenido
		addButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (validationSupport.isInvalid())
					return;

				addBookToDB(nameText.getText(), descriptionText.getText(), genreText.getText(), imagePath.getText(),
						new HashSet<Personaje>(characterList.getCheckModel().getCheckedItems()),
						new HashSet<Proyecto>(projectList.getCheckModel().getCheckedItems()));
				borderPane.getScene().getWindow().hide();
			}
		});

		// Evento para mostrar el selector de archivo, con un filtro de extensiones de
		// im�genes
		pathButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
		});		
			}			
				chooseFileDialog();
	}
	
	/**
	 * Muestra un dialogo para elegir un archivo
	 */
	private void chooseFileDialog() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setTitle("Selecciona una imagen para tu libro");
		ExtensionFilter imageFilter = new ExtensionFilter("Archivos de imagen (*.jpg, *.png, *.jpeg)", "*.jpg", "*.png", "*.jpeg", "*.JPG", "*.PNG", "*.JPEG");
		fileChooser.getExtensionFilters().add(imageFilter);
		imagePath.setText(fileChooser.showOpenDialog(borderPane.getScene().getWindow()).getAbsolutePath());
	}

	/**
	 * M�todo para a�adir el libro al proyecto, y guardarlo en la base de datos
	 * 
	 * @param name        Nombre del libro
	 * @param description Descripci�n del libro
	 * @param genre       G�nero literario del libro
	 * @param imagePath   Imagen del libro
	 * @param characters  Personajes del libro
	 * @param projects    Proyectos en los que se encuentra el libro
	 */
	private void addBookToDB(String name, String description, String genre, String imagePath, Set<Personaje> characters,
			Set<Proyecto> projects) {

		bookToReturn = new Libro(name, description, genre, imagePath, characters, projects);
		if (project != null)
			project.getLibros().add(bookToReturn);
		DAOManager.getLibroDAO().addLibro(bookToReturn);
	}

	/**
	 * M�todo para seleccionar el proyecto
	 * 
	 * @param p Proyecto de entrada
	 */
	public void setProject(Proyecto p) {
		project = p;
	}
}
