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

public class UpdateProjectViewController implements Initializable {

	@FXML public TextField nameText;
	@FXML public TextField imagePath;
	@FXML public TextArea descriptionText;
	@FXML public CheckListView<Libro> bookList;
	@FXML public Button updateButton;
	@FXML public Button cancelButton;
	@FXML public Button pathButton;
	@FXML public BorderPane borderPane;	

	private static double xOffset;
	private static double yOffset;
	
	private Proyecto project;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {	

		//carga la informacion del proyecto seleccionado en los diferentes campos
		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
			
				bookList.getItems().addAll(FXCollections.observableList(DAOManager.getLibroDAO().getLibros()));

				nameText.setText(project.getNombre());
				descriptionText.setText(project.getDescripcion());
				imagePath.setText(project.getImagen());

				checkExistingBooks();
			}			
		});

		//inicializa la validacion para que el campo de nombre no se quede vacio
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText, Validator.createEmptyValidator("El proyecto tiene que tener un nombre"));

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

		//evento de click para cerrar la ventana
		cancelButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				borderPane.getScene().getWindow().hide();
			}
		});

		//evento de click para actualizar el proyecto
		updateButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				if (validationSupport.isInvalid()) return;

				updateProject(nameText.getText(), descriptionText.getText(), imagePath.getText(), new HashSet<Libro>(bookList.getCheckModel().getCheckedItems()));
				borderPane.getScene().getWindow().hide();
			}			
		});

		//evento de click para mostrar el selector de archivo, con un filtro de extensiones de imagenes
		pathButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				chooseFileDialog();
			}
		});	
	}	

	private void chooseFileDialog() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setTitle("Selecciona una imagen para tu proyecto");
		ExtensionFilter imageFilter = new ExtensionFilter("Archivos de imagen (*.jpg, *.png, *.jpeg)", "*.jpg", "*.png", "*.jpeg", "*.JPG", "*.PNG", "*.JPEG");
		fileChooser.getExtensionFilters().add(imageFilter);
		imagePath.setText(fileChooser.showOpenDialog(borderPane.getScene().getWindow()).getAbsolutePath());
	}

	//actualiza el proyecto en la base de datos
	private void updateProject(String name, String description, String imagePath, Set<Libro> books) {

		project.setNombre(name);
		project.setDescripcion(description);
		project.setImagen(imagePath);
		project.setLibros(books);
		
		DAOManager.getProyectoDAO().updateProyecto(project);
	}

	//recorre la collection de libros totales en la base de datos, compara la id con los libros existentes en el proyecto, y los pone checked
	private void checkExistingBooks() {
		for (Libro libro : bookList.getItems()) {
			for (Libro existingBook : project.getLibros()) {
				if (libro.getId() == existingBook.getId()) {
					bookList.getCheckModel().check(libro);
				}
			}
		}
	}	

	public void setProject(Proyecto p) {
		project = p;
	}

	public Proyecto getProyecto() {
		return project;
	}

}
