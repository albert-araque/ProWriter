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
import model.Libro;
import model.Proyecto;
import view.Main;

/**
 * Controlador de la vista de los capitulo dentro de un libro
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class ChapterInsideBookViewController implements Initializable {

	private static final int MAX_LENGTH = 20;
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int ADD_IMAGE_Y = 62;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML public Label selectedChapterLabel;
	@FXML public Label errorLabel;
	@FXML public Label bookNameDisplay;
	@FXML public Button backButton;
	@FXML public Button updateChapterButton;
	@FXML public Button deleteChapterButton;
	@FXML public Button displayChapterButton;
	@FXML public FlowPane chapterFlowPane;

	private MainViewController mainViewController;
	private Libro book;
	private Proyecto project;
	private ContextMenu contextMenu = new ContextMenu();
	private Capitulo selectedChapter;

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		chapterFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		chapterFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				bookNameDisplay.setText(project.getNombre() + " > " + book.getNombre());
				loadChapters();
				createContextMenu();
			}
		});

		backButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InsideBookView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane = fxmlLoader.load();
				} catch (IOException e) {
				}

				InsideBookViewController insideBookViewController = fxmlLoader.getController();
				insideBookViewController.setProject(project);
				insideBookViewController.setBook(book);
				insideBookViewController.setController(mainViewController);

				mainViewController.setView(borderPane);
			}
		});
	}
	
	/**
	 * Método que coge los capitulos del libro y los añade en forma de Pane al flowPane
	 * 
	 * @param c Capítulo de entrada
	 * @throws IOException 
	 */
	private void convertChapterToPane(Capitulo c) throws IOException {
		
		if (c == null) return;

		// Crea i carga el contendor (Pane) donde va la información
		Pane chapterPane = FXMLLoader.load(getClass().getResource("/view/StandardPane.fxml"));
		FlowPane.setMargin(chapterPane,	new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		//Carga la imagen del Pane		
		((ImageView) chapterPane.getChildren().get(0)).setImage(new Image("resources/capitulo_icono.png"));

		//Ponemos texto a las labels que queramos
		if (c.getNombre().length() > MAX_LENGTH) {
			((Label) chapterPane.getChildren().get(1)).setText("Nombre: " + c.getNombre().substring(0, MAX_LENGTH) + "...");
		} else {			
			((Label) chapterPane.getChildren().get(1)).setText("Nombre: " + c.getNombre());
		}

		//Añade el Pane al flowPane
		chapterFlowPane.getChildren().add(chapterPane);

		//Define un evento para ejecutarse cuando se hace click
		chapterPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				if (event.getButton() == MouseButton.SECONDARY) contextMenu.show(chapterPane, event.getScreenX(), event.getScreenY());

				if (event.getClickCount() == 1) {
					selectedChapter = c;
					selectedChapterLabel.setText("Capítulo seleccionado: " + selectedChapter.getNombre());
					if (errorLabel.isVisible())
						errorLabel.setVisible(false);
				}

				if (event.getClickCount() == 2) {

					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SceneChapterView.fxml"));
					BorderPane borderPane = null;
					try {
						borderPane = fxmlLoader.load();
					} catch (IOException e) {
					}

					SceneChapterViewController sceneChapterViewController = fxmlLoader.getController();
					sceneChapterViewController.setBook(book);
					sceneChapterViewController.setProject(project);
					sceneChapterViewController.setChapter(selectedChapter);
					sceneChapterViewController.setController(mainViewController);

					mainViewController.setView(borderPane);
				}
			}
		});
	}

	/**
	 * Método para cargar los capítulos
	 */
	private void loadChapters() {

		chapterFlowPane.getChildren().clear();

		for (Capitulo cap : book.getCapitulos()) {
			try {
				convertChapterToPane(cap);
			} catch (IOException e) {
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
		chapterFlowPane.getChildren().add(addPane);
		
		// Evento al hacer click al botón añadir
		addPane.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				Stage addChapterDialog = new Stage();

				addChapterDialog.initModality(Modality.APPLICATION_MODAL);
				addChapterDialog.initStyle(StageStyle.UNDECORATED);
				addChapterDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddChapterView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				AddChapterViewController addChapterViewController = fxmlLoader.getController();
				addChapterViewController.setBook(book);

				Scene dialogScene = new Scene(dialogRoot, 400, 400);
				addChapterDialog.setScene(dialogScene);
				addChapterDialog.showAndWait();
				loadChapters();
				selectedChapter = null;
				selectedChapterLabel.setText("Ningún capitulo seleccionado");			
			}
		});
	}
	
	/**
	 * Metodo que añade items al menu contextual que aparece al hacer click derecho
	 */
	private void createContextMenu() {
		
		MenuItem viewItem = new MenuItem("Ver capitulo");
		MenuItem updateItem = new MenuItem("Actualizar capitulo");
		MenuItem deleteItem = new MenuItem("Borrar capitulo");

		// Evento al hacer click para visualizar
		viewItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedChapter == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage displayChapterDialog = new Stage();

				displayChapterDialog.initModality(Modality.APPLICATION_MODAL);
				displayChapterDialog.initStyle(StageStyle.UNDECORATED);
				displayChapterDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DisplayChapterView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				DisplayChapterViewController displayChapterViewController = fxmlLoader.getController();
				displayChapterViewController.setChapter(selectedChapter);

				Scene dialogScene = new Scene(dialogRoot, 600, 415);
				displayChapterDialog.setScene(dialogScene);
				displayChapterDialog.showAndWait();
				selectedChapter = null;
				selectedChapterLabel.setText("Ningún capitulo seleccionado");	
			}
		});

		// Evento al hacer click para actualizar
		updateItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedChapter == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage updateChapterDialog = new Stage();

				updateChapterDialog.initModality(Modality.APPLICATION_MODAL);
				updateChapterDialog.initStyle(StageStyle.UNDECORATED);
				updateChapterDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateChapterView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				UpdateChapterViewController updateChapterViewController = fxmlLoader.getController();
				updateChapterViewController.setChapter(selectedChapter);

				Scene dialogScene = new Scene(dialogRoot, 400, 400);
				updateChapterDialog.setScene(dialogScene);
				updateChapterDialog.showAndWait();
				loadChapters();
				selectedChapter = null;
				selectedChapterLabel.setText("Ningún capitulo seleccionado");		
			}
		});

		// Evento al hacer click para borrar
		deleteItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedChapter == null) {
					errorLabel.setVisible(true);
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Eliminación de capítulo");
				alert.setHeaderText("Estás a punto de eliminar el capítulo");
				alert.setContentText("¿Estás seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if (resultado.get() == ButtonType.OK) {

					book.getCapitulos().remove(selectedChapter);
					DAOManager.getCapituloDAO().removeCapitulo(selectedChapter.getId());
					DAOManager.getLibroDAO().updateLibro(book);

					selectedChapter = null;
					selectedChapterLabel.setText("Ningún capitulo seleccionado");
					loadChapters();
				}		
			}
		});

		contextMenu.setStyle("-fx-background-color: black");
		contextMenu.getItems().addAll(viewItem, updateItem, deleteItem);
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
	 * Método para seleccionar el proyecto
	 * 
	 * @param p Proyecto de entrada
	 */
	public void setProject(Proyecto p) {
		project = p;
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
