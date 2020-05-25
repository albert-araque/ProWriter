package view.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import dao.DAOManager;
import javafx.application.Platform;
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
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
	private static final int[] PANE_SIZE = { 290, 340 };
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int ADD_IMAGE_Y = 62;
	private static final int FONT_SIZE = 14;
	private static final int LABEL_XLAY = 32;
	private static final int NLABEL_YLAY = 275;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML public Label selectedCharacterLabel;
	@FXML public Label errorLabel;
	@FXML public Label bookNameDisplay;
	@FXML public Button backButton;
	@FXML public Button updateCharacterButton;
	@FXML public Button deleteCharacterButton;
	@FXML public Button displayCharacterButton;
	@FXML public FlowPane characterFlowPane;

	private MainViewController mainViewController;
	private Libro book;
	private Proyecto project;
	private Pane characterPane;
	private Personaje selectedCharacter;

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
			}
		});

		updateCharacterButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

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

		deleteCharacterButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
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

		displayCharacterButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

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
	 * Método para cargar los personajes
	 */
	private void loadCharacters() {
		characterFlowPane.getChildren().clear();

		for (Personaje p : book.getPersonajes()) {
			convertCharacterToPane(p);
		}
		addButtonPane();
	}

	/**
	 * Método que coge los personajes de un libro y los añade en forma de Pane al flowPane
	 * 
	 * @param p Personaje de entrada
	 */
	private void convertCharacterToPane(Personaje p) {

		

		if (p == null)
			return;
		characterPane = new Pane();

		Label nameLabel = new Label();
		ImageView characterImage;
		File imageFile = null;

		try {
			imageFile = new File(p.getImagen());
		} catch (Exception e) {
		}
		if (p.getImagen() == null || p.getImagen().equals("") || !imageFile.exists())
			characterImage = new ImageView("resources/character_icon.png");
		else {
			characterImage = new ImageView(new Image(imageFile.toURI().toString()));
		}

		// Establece el margen de cada contenedor
		FlowPane.setMargin(characterPane,
				new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		// Establece las medidas del contenedor y todo lo que haya dentro de éste
		characterPane.setPrefSize(PANE_SIZE[0], PANE_SIZE[1]);
		characterPane.getStyleClass().add("pane");

		characterImage.setFitHeight(IMAGE_FIT[0]);
		characterImage.setFitWidth(IMAGE_FIT[1]);
		characterImage.setLayoutX(IMAGE_LAYOUT[0]);
		characterImage.setLayoutY(IMAGE_LAYOUT[1]);
		characterImage.setPreserveRatio(true);
		characterImage.setPickOnBounds(true);

		if (p.getNombre().length() > MAX_LENGTH)
			nameLabel.setText("Nombre: " + p.getNombre().substring(0, MAX_LENGTH) + "...");
		else
			nameLabel.setText("Nombre: " + p.getNombre());
		nameLabel.setLayoutX(LABEL_XLAY);
		nameLabel.setLayoutY(NLABEL_YLAY);
		nameLabel.setFont(new Font(FONT_SIZE));

		characterPane.getChildren().add(nameLabel);
		characterPane.getChildren().add(characterImage);
		characterFlowPane.getChildren().add(characterPane);

		characterPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectedCharacter = p;
				selectedCharacterLabel.setText("Personaje seleccionado: " + selectedCharacter.getNombre());
				if (errorLabel.isVisible())
					errorLabel.setVisible(false);
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
