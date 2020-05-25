package view.controllers;

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
import model.Localidad;
import view.Main;

/**
 * Controlador de la vista general de las localizaciones
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class LocationViewController implements Initializable {

	private static final int MAX_LENGTH = 20;
	private static final int[] IMAGE_FIT = { 200, 230 };
	private static final int IMAGE_LAYOUT[] = { 45, 22 };
	private static final int ADD_IMAGE_Y = 62;
	private static final int[] FLOWPANE_MARGIN = { 10, 8, 20, 8 };

	@FXML public Pane projectButton;
	@FXML public Pane bookButton;
	@FXML public Pane characterButton;
	@FXML public Label errorLabel;
	@FXML public Label selectedLocationLabel;
	@FXML public FlowPane locationFlowPane;

	private MainViewController mainViewController;
	private Localidad selectedLocation;
	private ContextMenu contextMenu = new ContextMenu();	

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		locationFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		locationFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		addLocationsFromDB();
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
	 * @throws IOException 
	 */
	private void convertLocationToPane(Localidad l) throws IOException {

		if (l == null) return;

		// Crea i carga el contendor (Pane) donde va la información
		Pane locationPane = FXMLLoader.load(getClass().getResource("/view/StandardPane.fxml"));
		FlowPane.setMargin(locationPane,	new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		//Carga la imagen del Pane				
		((ImageView) locationPane.getChildren().get(0)).setImage(new Image("resources/localidad.png"));

		//Ponemos texto a las labels que queramos
		if (l.getNombre().length() > MAX_LENGTH) {
			((Label) locationPane.getChildren().get(1)).setText("Nombre: " + l.getNombre().substring(0, MAX_LENGTH) + "...");
		} else {			
			((Label) locationPane.getChildren().get(1)).setText("Nombre: " + l.getNombre());
		}

		//Añade el Pane al flowPane
		locationFlowPane.getChildren().add(locationPane);

		//Define un evento para ejecutarse cuando se hace click
		locationPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				selectedLocation = l;
				selectedLocationLabel.setText("Localizacion seleccionada: " + l.getNombre());
				if (errorLabel.isVisible())	errorLabel.setVisible(false);

				if (event.getButton() == MouseButton.SECONDARY) contextMenu.show(locationPane, event.getScreenX(), event.getScreenY());
			}
		});
	}

	/**
	 * Método para cargar las localizaciones de la base de datos
	 */
	private void addLocationsFromDB() {
		locationFlowPane.getChildren().clear();
		for (Localidad l : DAOManager.getLocalidadDAO().getLocalidades()) {
			try {
				convertLocationToPane(l);
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
		locationFlowPane.getChildren().add(addPane);

		// Evento al hacer click al botón añadir
		addPane.setOnMouseClicked(new EventHandler<Event>() {
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
	}

	/**
	 * Metodo que añade items al menu contextual que aparece al hacer click derecho
	 */
	private void createContextMenu() {

		MenuItem viewItem = new MenuItem("Ver localizacion");
		MenuItem updateItem = new MenuItem("Actualizar localizacion");
		MenuItem deleteItem = new MenuItem("Borrar localizacion");

		// Evento al hacer click para visualizar
		viewItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
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

		// Evento al hacer click para actualizar
		updateItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
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

		// Evento al hacer click para borrar
		deleteItem.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
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

					try {
						DAOManager.getLocalidadDAO().removeLocalidad(selectedLocation.getId());
					} catch (Exception e) {
						Alert errorAlert = new Alert(AlertType.ERROR);
						errorAlert.setTitle("Eliminación de localidad");
						errorAlert.setHeaderText("No ha sido posible eliminar la localidad");
						errorAlert.setContentText("No ha sido posible eliminar la localidad, puede que este asignada a alguna escena");
						errorAlert.showAndWait();
					}


					selectedLocation = null;
					selectedLocationLabel.setText("Ninguna localidad seleccionada");
					addLocationsFromDB();
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
