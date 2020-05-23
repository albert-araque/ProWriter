package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.io.File;
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
import model.Personaje;
import view.Main;

public class CharacterViewController implements Initializable {
	
	private static final int MAX_LENGHT = 20;
	private static final int[] PANE_SIZE = {290, 340};
	private static final int[] IMAGE_FIT = {200, 230};
	private static final int IMAGE_LAYOUT[] = {45, 22};
	private static final int FONT_SIZE = 14;
	private static final int LABEL_XLAY = 32;
	private static final int NLABEL_YLAY = 275;
	private static final int[] FLOWPANE_MARGIN = {10, 8, 20, 8};

	@FXML public Pane projectButton;
	@FXML public Pane bookButton;
	@FXML public Pane locationButton;
	@FXML public Label errorLabel;
	
	@FXML public Button addCharacterButton;
	@FXML public Button updateCharacterButton;
	@FXML public Button deleteCharacterButton;
	
	@FXML public Label selectedCharacterLabel;
	@FXML public FlowPane characterFlowPane;
	
	private MainViewController mainViewController;
	
	private Pane characterPane;
	private Personaje selectedCharacter;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		characterFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		characterFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		addCharactersFromDB();

		//evento al hacer click al boton de añadir
		addCharacterButton.setOnMouseClicked(new EventHandler<Event>() {
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

				Scene dialogScene = new Scene(dialogRoot, 400, 600);
				addCharacterDialog.setScene(dialogScene);
				addCharacterDialog.showAndWait();
				selectedCharacter = null;
				selectedCharacterLabel.setText("Ningún personaje seleccionado");
				addCharactersFromDB();
			}
		});

		//evento al hacer click al boton de actualizar
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
				addCharactersFromDB();
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
				alert.setHeaderText("Estás a punto de eliminar el personaje de todos los proyectos");
				alert.setContentText("¿Estás seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if(resultado.get() == ButtonType.OK) {

					DAOManager.getPersonajeDAO().removePersonaje(selectedCharacter.getId());

					selectedCharacter = null;
					selectedCharacterLabel.setText("Ningún personaje seleccionado");
					addCharactersFromDB();
				}				
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
	
	public void convertCharacterToPane(Personaje p) {

		if (p == null) return;

		//crea el pane, el "contenedor" donde va a ir la informacion
		characterPane = new Pane();

		//si el proyecto tiene una imagen, la añade, si no coge una por defecto
		ImageView projectImage;
		File imageFile = null;

		try { imageFile = new File(p.getImagen()); } catch (Exception e) {}		
		if (p.getImagen() == null || p.getImagen().equals("") || !imageFile.exists()) projectImage = new ImageView("resources/character_icon.png");
		else {			
			projectImage = new ImageView(new Image(imageFile.toURI().toString()));
		}

		Label nameLabel = new Label();
		Label characterLabel = new Label();

		//establece el margin de cada contenedor
		FlowPane.setMargin(characterPane, new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		//establece diversas medidas del contenedor, la imagen, las label
		characterPane.setPrefSize(PANE_SIZE[0], PANE_SIZE[1]);
		characterPane.getStyleClass().add("pane");		

		projectImage.setFitHeight(IMAGE_FIT[0]);
		projectImage.setFitWidth(IMAGE_FIT[1]);
		projectImage.setLayoutX(IMAGE_LAYOUT[0]);
		projectImage.setLayoutY(IMAGE_LAYOUT[1]);
		projectImage.setPickOnBounds(true);
		projectImage.setPreserveRatio(true);		

		if (p.getNombre().length() > MAX_LENGHT) nameLabel.setText("Nombre: " + p.getNombre().substring(0, MAX_LENGHT) + "...");
		else nameLabel.setText("Nombre: " + p.getNombre());		
		nameLabel.setLayoutX(LABEL_XLAY);
		nameLabel.setLayoutY(NLABEL_YLAY);
		nameLabel.setFont(new Font(FONT_SIZE));

		characterLabel.setLayoutX(LABEL_XLAY);
		characterLabel.setLayoutY(NLABEL_YLAY+ 10);

		characterPane.getChildren().add(nameLabel);	
		characterPane.getChildren().add(projectImage);	
		characterFlowPane.getChildren().add(characterPane);

		//evento de click para que al hacer click sobre un contenedor de proyecto se quede como seleccionado tanto el proyecto como el contenedor
		characterPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {			

				if (event.getClickCount() == 1) {
					selectedCharacter = p;
					selectedCharacterLabel.setText("Libro seleccionado: " + p.getNombre());
					if (errorLabel.isVisible()) errorLabel.setVisible(false);
				}
			}
		});
	}
	
	private void addCharactersFromDB() {
		characterFlowPane.getChildren().clear();
		for (Personaje p : DAOManager.getPersonajeDAO().getPersonajes()) {
			convertCharacterToPane(p);
		}
	}
	
	public void setController(MainViewController controller) {
		mainViewController = controller;
	}

}
