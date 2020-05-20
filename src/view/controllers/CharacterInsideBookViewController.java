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

public class CharacterInsideBookViewController implements Initializable {
	
	private static final int MAX_LENGHT = 20;
	private static final int[] PANE_SIZE = {290, 340};
	private static final int[] IMAGE_FIT = {200, 230};
	private static final int IMAGE_LAYOUT[] = {45, 22};
	private static final int FONT_SIZE = 14;
	private static final int LABEL_XYLAY = 32;
	private static final int NLABEL_YLAY = 275;
	private static final int[] FLOWPANE_MARGIN = {10, 8, 20, 8};
	
	@FXML public Label selectedCharacterLabel;
	@FXML public Label errorLabel;
	@FXML public Label bookNameDisplay;
	@FXML public Button backButton;
	@FXML public Button addCharacterButton;
	@FXML public Button updateCharacterButton;
	@FXML public Button deleteCharacterButton;
	@FXML public FlowPane characterFlowPane;
	
	private MainViewController mainViewController;
	private Libro book;
	private Proyecto project;
	private Pane characterPane;
	private Personaje selectedCharacter;

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

				AddCharacterViewController addController = fxmlLoader.getController();
				addController.setBook(book);

				Scene dialogScene = new Scene(dialogRoot, 400, 750);              
				addCharacterDialog.setScene(dialogScene);
				addCharacterDialog.showAndWait();
				selectedCharacter = null;
				selectedCharacterLabel.setText("Ningun personaje seleccionado");
				loadCharacters();
			}
		});
		
		updateCharacterButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
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
				alert.setTitle("Eliminacion de personaje");
				alert.setHeaderText("Estas a punto de eliminar el personaje");
				alert.setContentText("Estas seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if(resultado.get() == ButtonType.OK) {
					
					book.getPersonajes().remove(selectedCharacter);
					for (Personaje p : DAOManager.getLibroDAO().getLibro(book.getId()).getPersonajes()) {
						System.out.println(p);
					}
					DAOManager.getLibroDAO().updateLibro(book);
//					DAOManager.getProyectoDAO().updateProyecto(project);
					
					selectedCharacter = null;
					selectedCharacterLabel.setText("Ningun proyecto seleccionado");
					loadCharacters();
				}	
			}
		});

		backButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InsideBookView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane =fxmlLoader.load();
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
	
	private void loadCharacters() {
		characterFlowPane.getChildren().removeAll(characterFlowPane.getChildren());
		
		for (Personaje p : book.getPersonajes()) {
			convertCharacterToPane(p);
		}
	}
	
	private void convertCharacterToPane(Personaje p) {
		
		if (p == null) return;
		characterPane = new Pane();

		Label nameLabel = new Label();
		ImageView projectImage;
		File imageFile = null;

		try { imageFile = new File(p.getImagen()); } catch (Exception e) {}		
		if (p.getImagen() == null || p.getImagen().equals("") || !imageFile.exists()) projectImage = new ImageView("resources/character_icon.png");
		else {			
			projectImage = new ImageView(new Image(imageFile.toURI().toString()));
		}
		
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
		nameLabel.setLayoutX(LABEL_XYLAY);
		nameLabel.setLayoutY(NLABEL_YLAY);
		nameLabel.setFont(new Font(FONT_SIZE));

		characterPane.getChildren().add(nameLabel);	
		characterPane.getChildren().add(projectImage);	
		characterFlowPane.getChildren().add(characterPane);

		characterPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {			

				if (event.getClickCount() == 1) {
					selectedCharacter = p;
					selectedCharacterLabel.setText("Personaje seleccionado: " + selectedCharacter.getNombre());
					if (errorLabel.isVisible()) errorLabel.setVisible(false);
				}

//				if (event.getClickCount() == 2) {
//
//					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InsideBookView.fxml"));
//					BorderPane borderPane = null;
//					try {
//						borderPane = fxmlLoader.load();
//					} catch (IOException e) {
//					}
//
//					InsideBookViewController insideBookViewController = fxmlLoader.getController();
//					insideBookViewController.setProject(project);
//					insideBookViewController.setBook(selectedCharacter);
//					insideBookViewController.setController(mainViewController);
//
//					mainViewController.setView(borderPane);
//				}
			}
		});
		
	}
	
	public void setProject(Proyecto p) {
		project = p;
	}
	
	public void setBook(Libro l) {
		book = l;
	}
	
	public void setController(MainViewController controller) {
		mainViewController = controller;
	}

}
