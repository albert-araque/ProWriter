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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
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
import model.Escena;
import model.Libro;
import model.Proyecto;
import view.Main;

public class SceneChapterViewController implements Initializable {

	private static final int MAX_LENGHT = 20;
	private static final int[] PANE_SIZE = {290, 340};
	private static final int[] IMAGE_FIT = {200, 230};
	private static final int IMAGE_LAYOUT[] = {45, 22};
	private static final int FONT_SIZE = 14;
	private static final int LABEL_XLAY = 32;
	private static final int NLABEL_YLAY = 230;
	private static final int[] FLOWPANE_MARGIN = {10, 8, 20, 8};

	@FXML public Label selectedSceneLabel;
	@FXML public Label errorLabel;
	@FXML public Label chapterNameDisplay;
	@FXML public Button backButton;
	@FXML public Button addSceneButton;
	@FXML public Button updateSceneButton;
	@FXML public Button deleteSceneButton;
	@FXML public FlowPane sceneFlowPane;

	private MainViewController mainViewController;
	private Pane scenePane;
	private Capitulo chapter;
	private Libro book;
	private Proyecto project;
	private Escena selectedScene;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		sceneFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		sceneFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				chapterNameDisplay.setText(project.getNombre() + " > " + book.getNombre() + " > " + chapter.getNombre());
				loadScenes();
			}
		});
		
		addSceneButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
				Stage addSceneDialog = new Stage();

				addSceneDialog.initModality(Modality.APPLICATION_MODAL);
				addSceneDialog.initStyle(StageStyle.UNDECORATED);
				addSceneDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddSceneView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}
				
				AddSceneViewController addSceneViewController = fxmlLoader.getController();
				addSceneViewController.setChapter(chapter);

				Scene dialogScene = new Scene(dialogRoot, 400, 550);              
				addSceneDialog.setScene(dialogScene);
				addSceneDialog.showAndWait();
				loadScenes();
				selectedScene = null;
				selectedSceneLabel.setText("Ningún capítulo seleccionado");
			}
		});

		updateSceneButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
				if (selectedScene == null) {
					errorLabel.setVisible(true);
					return;
				}
				
				Stage addChapterDialog = new Stage();

				addChapterDialog.initModality(Modality.APPLICATION_MODAL);
				addChapterDialog.initStyle(StageStyle.UNDECORATED);
				addChapterDialog.initOwner(Main.getStage());                

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateSceneView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}
				
				UpdateSceneViewController updateSceneViewController = fxmlLoader.getController();
				updateSceneViewController.setScene(selectedScene);

				Scene dialogScene = new Scene(dialogRoot, 400, 550);              
				addChapterDialog.setScene(dialogScene);
				addChapterDialog.showAndWait();
				loadScenes();
				selectedScene = null;
				selectedSceneLabel.setText("Ningún capítulo seleccionado");
			}
		});
		
		deleteSceneButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				if (selectedScene == null) {
					errorLabel.setVisible(true);
					return;
				}
				
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Eliminacion de capitulo");
				alert.setHeaderText("Estas a punto de eliminar la escena");
				alert.setContentText("Estas seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if (resultado.get() == ButtonType.OK) {

					chapter.getEscenas().remove(selectedScene);
					DAOManager.getEscenaDAO().removeEscena(selectedScene.getId());
					DAOManager.getCapituloDAO().updateCapitulo(chapter);
					
					selectedScene = null;
					selectedSceneLabel.setText("Ningun proyecto seleccionado");
					loadScenes();
				}
			}
		});
		
		backButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ChapterInsideBookView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane =fxmlLoader.load();
				} catch (IOException e) {
				}
				
				ChapterInsideBookViewController chapterInsideBookViewController = fxmlLoader.getController();
				chapterInsideBookViewController.setProject(project);
				chapterInsideBookViewController.setBook(book);
				chapterInsideBookViewController.setController(mainViewController);

				mainViewController.setView(borderPane);	
			}
		});	
		
	}
	
	private void loadScenes() {
		sceneFlowPane.getChildren().clear();
		
		for (Escena e : chapter.getEscenas()) {
			convertSceneToPane(e);
		}
	}

	private void convertSceneToPane(Escena e) {

		if (e == null) return;
		scenePane = new Pane();

		Label nameLabel = new Label();

		ImageView chapterImage = new ImageView("resources/capitulo_icono.png");

		//establece el margin de cada contenedor
		FlowPane.setMargin(scenePane, new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		//establece diversas medidas del contenedor, la imagen, las label
		scenePane.setPrefSize(PANE_SIZE[0], PANE_SIZE[1]);
		scenePane.getStyleClass().add("pane");

		chapterImage.setFitHeight(IMAGE_FIT[0]);
		chapterImage.setFitWidth(IMAGE_FIT[1]);
		chapterImage.setLayoutX(IMAGE_LAYOUT[0]);
		chapterImage.setLayoutY(IMAGE_LAYOUT[1]);
		chapterImage.setPickOnBounds(true);
		chapterImage.setPreserveRatio(true);

		if (e.getNombre().length() > MAX_LENGHT) nameLabel.setText("Nombre: " + e.getNombre().substring(0, MAX_LENGHT) + "...");
		else nameLabel.setText("Nombre: " + e.getNombre());		
		nameLabel.setLayoutX(LABEL_XLAY);
		nameLabel.setLayoutY(NLABEL_YLAY);
		nameLabel.setFont(new Font(FONT_SIZE));

		scenePane.getChildren().add(nameLabel);
		scenePane.getChildren().add(chapterImage);
		sceneFlowPane.getChildren().add(scenePane);

		scenePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {			

				if (event.getClickCount() == 1) {
					selectedScene = e;
					selectedSceneLabel.setText("Escena seleccionada: " + selectedScene.getNombre());
					if (errorLabel.isVisible()) errorLabel.setVisible(false);
				}

//				if (event.getClickCount() == 2) {
//
//					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SceneChapterView.fxml"));
//					BorderPane borderPane = null;
//					try {
//						borderPane = fxmlLoader.load();
//					} catch (IOException e) {
//					}
//
//					SceneChapterViewController sceneChapterViewController = fxmlLoader.getController();
//					sceneChapterViewController.setChapter(selectedChapter);
//					sceneChapterViewController.setController(mainViewController);
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

	public void setChapter(Capitulo c) {
		chapter = c;
	}

	public void setController(MainViewController controller) {
		mainViewController = controller;
	}

}
