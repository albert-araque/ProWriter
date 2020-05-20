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
import model.Proyecto;
import view.Main;

public class ProjectViewController implements Initializable{

	private static final int MAX_LENGHT = 20;
	private static final int[] PANE_SIZE = {290, 340};
	private static final int[] IMAGE_FIT = {200, 230};
	private static final int IMAGE_LAYOUT[] = {45, 22};
	private static final int FONT_SIZE = 14;
	private static final int LABEL_XYLAY = 32;
	private static final int NLABEL_YLAY = 275;
	private static final int BLABEL_YLAY = 300;
	private static final int[] FLOWPANE_MARGIN = {10, 8, 20, 8};

	@FXML public FlowPane projectFlowPane;
	@FXML public Button addProjectButton;
	@FXML public Button updateProjectButton;
	@FXML public Button deleteProjectButton;
	@FXML public Label errorLabel;
	@FXML public Label selectedProjectLabel;
	@FXML public Label selectedPaneLabel;
	
	private Pane projectPane;
	private Proyecto selectedProject;
	private MainViewController mainViewController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		projectFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		projectFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		addProjectsFromDB();		
		
		//evento al hacer click al boton de añadir
		addProjectButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				Stage addProjectDialog = new Stage();

				addProjectDialog.initModality(Modality.APPLICATION_MODAL);
				addProjectDialog.initStyle(StageStyle.UNDECORATED);
				addProjectDialog.initOwner(Main.getStage());                

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddProjectView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}

				Scene dialogScene = new Scene(dialogRoot, 400, 600);              
				addProjectDialog.setScene(dialogScene);
				addProjectDialog.showAndWait();
				addProjectsFromDB();
				selectedProject = null;
				selectedProjectLabel.setText("Ningun proyecto seleccionado");
			}
		});

		//evento al hacer click al boton de actualizar
		updateProjectButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (selectedProject == null) {
					errorLabel.setVisible(true);
					return;
				}				

				Stage updateProjectDialog = new Stage();

				updateProjectDialog.initModality(Modality.APPLICATION_MODAL);
				updateProjectDialog.initStyle(StageStyle.UNDECORATED);
				updateProjectDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateProjectView.fxml"));
				BorderPane dialogRoot = null;
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}				

				UpdateProjectViewController updateController = fxmlLoader.getController();
				updateController.setProject(selectedProject);

				Scene dialogScene = new Scene(dialogRoot, 400, 600);
				updateProjectDialog.setScene(dialogScene);
				updateProjectDialog.showAndWait();
				addProjectsFromDB();
				selectedProject = null;
				selectedProjectLabel.setText("Ningun proyecto seleccionado");
			}
		});
		
		//evento al hacer click en el boton de borrar
		deleteProjectButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
				if (selectedProject == null) {
					errorLabel.setVisible(true);
					return;
				}			
				
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Eliminacion de proyecto");
				alert.setHeaderText("Estas a punto de eliminar el proyecto");
				alert.setContentText("Estas seguro?");
				
				Optional<ButtonType> resultado = alert.showAndWait();
				if(resultado.get() == ButtonType.OK)
				{
					DAOManager.getProyectoDAO().removeProyecto(selectedProject.getId());
					selectedProject = null;
					selectedProjectLabel.setText("Ningun proyecto seleccionado");
					addProjectsFromDB();
				}				
			}
		});
		
	}

	public void convertProjectToPane(Proyecto p) {
		
		if (p == null) return;

		//crea el pane, el "contenedor" donde va a ir la informacion
		projectPane = new Pane();

		//si el proyecto tiene una imagen, la añade, si no coge una por defecto
		ImageView projectImage;
		File imageFile = null;

		try { imageFile = new File(p.getImagen()); } catch (Exception e) {}		
		if (p.getImagen() == null || p.getImagen().equals("") || !imageFile.exists()) projectImage = new ImageView("resources/proyecto.png");
		else {			
			projectImage = new ImageView(new Image(imageFile.toURI().toString()));
		}

		Label bookCountLabel = new Label("Numero de libros: " + p.getLibros().size());
		Label nameLabel = new Label();

		//establece el margin de cada contenedor
		FlowPane.setMargin(projectPane, new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		//establece diversas medidas del contenedor, la imagen, las label
		projectPane.setPrefSize(PANE_SIZE[0], PANE_SIZE[1]);
		projectPane.getStyleClass().add("pane");		

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

		bookCountLabel.setLayoutX(LABEL_XYLAY);
		bookCountLabel.setLayoutY(BLABEL_YLAY);
		bookCountLabel.setFont(new Font(FONT_SIZE));

		projectPane.getChildren().add(nameLabel);
		projectPane.getChildren().add(bookCountLabel);		
		projectPane.getChildren().add(projectImage);	
		projectFlowPane.getChildren().add(projectPane);

		//evento de click para que al hacer click sobre un contenedor de proyecto se quede como seleccionado tanto el proyecto como el contenedor
		projectPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {			
				
				if (event.getClickCount() == 1) {
					selectedProject = p;					
					selectedProjectLabel.setText("Proyecto seleccionado: " + p.getNombre());
					if (errorLabel.isVisible()) errorLabel.setVisible(false);
				}
				
				if (event.getClickCount() == 2) {
					
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InsideProjectView.fxml"));
					BorderPane borderPane = null;
					try {
						 borderPane = fxmlLoader.load();
					} catch (IOException e) {
					}
					
					InsideProjectViewController insideProjectViewController = fxmlLoader.getController();
					insideProjectViewController.setProyecto(selectedProject);
					insideProjectViewController.setController(mainViewController);
					
					mainViewController.setView(borderPane);
				}
			}
		});
	}
	
	//obtiene todos los proyectos de la base de datos y los añade
	private void addProjectsFromDB() {
		Platform.runLater(new Runnable() {			
			@Override
			public void run() {

				projectFlowPane.getChildren().removeAll(projectFlowPane.getChildren());

				for (Proyecto p : DAOManager.getProyectoDAO().getProyectos()) {
					convertProjectToPane(p);
				}				
			}
		});
	}
	
	public void setController(MainViewController controller) {
		mainViewController = controller;
	}
}