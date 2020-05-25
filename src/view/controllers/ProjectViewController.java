package view.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import dao.DAOManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Proyecto;
import view.Main;

/**
 * Controlador de la vista general de los proyectos
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class ProjectViewController implements Initializable {

	private static final int MAX_LENGTH = 20;
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int ADD_IMAGE_Y = 62;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML public FlowPane projectFlowPane;
	@FXML public Label errorLabel;
	@FXML public Label selectedProjectLabel;
	@FXML public Pane bookButton;
	@FXML public Pane characterButton;
	@FXML public Pane locationButton;

	private Proyecto selectedProject;
	private MainViewController mainViewController;
	private ContextMenu contextMenu = new ContextMenu();

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		projectFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		projectFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		addProjectsFromDB();
		createContextMenu();

		bookButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/BookView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane = fxmlLoader.load();
				} catch (IOException e) {
				}

				BookViewController bookViewController = fxmlLoader.getController();
				bookViewController.setController(mainViewController);

				mainViewController.setView(borderPane);
			}
		});

		characterButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CharacterView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane = fxmlLoader.load();
				} catch (IOException e) {
				}

				CharacterViewController characterViewController = fxmlLoader.getController();
				characterViewController.setController(mainViewController);

				mainViewController.setView(borderPane);
			}
		});

		locationButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LocationView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane = fxmlLoader.load();
				} catch (IOException e) {
				}

				LocationViewController locationViewController = fxmlLoader.getController();
				locationViewController.setController(mainViewController);

				mainViewController.setView(borderPane);
			}
		});

	}

	/**
	 * Método para mostrar el proyecto en el panel
	 * 
	 * @param p Proyecto de entrada
	 *
	 */
	private void convertProjectToPane(Proyecto p) throws IOException {

		if (p == null) return;

		// Crea i carga el contendor (Pane) donde va la información
		Pane projectPane = FXMLLoader.load(getClass().getResource("/view/StandardPane.fxml"));
		FlowPane.setMargin(projectPane,	new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));
		
		//Carga la imagen del Pane				
		File imageFile = new File(p.getImagen());
		if (p.getImagen() == null || p.getImagen().equals("") || !imageFile.exists())
			((ImageView) projectPane.getChildren().get(0)).setImage(new Image("resources/proyecto.png"));
		else {
			((ImageView) projectPane.getChildren().get(0)).setImage(new Image(imageFile.toURI().toString()));
		}

		//Ponemos texto a las labels que queramos
		if (p.getNombre().length() > MAX_LENGTH) {
			((Label) projectPane.getChildren().get(1)).setText("Nombre: " + p.getNombre().substring(0, MAX_LENGTH) + "...");
		} else {			
			((Label) projectPane.getChildren().get(1)).setText("Nombre: " + p.getNombre());
		}
		((Label) projectPane.getChildren().get(2)).setText("Numero de libros: " + p.getLibros().size());

		//Añade el Pane al flowPane
		projectFlowPane.getChildren().add(projectPane);
		
		//Define un evento para ejecutarse cuando se hace click
		projectPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				if (event.getButton() == MouseButton.SECONDARY) contextMenu.show(projectPane, event.getScreenX(), event.getScreenY());

				if (event.getClickCount() == 1) {
					selectedProject = p;
					selectedProjectLabel.setText("Proyecto seleccionado: " + p.getNombre());
					if (errorLabel.isVisible())	errorLabel.setVisible(false);
				}

				if (event.getClickCount() == 2) {

					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InsideProjectView.fxml"));
					BorderPane borderPane = null;
					try {
						borderPane = fxmlLoader.load();
					} catch (IOException e) {
					}

					InsideProjectViewController insideProjectViewController = fxmlLoader.getController();
					insideProjectViewController.setProyecto(selectedProject);
					insideProjectViewController.setController(mainViewController);
					mainViewController.setView(borderPane);
				}
			}
		});
	}

	/**
	 * Método para mostrar los proyectos de la base de datos
	 */
	private void addProjectsFromDB() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				projectFlowPane.getChildren().removeAll(projectFlowPane.getChildren());

				for (Proyecto p : DAOManager.getProyectoDAO().getProyectos()) {
					try {
						convertProjectToPane(p);
					} catch (IOException e) {
					}
				}
				addButtonPane();
			}
		});
	}

	/**
	 * Método que añade un Pane para añadir un objeto a la BD
	 *  
	 */
	private void addButtonPane() {

		Pane addPane = new Pane();
		ImageView addImage = new ImageView("resources/add_icon.png");

		addImage.setFitHeight(IMAGE_FIT[0]);
		addImage.setFitWidth(IMAGE_FIT[1]);
		addImage.setLayoutX(IMAGE_LAYOUT[0]);
		addImage.setLayoutY(ADD_IMAGE_Y);
		addImage.setPickOnBounds(true);
		addImage.setPreserveRatio(false);

		addPane.getChildren().add(addImage);
		projectFlowPane.getChildren().add(addPane);

		// Evento al hacer click al botón añadir
		addPane.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				Stage addProjectDialog = new Stage();

				addProjectDialog.initModality(Modality.APPLICATION_MODAL);
				addProjectDialog.initStyle(StageStyle.UNDECORATED);
				addProjectDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddProjectView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				Scene dialogScene = new Scene(dialogRoot, 400, 600);
				addProjectDialog.setScene(dialogScene);
				addProjectDialog.showAndWait();
				addProjectsFromDB();
				selectedProject = null;
				selectedProjectLabel.setText("Ningún proyecto seleccionado");				
			}
		});
	}

	/**
	 * Metodo que añade items al menu contextual que aparece al hacer click derecho
	 */
	private void createContextMenu() {
		
		MenuItem viewItem = new MenuItem("Ver proyecto");
		MenuItem updateItem = new MenuItem("Actualizar proyecto");
		MenuItem deleteItem = new MenuItem("Borrar proyecto");

		// Evento al hacer click para visualizar
		viewItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedProject == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage displayProjectDialog = new Stage();

				displayProjectDialog.initModality(Modality.APPLICATION_MODAL);
				displayProjectDialog.initStyle(StageStyle.UNDECORATED);
				displayProjectDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DisplayProjectView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				DisplayProjectViewController displayController = fxmlLoader.getController();
				displayController.setProject(selectedProject);

				Scene dialogScene = new Scene(dialogRoot, 600, 490);
				displayProjectDialog.setScene(dialogScene);
				displayProjectDialog.showAndWait();
				selectedProject = null;
				selectedProjectLabel.setText("Ningún proyecto seleccionado");				
			}
		});

		// Evento al hacer click para actualizar
		updateItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {

				if (selectedProject == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage updateProjectDialog = new Stage();

				updateProjectDialog.initModality(Modality.APPLICATION_MODAL);
				updateProjectDialog.initStyle(StageStyle.UNDECORATED);
				updateProjectDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateProjectView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				UpdateProjectViewController updateController = fxmlLoader.getController();
				updateController.setProject(selectedProject);

				Scene dialogScene = new Scene(dialogRoot, 400, 600);
				updateProjectDialog.setScene(dialogScene);
				updateProjectDialog.showAndWait();
				addProjectsFromDB();
				selectedProject = null;
				selectedProjectLabel.setText("Ningún proyecto seleccionado");				
			}
		});

		// Evento al hacer click para borrar
		deleteItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedProject == null) {
					errorLabel.setVisible(true);
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Eliminación de proyecto");
				alert.setHeaderText("Estás a punto de eliminar el proyecto");
				alert.setContentText("Estás seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if (resultado.get() == ButtonType.OK) {
					DAOManager.getProyectoDAO().removeProyecto(selectedProject.getId());
					selectedProject = null;
					selectedProjectLabel.setText("Ningún proyecto seleccionado");
					addProjectsFromDB();
				}				
			}
		});

		contextMenu.getItems().addAll(viewItem, updateItem, deleteItem);
	}

	/**
	 * Método para seleccionar el controlador
	 * 
	 * @param controller Controlador de entrada
	 */
	public void setController(MainViewController controller) {
		mainViewController = controller;
	}
}