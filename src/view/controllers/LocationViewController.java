package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.util.Optional;
import dao.DAOManager;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import model.Localidad;
import view.Main;

/**
 * Clase que contiene la vista general de las localizaciones
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class LocationViewController implements Initializable {

	private static final int MAX_LENGTH = 20;
	private static final int[] PANE_SIZE = { 290, 340 };
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int FONT_SIZE = 14;
	private static final int LABEL_XLAY = 32;
	private static final int NLABEL_YLAY = 275;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML
	public Pane projectButton;
	@FXML
	public Pane bookButton;
	@FXML
	public Pane characterButton;
	@FXML
	public Label errorLabel;

	@FXML
	public Button addLocationButton;
	@FXML
	public Button updateLocationButton;
	@FXML
	public Button deleteLocationButton;
	@FXML
	public Button displayLocationButton;

	@FXML
	public Label selectedLocationLabel;
	@FXML
	public FlowPane locationFlowPane;

	private MainViewController mainViewController;

	private Pane locationPane;
	private Localidad selectedLocation;

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		locationFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		locationFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		addLocationsFromDB();

		// Evento al hacer click al botón añadir
		addLocationButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				Stage addLocationDialog = new Stage();

				addLocationDialog.initModality(Modality.APPLICATION_MODAL);
				addLocationDialog.initStyle(StageStyle.UNDECORATED);
				addLocationDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddLocationView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				Scene dialogScene = new Scene(dialogRoot, 400, 350);
				addLocationDialog.setScene(dialogScene);
				addLocationDialog.showAndWait();
				selectedLocation = null;
				selectedLocationLabel.setText("Ninguna localidad seleccionada");
				addLocationsFromDB();
			}
		});

		// Evento al hacer click al botón actualizar
		updateLocationButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (selectedLocation == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage updateLocationDialog = new Stage();

				updateLocationDialog.initModality(Modality.APPLICATION_MODAL);
				updateLocationDialog.initStyle(StageStyle.UNDECORATED);
				updateLocationDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateLocationView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				UpdateLocationViewController updateController = fxmlLoader.getController();
				updateController.setLocation(selectedLocation);

				Scene dialogScene = new Scene(dialogRoot, 400, 350);
				updateLocationDialog.setScene(dialogScene);
				updateLocationDialog.showAndWait();
				selectedLocation = null;
				selectedLocationLabel.setText("Ninguna localidad seleccionada");
				addLocationsFromDB();
			}
		});

		deleteLocationButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (selectedLocation == null) {
					errorLabel.setVisible(true);
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Eliminación de localidad");
				alert.setHeaderText("Estás a punto de eliminar la localidad de todos los proyectos");
				alert.setContentText("¿Estás seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if (resultado.get() == ButtonType.OK) {

					DAOManager.getLocalidadDAO().removeLocalidad(selectedLocation.getId());

					selectedLocation = null;
					selectedLocationLabel.setText("Ninguna localidad seleccionada");
					addLocationsFromDB();
				}
			}
		});

		displayLocationButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (selectedLocation == null) {
					errorLabel.setVisible(true);
					return;
				}

				Stage displayLocationDialog = new Stage();

				displayLocationDialog.initModality(Modality.APPLICATION_MODAL);
				displayLocationDialog.initStyle(StageStyle.UNDECORATED);
				displayLocationDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DisplayLocationView.fxml"));
				BorderPane dialogRoot = null;

				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				DisplayLocationViewController displayController = fxmlLoader.getController();
				displayController.setLocation(selectedLocation);

				Scene dialogScene = new Scene(dialogRoot, 600, 415);
				displayLocationDialog.setScene(dialogScene);
				displayLocationDialog.showAndWait();
				selectedLocation = null;
				selectedLocationLabel.setText("Ninguna localidad seleccionada");
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

	}

	/**
	 * Método para mostrar la localización en el panel
	 * 
	 * @param l Localización de entrada
	 */
	public void convertLocationToPane(Localidad l) {

		if (l == null)
			return;

		// Crea el contenedor (Pane) donde va la información
		locationPane = new Pane();

		Label nameLabel = new Label();
		Label locationLabel = new Label();

		ImageView locationImage = new ImageView("resources/localidad.png");

		// Establece el margen de cada contenedor
		FlowPane.setMargin(locationPane,
				new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		// Establece las medidas del contenedor y todo lo que haya dentro de éste
		locationPane.setPrefSize(PANE_SIZE[0], PANE_SIZE[1]);
		locationPane.getStyleClass().add("pane");

		locationImage.setFitHeight(IMAGE_FIT[0]);
		locationImage.setFitWidth(IMAGE_FIT[1]);
		locationImage.setLayoutX(IMAGE_LAYOUT[0]);
		locationImage.setLayoutY(IMAGE_LAYOUT[1]);
		locationImage.setPickOnBounds(true);
		locationImage.setPreserveRatio(true);

		if (l.getNombre().length() > MAX_LENGTH)
			nameLabel.setText("Nombre: " + l.getNombre().substring(0, MAX_LENGTH) + "...");
		else
			nameLabel.setText("Nombre: " + l.getNombre());
		nameLabel.setLayoutX(LABEL_XLAY);
		nameLabel.setLayoutY(NLABEL_YLAY);
		nameLabel.setFont(new Font(FONT_SIZE));

		locationLabel.setLayoutX(LABEL_XLAY);
		locationLabel.setLayoutY(NLABEL_YLAY + 10);

		locationPane.getChildren().add(nameLabel);
		locationPane.getChildren().add(locationImage);
		locationFlowPane.getChildren().add(locationPane);

		// Evento para que quede seleccionado proyecto y contenedor al hacer click sobre
		// un contenedor de proyecto
		locationPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				if (event.getClickCount() == 1) {
					selectedLocation = l;
					selectedLocationLabel.setText("Localizacion seleccionada: " + l.getNombre());
					if (errorLabel.isVisible())
						errorLabel.setVisible(false);
				}
			}
		});
	}

	/**
	 * Método para cargar las localizaciones de la base de datos
	 */
	private void addLocationsFromDB() {
		locationFlowPane.getChildren().clear();
		for (Localidad l : DAOManager.getLocalidadDAO().getLocalidades()) {
			convertLocationToPane(l);
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
