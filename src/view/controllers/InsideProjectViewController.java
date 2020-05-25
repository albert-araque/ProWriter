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
import model.Libro;
import model.Proyecto;
import view.Main;

/**
 * Controlador de la vista que permite acceder a los libros que hay en un proyecto
 * 
 * @author Albert Araque, Francisco Jos� Ruiz
 * @version 1.0
 */
public class InsideProjectViewController implements Initializable {

	private static final int MAX_LENGTH = 20;
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int ADD_IMAGE_Y = 62;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML public FlowPane bookFlowPane;
	@FXML public Button backButton;
	@FXML public Label errorLabel;
	@FXML public Label selectedBookLabel;
	@FXML public Label projectNameDisplay;

	private MainViewController mainViewController;
	private Proyecto project;
	private Libro selectedBook;
	private ContextMenu contextMenu = new ContextMenu();	

	/**
	 * M�todo para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		bookFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		bookFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				projectNameDisplay.setText(project.getNombre());
				loadBooks();
				createContextMenu();
			}
		});

		backButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProjectView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane = fxmlLoader.load();
				} catch (IOException e) {
				}

				ProjectViewController projectViewController = fxmlLoader.getController();
				projectViewController.setController(mainViewController);

				mainViewController.setView(borderPane);

			}
		});	

	}

	/**
	 * M�todo para mostrar el libro en el panel
	 * 
	 * @param l Libro de entrada
	 * @throws IOException 
	 */
	public void convertBookToPane(Libro l) throws IOException {

		if (l == null) return;

		// Crea i carga el contendor (Pane) donde va la informaci�n
		Pane bookPane = FXMLLoader.load(getClass().getResource("/view/StandardPane.fxml"));
		FlowPane.setMargin(bookPane,	new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));
		
		//Carga la imagen del Pane				
		File imageFile = new File(l.getImagen());
		if (l.getImagen() == null || l.getImagen().equals("") || !imageFile.exists())
			((ImageView) bookPane.getChildren().get(0)).setImage(new Image("resources/libro.png"));
		else {
			((ImageView) bookPane.getChildren().get(0)).setImage(new Image(imageFile.toURI().toString()));
		}

		//Ponemos texto a las labels que queramos
		if (l.getNombre().length() > MAX_LENGTH) {
			((Label) bookPane.getChildren().get(1)).setText("Nombre: " + l.getNombre().substring(0, MAX_LENGTH) + "...");
		} else {			
			((Label) bookPane.getChildren().get(1)).setText("Nombre: " + l.getNombre());
		}

		//A�ade el Pane al flowPane
		bookFlowPane.getChildren().add(bookPane);
		
		//Define un evento para ejecutarse cuando se hace click
		bookPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				if (event.getButton() == MouseButton.SECONDARY) contextMenu.show(bookPane, event.getScreenX(), event.getScreenY());

				if (event.getClickCount() == 1) {
					selectedBook = l;
					selectedBookLabel.setText("Libro seleccionado: " + l.getNombre());
					if (errorLabel.isVisible())
						errorLabel.setVisible(false);
				}

				if (event.getClickCount() == 2) {

					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InsideBookView.fxml"));
					BorderPane borderPane = null;
					try {
						borderPane = fxmlLoader.load();
					} catch (IOException e) {
					}

					InsideBookViewController insideBookViewController = fxmlLoader.getController();
					insideBookViewController.setProject(project);
					insideBookViewController.setBook(selectedBook);
					insideBookViewController.setController(mainViewController);

					mainViewController.setView(borderPane);
				}
			}
		});
	}

	/**
	 * M�todo para cargar los libros en el panel
	 */
	private void loadBooks() {
		bookFlowPane.getChildren().removeAll(bookFlowPane.getChildren());

		for (Libro l : project.getLibros()) {
			try {
				convertBookToPane(l);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		addButtonPane();
	}
	
	/**
	 * M�todo que a�ade un Pane para a�adir un objeto a la BD
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
		bookFlowPane.getChildren().add(addPane);
		
		// Evento al hacer click al bot�n a�adir
		addPane.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				Stage addBookDialog = new Stage();

				addBookDialog.initModality(Modality.APPLICATION_MODAL);
				addBookDialog.initStyle(StageStyle.UNDECORATED);
				addBookDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddBookView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				AddBookViewController addController = fxmlLoader.getController();
				addController.setProject(project);

				Scene dialogScene = new Scene(dialogRoot, 400, 750);
				addBookDialog.setScene(dialogScene);
				addBookDialog.showAndWait();
				selectedBook = null;
				selectedBookLabel.setText("Ning�n libro seleccionado");
				loadBooks();				
			}
		});
	}
	
	/**
	 * Metodo que a�ade items al menu contextual que aparece al hacer click derecho
	 */
	private void createContextMenu() {
		
		MenuItem viewItem = new MenuItem("Ver libro");
		MenuItem updateItem = new MenuItem("Actualizar libro");
		MenuItem deleteItem = new MenuItem("Borrar libro");

		// Evento al hacer click para visualizar
		viewItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedBook == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage displayBookDialog = new Stage();
				displayBookDialog.initModality(Modality.APPLICATION_MODAL);
				displayBookDialog.initStyle(StageStyle.UNDECORATED);
				displayBookDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DisplayBookView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				DisplayBookViewController displayController = fxmlLoader.getController();
				displayController.setBook(selectedBook);

				Scene dialogScene = new Scene(dialogRoot, 600, 770);
				displayBookDialog.setScene(dialogScene);
				displayBookDialog.showAndWait();
				selectedBook = null;
				selectedBookLabel.setText("Ningun libro seleccionado");			
			}
		});

		// Evento al hacer click para actualizar
		updateItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {

				if (selectedBook == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage updateProjectDialog = new Stage();

				updateProjectDialog.initModality(Modality.APPLICATION_MODAL);
				updateProjectDialog.initStyle(StageStyle.UNDECORATED);
				updateProjectDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateBookView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				UpdateBookViewController updateController = fxmlLoader.getController();
				updateController.setBook(selectedBook);

				Scene dialogScene = new Scene(dialogRoot, 400, 750);
				updateProjectDialog.setScene(dialogScene);
				updateProjectDialog.showAndWait();
				selectedBook = null;
				selectedBookLabel.setText("Ning�n libro seleccionado");
				loadBooks();			
			}
		});

		// Evento al hacer click para borrar
		deleteItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedBook == null) {
					errorLabel.setVisible(true);
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Eliminaci�n de libro");
				alert.setHeaderText("Est�s a punto de eliminar el libro");
				alert.setContentText("�Est�s seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if (resultado.get() == ButtonType.OK) {

					// Esta l�nea elimina el libro seleccionado del set de libros del proyecto
					project.getLibros().remove(selectedBook);

					// Ahora solo queda reflejarlo en la base de datos, con un update
					DAOManager.getProyectoDAO().updateProyecto(project);

					selectedBook = null;
					selectedBookLabel.setText("Ning�n libro seleccionado");
					loadBooks();
				}				
			}
		});

		contextMenu.getItems().addAll(viewItem, updateItem, deleteItem);
	}

	/**
	 * M�todo para seleccionar el controlador
	 * 
	 * @param controller Controlador de entrada
	 */
	public void setController(MainViewController controller) {
		mainViewController = controller;
	}

	/**
	 * M�todo para seleccionar el proyecto
	 * 
	 * @param p Proyecto de entrada
	 */
	public void setProyecto(Proyecto p) {
		project = p;
	}

}