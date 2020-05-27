package view.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import dao.DAOManager;
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
import javafx.scene.control.ScrollPane;
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
import view.Main;

/**
 * Controlador de la vista general de libros
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class BookViewController implements Initializable {

	private static final int MAX_LENGTH = 20;
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int ADD_IMAGE_Y = 62;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML public FlowPane bookFlowPane;
	@FXML public Button updateBookButton;
	@FXML public Button deleteBookButton;
	@FXML public Button displayBookButton;
	@FXML public Label errorLabel;
	@FXML public Label selectedBookLabel;
	@FXML public ScrollPane scrollPane;
	@FXML public ImageView projectIco;
	@FXML public ImageView bookIco;
	@FXML public ImageView characterIco;
	@FXML public ImageView locationIco;

	@FXML public Pane projectButton;
	@FXML public Pane characterButton;
	@FXML public Pane locationButton;

	private Libro selectedBook;
	private MainViewController mainViewController;
	private ContextMenu contextMenu = new ContextMenu();	

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		bookFlowPane.prefWidthProperty().bind(scrollPane.widthProperty());
		bookFlowPane.prefHeightProperty().bind(scrollPane.heightProperty());
		
		projectIco.setImage(new Image("resources/proyecto.png"));
		bookIco.setImage(new Image("resources/libro.png"));
		characterIco.setImage(new Image("resources/character_icon.png"));
		locationIco.setImage(new  Image("resources/localidad.png"));

		addBooksFromDB();
		createContextMenu();

		projectButton.setOnMouseClicked(new EventHandler<Event>() {
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
	 * Método que coge los libros de la base de datos y los añade en forma de Pane al flowPane
	 * 
	 * @param l Libro de entrada
	 * @throws IOException 
	 */
	public void convertBookToPane(Libro l) throws IOException {

		if (l == null) return;

		// Crea i carga el contendor (Pane) donde va la información
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

		//Añade el Pane al flowPane
		bookFlowPane.getChildren().add(bookPane);

		//Define un evento para ejecutarse cuando se hace click
		bookPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectedBook = l;
				selectedBookLabel.setText("Libro seleccionado: " + l.getNombre());
				if (errorLabel.isVisible())	errorLabel.setVisible(false);
				if (event.getButton() == MouseButton.SECONDARY) contextMenu.show(bookPane, event.getScreenX(), event.getScreenY());
			}
		});
	}

	/**
	 * Método para cargar los libros de la base de datos
	 */
	private void addBooksFromDB() {
		bookFlowPane.getChildren().clear();
		for (Libro l : DAOManager.getLibroDAO().getLibros()) {
			try {
				convertBookToPane(l);
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
		bookFlowPane.getChildren().add(addPane);

		// Evento al hacer click al botón añadir
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

				Scene dialogScene = new Scene(dialogRoot, 400, 750);
				addBookDialog.setScene(dialogScene);
				addBookDialog.showAndWait();
				selectedBook = null;
				selectedBookLabel.setText("Ningún libro seleccionado");
				addBooksFromDB();			
			}
		});
	}
	
	/**
	 * Metodo que añade items al menu contextual que aparece al hacer click derecho
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

				Stage updateBookDialog = new Stage();

				updateBookDialog.initModality(Modality.APPLICATION_MODAL);
				updateBookDialog.initStyle(StageStyle.UNDECORATED);
				updateBookDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateBookView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				UpdateBookViewController updateController = fxmlLoader.getController();
				updateController.setBook(selectedBook);

				Scene dialogScene = new Scene(dialogRoot, 400, 750);
				updateBookDialog.setScene(dialogScene);
				updateBookDialog.showAndWait();
				selectedBook = null;
				selectedBookLabel.setText("Ningún libro seleccionado");
				addBooksFromDB();			
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
				alert.setTitle("Eliminación de libro");
				alert.setHeaderText("Estás a punto de eliminar el libro de todos los proyectos");
				alert.setContentText("Estás seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if (resultado.get() == ButtonType.OK) {

					DAOManager.getLibroDAO().removeLibro(selectedBook.getId());

					selectedBook = null;
					selectedBookLabel.setText("Ningún libro seleccionado");
					addBooksFromDB();
				}			
			}
		});

		contextMenu.setStyle("-fx-background-color: black");
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