package view.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import dao.DAOManager;
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
import view.Main;

/**
 * Clase que contiene la vista general de los libros
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class BookViewController implements Initializable {

	private static final int MAX_LENGTH = 20;
	private static final int[] PANE_SIZE = { 290, 340 };
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int FONT_SIZE = 14;
	private static final int LABEL_XLAY = 32;
	private static final int NLABEL_YLAY = 275;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML
	public FlowPane bookFlowPane;
	@FXML
	public Button addBookButton;
	@FXML
	public Button updateBookButton;
	@FXML
	public Button deleteBookButton;
	@FXML
	public Button displayBookButton;
	@FXML
	public Label errorLabel;
	@FXML
	public Label selectedBookLabel;

	@FXML
	public Pane projectButton;
	@FXML
	public Pane characterButton;
	@FXML
	public Pane locationButton;

	private Pane bookPane;
	private Libro selectedBook;
	private MainViewController mainViewController;

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		bookFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		bookFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		addBooksFromDB();

		// Evento al hacer click al botón añadir
		addBookButton.setOnMouseClicked(new EventHandler<Event>() {
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
				selectedBookLabel.setText("Ningï¿½n libro seleccionado");
				addBooksFromDB();
			}
		});

		// Evento al hacer click al botón actualizar
		updateBookButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

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
				selectedBookLabel.setText("Ningï¿½n libro seleccionado");
				addBooksFromDB();
			}
		});

		// Evento al hacer click al botón eliminar
		deleteBookButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (selectedBook == null) {
					errorLabel.setVisible(true);
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Eliminaciï¿½n de libro");
				alert.setHeaderText("Estï¿½s a punto de eliminar el libro de todos los proyectos");
				alert.setContentText("ï¿½Estï¿½s seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if (resultado.get() == ButtonType.OK) {

					DAOManager.getLibroDAO().removeLibro(selectedBook.getId());

					selectedBook = null;
					selectedBookLabel.setText("Ningï¿½n libro seleccionado");
					addBooksFromDB();
				}
			}
		});

		// Evento al hacer click al botón ver
		displayBookButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
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
	 * Método para mostrar el libro en el panel
	 * 
	 * @param l Libro de entrada
	 */
	public void convertBookToPane(Libro l) {

		if (l == null)
			return;

		// Crea el contenedor (Pane) donde va la información
		bookPane = new Pane();

		// Si el libro tiene una imagen, la añade, si no, coge una por defecto
		ImageView projectImage;
		File imageFile = null;

		try {
			imageFile = new File(l.getImagen());
		} catch (Exception e) {
		}
		if (l.getImagen() == null || l.getImagen().equals("") || !imageFile.exists())
			projectImage = new ImageView("resources/libro.png");
		else {
			projectImage = new ImageView(new Image(imageFile.toURI().toString()));
		}

		Label nameLabel = new Label();
		Label characterLabel = new Label();

		// Establece el margen de cada contenedor
		FlowPane.setMargin(bookPane,
				new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		// Establece las medidas del contenedor y todo lo que haya dentro de éste
		bookPane.setPrefSize(PANE_SIZE[0], PANE_SIZE[1]);
		bookPane.getStyleClass().add("pane");

		projectImage.setFitHeight(IMAGE_FIT[0]);
		projectImage.setFitWidth(IMAGE_FIT[1]);
		projectImage.setLayoutX(IMAGE_LAYOUT[0]);
		projectImage.setLayoutY(IMAGE_LAYOUT[1]);
		projectImage.setPickOnBounds(true);
		projectImage.setPreserveRatio(true);

		if (l.getNombre().length() > MAX_LENGTH)
			nameLabel.setText("Nombre: " + l.getNombre().substring(0, MAX_LENGTH) + "...");
		else
			nameLabel.setText("Nombre: " + l.getNombre());
		nameLabel.setLayoutX(LABEL_XLAY);
		nameLabel.setLayoutY(NLABEL_YLAY);
		nameLabel.setFont(new Font(FONT_SIZE));

		characterLabel.setText("Numero de personajes: " + l.getPersonajes().size());
		characterLabel.setLayoutX(LABEL_XLAY);
		characterLabel.setLayoutY(NLABEL_YLAY + 10);

		bookPane.getChildren().add(nameLabel);
		bookPane.getChildren().add(projectImage);
		bookFlowPane.getChildren().add(bookPane);

		// Evento para seleccionar el objeto
		bookPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				selectedBook = l;
				selectedBookLabel.setText("Libro seleccionado: " + l.getNombre());
				if (errorLabel.isVisible())
					errorLabel.setVisible(false);

			}
		});
	}

	/**
	 * Método para cargar los libros de la base de datos
	 */
	private void addBooksFromDB() {
		bookFlowPane.getChildren().clear();
		for (Libro l : DAOManager.getLibroDAO().getLibros()) {
			convertBookToPane(l);
		}
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
