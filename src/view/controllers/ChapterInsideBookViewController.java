package view.controllers;

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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
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
	private static final int[] PANE_SIZE = { 290, 340 };
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int FONT_SIZE = 14;
	private static final int LABEL_XLAY = 32;
	private static final int NLABEL_YLAY = 230;
	private static final int OLABEL_YLAY = 250;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML public Label selectedChapterLabel;
	@FXML public Label errorLabel;
	@FXML public Label bookNameDisplay;
	@FXML public Button backButton;
	@FXML public Button addChapterButton;
	@FXML public Button updateChapterButton;
	@FXML public Button deleteChapterButton;
	@FXML public Button displayChapterButton;
	@FXML public FlowPane chapterFlowPane;

	private MainViewController mainViewController;
	private Libro book;
	private Proyecto project;
	private Pane chapterPane;
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
			}
		});

		addChapterButton.setOnMouseClicked(new EventHandler<Event>() {
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

		updateChapterButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

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

		deleteChapterButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

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

		displayChapterButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

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
	 * Método para cargar los capítulos
	 */
	private void loadChapters() {

		chapterFlowPane.getChildren().clear();

		for (Capitulo cap : book.getCapitulos()) {
			convertChapterToPane(cap);
		}
	}

	/**
	 * Método que coge los capitulos del libro y los añade en forma de Pane al flowPane
	 * 
	 * @param c Capítulo de entrada
	 */
	private void convertChapterToPane(Capitulo c) {

		if (c == null)
			return;
		chapterPane = new Pane();

		Label nameLabel = new Label();
		Label orderLabel = new Label();

		ImageView chapterImage = new ImageView("resources/capitulo_icono.png");

		// Establece el margen de cada contenedor
		FlowPane.setMargin(chapterPane,
				new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		// Establece las medidas del contenedor y todo lo que haya dentro de éste
		chapterPane.setPrefSize(PANE_SIZE[0], PANE_SIZE[1]);
		chapterPane.getStyleClass().add("pane");

		chapterImage.setFitHeight(IMAGE_FIT[0]);
		chapterImage.setFitWidth(IMAGE_FIT[1]);
		chapterImage.setLayoutX(IMAGE_LAYOUT[0]);
		chapterImage.setLayoutY(IMAGE_LAYOUT[1]);
		chapterImage.setPickOnBounds(true);
		chapterImage.setPreserveRatio(true);

		if (c.getNombre().length() > MAX_LENGTH)
			nameLabel.setText("Nombre: " + c.getNombre().substring(0, MAX_LENGTH) + "...");
		else
			nameLabel.setText("Nombre: " + c.getNombre());
		nameLabel.setLayoutX(LABEL_XLAY);
		nameLabel.setLayoutY(NLABEL_YLAY);
		nameLabel.setFont(new Font(FONT_SIZE));

		orderLabel.setText("Numero de capitulo: " + c.getNumero());
		orderLabel.setLayoutX(LABEL_XLAY);
		orderLabel.setLayoutY(OLABEL_YLAY);
		orderLabel.setFont(new Font(FONT_SIZE));

		chapterPane.getChildren().add(nameLabel);
		chapterPane.getChildren().add(orderLabel);
		chapterPane.getChildren().add(chapterImage);
		chapterFlowPane.getChildren().add(chapterPane);

		chapterPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

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
