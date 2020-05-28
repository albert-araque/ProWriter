package view.controllers;

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
import javafx.scene.control.Button;
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
import model.Capitulo;
import model.Escena;
import model.Libro;
import model.Proyecto;
import view.Main;

/**
 * Controlador de la vista de las escenas de un capítulo
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class SceneChapterViewController implements Initializable {

	private static final int MAX_LENGTH = 20;
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int ADD_IMAGE_Y = 62;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML public Label selectedSceneLabel;
	@FXML public Label errorLabel;
	@FXML public Label chapterNameDisplay;
	@FXML public Button backButton;
	@FXML public FlowPane sceneFlowPane;

	private MainViewController mainViewController;
	private Capitulo chapter;
	private Libro book;
	private Proyecto project;
	private Escena selectedScene;
	private ContextMenu contextMenu = new ContextMenu();	

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		sceneFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		sceneFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chapterNameDisplay
				.setText(project.getNombre() + " > " + book.getNombre() + " > " + chapter.getNombre());
				loadScenes();
				createContextMenu();
			}
		});

		backButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ChapterInsideBookView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane = fxmlLoader.load();
				} catch (IOException e) {
				}

				ChapterInsideBookViewController chapterInsideBookViewController = fxmlLoader.getController();
				chapterInsideBookViewController.setProject(project);
				chapterInsideBookViewController.setBook(book);
				chapterInsideBookViewController.setController(mainViewController);

				mainViewController.setView(borderPane);
			}
		});
	}

	/**
	 * Método para mostrar la escena en el panel
	 * 
	 * @param e Escena de entrada
	 * @throws IOException 
	 */
	private void convertSceneToPane(Escena e) throws IOException {

		if (e == null) return;

		// Crea i carga el contendor (Pane) donde va la información
		Pane scenePane = FXMLLoader.load(getClass().getResource("/view/StandardPane.fxml"));
		FlowPane.setMargin(scenePane,	new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		//Carga la imagen del Pane				
		((ImageView) scenePane.getChildren().get(0)).setImage(new Image("resources/scene_icon.png"));

		//Ponemos texto a las labels que queramos
		if (e.getNombre().length() > MAX_LENGTH) {
			((Label) scenePane.getChildren().get(1)).setText("Nombre: " + e.getNombre().substring(0, MAX_LENGTH) + "...");
		} else {			
			((Label) scenePane.getChildren().get(1)).setText("Nombre: " + e.getNombre());
		}

		//Añade el Pane al flowPane
		sceneFlowPane.getChildren().add(scenePane);

		//Define un evento para ejecutarse cuando se hace click
		scenePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {				
				selectedScene = e;
				selectedSceneLabel.setText("Escena seleccionada: " + selectedScene.getNombre());
				if (errorLabel.isVisible())	errorLabel.setVisible(false);

				if (event.getButton() == MouseButton.SECONDARY) contextMenu.show(scenePane, event.getScreenX(), event.getScreenY());
			}
		});

	}

	/**
	 * Método para cargar las escenas
	 */
	private void loadScenes() {
		sceneFlowPane.getChildren().clear();

		for (Escena e : chapter.getEscenas()) {
			try {
				convertSceneToPane(e);
			} catch (IOException e1) {
			}
		}
		addButtonPane();
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
		sceneFlowPane.getChildren().add(addPane);

		// Evento al hacer click al botón añadir
		addPane.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				Stage addSceneDialog = new Stage();

				addSceneDialog.initModality(Modality.APPLICATION_MODAL);
				addSceneDialog.initStyle(StageStyle.UNDECORATED);
				addSceneDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddSceneView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				AddSceneViewController addSceneViewController = fxmlLoader.getController();
				addSceneViewController.setChapter(chapter);

				Scene dialogScene = new Scene(dialogRoot, 400, 550);
				addSceneDialog.setScene(dialogScene);
				addSceneDialog.showAndWait();
				loadScenes();
				selectedScene = null;
				selectedSceneLabel.setText("Ningúna escena seleccionada");				
			}
		});
	}

	/**
	 * Metodo que añade items al menu contextual que aparece al hacer click derecho
	 */
	private void createContextMenu() {

		MenuItem viewItem = new MenuItem("Ver escena");
		MenuItem updateItem = new MenuItem("Actualizar escena");
		MenuItem deleteItem = new MenuItem("Borrar escena");

		// Evento al hacer click para visualizar
		viewItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedScene == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage addChapterDialog = new Stage();

				addChapterDialog.initModality(Modality.APPLICATION_MODAL);
				addChapterDialog.initStyle(StageStyle.UNDECORATED);
				addChapterDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DisplaySceneView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				DisplaySceneViewController displaySceneViewController = fxmlLoader.getController();
				displaySceneViewController.setScene(selectedScene);

				Scene dialogScene = new Scene(dialogRoot, 600, 440);
				addChapterDialog.setScene(dialogScene);
				addChapterDialog.showAndWait();
				loadScenes();
				selectedScene = null;
				selectedSceneLabel.setText("Ningúna escena seleccionada");		
			}
		});

		// Evento al hacer click para actualizar
		updateItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedScene == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage addChapterDialog = new Stage();

				addChapterDialog.initModality(Modality.APPLICATION_MODAL);
				addChapterDialog.initStyle(StageStyle.UNDECORATED);
				addChapterDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateSceneView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				UpdateSceneViewController updateSceneViewController = fxmlLoader.getController();
				updateSceneViewController.setChapter(chapter);
				updateSceneViewController.setScene(selectedScene);

				Scene dialogScene = new Scene(dialogRoot, 400, 550);
				addChapterDialog.setScene(dialogScene);
				addChapterDialog.showAndWait();
				loadScenes();
				selectedScene = null;
				selectedSceneLabel.setText("Ningúna escena seleccionada");			
			}
		});

		// Evento al hacer click para borrar
		deleteItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedScene == null) {
					errorLabel.setVisible(true);
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Eliminacion de capitulo");
				alert.setHeaderText("Estas a punto de eliminar la escena");
				alert.setContentText("Estas seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if (resultado.get() == ButtonType.OK) {

					chapter.getEscenas().remove(selectedScene);
					DAOManager.getEscenaDAO().removeEscena(selectedScene.getId());
					DAOManager.getCapituloDAO().updateCapitulo(chapter);

					selectedScene = null;
					selectedSceneLabel.setText("Ninguna escena seleccionada");
					loadScenes();
				}			
			}
		});

		contextMenu.setStyle("-fx-background-color: black");
		contextMenu.getItems().addAll(viewItem, updateItem, deleteItem);
	}	

	/**
	 * Método para seleccionar el proyecto
	 * 
	 * @param p Proyecto de entrada
	 */
	public void setProject(Proyecto p) {
		project = p;
	}

	/**
	 * Método para seleccionar el libro
	 * 
	 * @param l Libro de entrada
	 */
	public void setBook(Libro l) {
		book = l;
	}

	/**
	 * Método para seleccionar el capítulo
	 * 
	 * @param c Capítulo de entrada
	 */
	public void setChapter(Capitulo c) {
		chapter = c;
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
