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
import model.Personaje;
import model.Proyecto;
import view.Main;

/**
 * Controlador de la vista de los personajes dentro de un libro
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class CharacterInsideBookViewController implements Initializable {

	private static final int MAX_LENGTH = 20;
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int ADD_IMAGE_Y = 62;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML public Label selectedCharacterLabel;
	@FXML public Label errorLabel;
	@FXML public Label bookNameDisplay;
	@FXML public Button backButton;
	@FXML public FlowPane characterFlowPane;

	private MainViewController mainViewController;
	private Libro book;
	private Proyecto project;
	private Personaje selectedCharacter;
	private ContextMenu contextMenu = new ContextMenu();	

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		characterFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		characterFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				bookNameDisplay.setText(project.getNombre() + " > " + book.getNombre());
				loadCharacters();
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
	 * Método que coge los personajes de la BD y los añade en forma de Pane al flowPane
	 * 
	 * @param p Personaje de entrada
	 * @throws IOException 
	 */
	private void convertCharacterToPane(Personaje p) throws IOException {

		if (p == null) return;

		// Crea i carga el contendor (Pane) donde va la información
		Pane characterPane = FXMLLoader.load(getClass().getResource("/view/StandardPane.fxml"));
		FlowPane.setMargin(characterPane,	new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		//Carga la imagen del Pane				
		File imageFile = new File(p.getImagen());
		if (p.getImagen() == null || p.getImagen().equals("") || !imageFile.exists()) {
			((ImageView) characterPane.getChildren().get(0)).setImage(new Image("resources/character_icon.png"));
		}					
		else {
			((ImageView) characterPane.getChildren().get(0)).setImage(new Image(imageFile.toURI().toString()));
		}

		//Ponemos texto a las labels que queramos
		if (p.getNombre().length() > MAX_LENGTH) {
			((Label) characterPane.getChildren().get(1)).setText("Nombre: " + p.getNombre().substring(0, MAX_LENGTH) + "...");
		} else {			
			((Label) characterPane.getChildren().get(1)).setText("Nombre: " + p.getNombre());
		}

		//Añade el Pane al flowPane
		characterFlowPane.getChildren().add(characterPane);

		//Define un evento para ejecutarse cuando se hace click
		characterPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectedCharacter = p;
				selectedCharacterLabel.setText("Personaje seleccionado: " + p.getNombre());
				if (errorLabel.isVisible())	errorLabel.setVisible(false);
				if (event.getButton() == MouseButton.SECONDARY) contextMenu.show(characterPane, event.getScreenX(), event.getScreenY());

			}
		});
	}
	
	/**
	 * Método para cargar los personajes
	 */
	private void loadCharacters() {
		characterFlowPane.getChildren().clear();

		for (Personaje p : book.getPersonajes()) {
			try {
				convertCharacterToPane(p);
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
		characterFlowPane.getChildren().add(addPane);
		
		// Evento al hacer click al botón añadir
		addPane.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				Stage addCharacterDialog = new Stage();

				addCharacterDialog.initModality(Modality.APPLICATION_MODAL);
				addCharacterDialog.initStyle(StageStyle.UNDECORATED);
				addCharacterDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddCharacterView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				AddCharacterViewController addController = fxmlLoader.getController();
				addController.setBook(book);

				Scene dialogScene = new Scene(dialogRoot, 400, 600);
				addCharacterDialog.setScene(dialogScene);
				addCharacterDialog.showAndWait();
				selectedCharacter = null;
				selectedCharacterLabel.setText("Ningún personaje seleccionado");
				loadCharacters();				
			}
		});
	}
	
	/**
	 * Metodo que añade items al menu contextual que aparece al hacer click derecho
	 */
	private void createContextMenu() {

		MenuItem viewItem = new MenuItem("Ver personaje");
		MenuItem updateItem = new MenuItem("Actualizar personaje");
		MenuItem deleteItem = new MenuItem("Borrar personaje");

		// Evento al hacer click para visualizar
		viewItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedCharacter == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage displayCharacterDialog = new Stage();

				displayCharacterDialog.initModality(Modality.APPLICATION_MODAL);
				displayCharacterDialog.initStyle(StageStyle.UNDECORATED);
				displayCharacterDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DisplayCharacterView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				DisplayCharacterViewController displayController = fxmlLoader.getController();
				displayController.setCharacter(selectedCharacter);

				Scene dialogScene = new Scene(dialogRoot, 600, 630);
				displayCharacterDialog.setScene(dialogScene);
				displayCharacterDialog.showAndWait();
				selectedCharacter = null;
				selectedCharacterLabel.setText("Ningún personaje seleccionado");	
			}
		});

		// Evento al hacer click para actualizar
		updateItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedCharacter == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage updateCharacterDialog = new Stage();

				updateCharacterDialog.initModality(Modality.APPLICATION_MODAL);
				updateCharacterDialog.initStyle(StageStyle.UNDECORATED);
				updateCharacterDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateCharacterView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				UpdateCharacterViewController updateController = fxmlLoader.getController();
				updateController.setPersonaje(selectedCharacter);

				Scene dialogScene = new Scene(dialogRoot, 400, 600);
				updateCharacterDialog.setScene(dialogScene);
				updateCharacterDialog.showAndWait();
				selectedCharacter = null;
				selectedCharacterLabel.setText("Ningún personaje seleccionado");
				loadCharacters();		
			}
		});

		// Evento al hacer click para borrar
		deleteItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				if (selectedCharacter == null) {
					errorLabel.setVisible(true);
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Eliminación de personaje");
				alert.setHeaderText("Estás a punto de eliminar el personaje");
				alert.setContentText("¿Estás seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if (resultado.get() == ButtonType.OK) {

					book.getPersonajes().remove(selectedCharacter);
					for (Personaje p : DAOManager.getLibroDAO().getLibro(book.getId()).getPersonajes()) {
						System.out.println(p);
					}
					DAOManager.getLibroDAO().updateLibro(book);

					selectedCharacter = null;
					selectedCharacterLabel.setText("Ningún personaje seleccionado");
					loadCharacters();
				}			
			}
		});

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
	 * Método para seleccionar el controlador
	 * 
	 * @param controller Controlador de entrada
	 */
	public void setController(MainViewController controller) {
		mainViewController = controller;
	}
}