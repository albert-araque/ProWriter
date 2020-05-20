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
import model.Proyecto;
import view.Main;

public class InsideProjectViewController implements Initializable {

	private static final int MAX_LENGHT = 20;
	private static final int[] PANE_SIZE = {290, 340};
	private static final int[] IMAGE_FIT = {200, 230};
	private static final int IMAGE_LAYOUT[] = {45, 22};
	private static final int FONT_SIZE = 14;
	private static final int LABEL_XYLAY = 32;
	private static final int NLABEL_YLAY = 275;
//	private static final int BLABEL_YLAY = 300;
	private static final int[] FLOWPANE_MARGIN = {10, 8, 20, 8};

	@FXML public FlowPane bookFlowPane;
	@FXML public Button addBookButton;
	@FXML public Button updateBookButton;
	@FXML public Button deleteBookButton;
	@FXML public Button backButton;
	@FXML public Label errorLabel;
	@FXML public Label selectedBookLabel;
	@FXML public Label projectNameDisplay;	

	private MainViewController mainViewController;
	private Proyecto project;	
	private Libro selectedBook;
	private Pane bookPane;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		bookFlowPane.prefWidthProperty().bind(Main.getStage().widthProperty());
		bookFlowPane.prefHeightProperty().bind(Main.getStage().heightProperty());

		//evento al hacer click al boton de añadir
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

				AddBookViewController addController = fxmlLoader.getController();
				addController.setProject(project);

				Scene dialogScene = new Scene(dialogRoot, 400, 750);              
				addBookDialog.setScene(dialogScene);
				addBookDialog.showAndWait();
				selectedBook = null;
				selectedBookLabel.setText("Ningun proyecto seleccionado");
				loadBooks();
			}
		});

		//evento al hacer click al boton de actualizar
		updateBookButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				//!!!! SI SE DESCOMENTA SELECTEDPANE AÑADIR AQUI
				if (selectedBook == null) {
					errorLabel.setVisible(true);
					return;
				}				

				Stage updateProjectDialog = new Stage();

				updateProjectDialog.initModality(Modality.APPLICATION_MODAL);
				updateProjectDialog.initStyle(StageStyle.UNDECORATED);
				updateProjectDialog.initOwner(Main.getStage());

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateBookView.fxml"));
				BorderPane dialogRoot = null;
				
				try {
					dialogRoot = fxmlLoader.load();
				} catch (IOException e) {
				}				

				UpdateBookViewController updateController = fxmlLoader.getController();
				updateController.setBook(selectedBook);

				Scene dialogScene = new Scene(dialogRoot, 400, 750);
				updateProjectDialog.setScene(dialogScene);
				updateProjectDialog.showAndWait();				
				selectedBook = null;
				selectedBookLabel.setText("Ningun proyecto seleccionado");
				loadBooks();
			}
		});

		
		//Esto borra el libro de la base de datos y lo que deberia hacer es borrar el libro unicamente del proyecto, es decir quitar la relacion que tiene proyecto y libro
		//evento al hacer click en el boton de borrar
		deleteBookButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (selectedBook == null) {
					errorLabel.setVisible(true);
					return;
				}			

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Eliminacion de libro");
				alert.setHeaderText("Estas a punto de eliminar el libro");
				alert.setContentText("Estas seguro?");

				Optional<ButtonType> resultado = alert.showAndWait();
				if(resultado.get() == ButtonType.OK) {
					DAOManager.getLibroDAO().removeLibro(selectedBook.getId());
					project.getLibros().remove(selectedBook);					
					selectedBook = null;
					selectedBookLabel.setText("Ningun proyecto seleccionado");
					loadBooks();
				}				
			}
		});
		
		backButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProjectView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane =fxmlLoader.load();
				} catch (IOException e) {
				}
				
				ProjectViewController projectViewController = fxmlLoader.getController();
				projectViewController.setController(mainViewController);

				mainViewController.setView(borderPane);

			}
		});

		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				projectNameDisplay.setText(project.getNombre());				
				loadBooks();
			}
		});	

	}

	public void convertBookToPane(Libro l) {

		if (l == null) return;

		//crea el pane, el "contenedor" donde va a ir la informacion
		bookPane = new Pane();

		//si el proyecto tiene una imagen, la añade, si no coge una por defecto
		ImageView projectImage;
		File imageFile = null;

		try { imageFile = new File(l.getImagen()); } catch (Exception e) {}		
		if (l.getImagen() == null || l.getImagen().equals("") || !imageFile.exists()) projectImage = new ImageView("resources/libro.png");
		else {			
			projectImage = new ImageView(new Image(imageFile.toURI().toString()));
		}
		
		Label nameLabel = new Label();
		Label characterLabel = new Label();

		//establece el margin de cada contenedor
		FlowPane.setMargin(bookPane, new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));

		//establece diversas medidas del contenedor, la imagen, las label
		bookPane.setPrefSize(PANE_SIZE[0], PANE_SIZE[1]);
		bookPane.getStyleClass().add("pane");		

		projectImage.setFitHeight(IMAGE_FIT[0]);
		projectImage.setFitWidth(IMAGE_FIT[1]);
		projectImage.setLayoutX(IMAGE_LAYOUT[0]);
		projectImage.setLayoutY(IMAGE_LAYOUT[1]);
		projectImage.setPickOnBounds(true);
		projectImage.setPreserveRatio(true);		

		if (l.getNombre().length() > MAX_LENGHT) nameLabel.setText("Nombre: " + l.getNombre().substring(0, MAX_LENGHT) + "...");
		else nameLabel.setText("Nombre: " + l.getNombre());		
		nameLabel.setLayoutX(LABEL_XYLAY);
		nameLabel.setLayoutY(NLABEL_YLAY);
		nameLabel.setFont(new Font(FONT_SIZE));
		
//		characterLabel.setText("Numero de personajes: " + l.getPersonajes().size());
		characterLabel.setLayoutX(LABEL_XYLAY);
		characterLabel.setLayoutY(NLABEL_YLAY+ 10);

		bookPane.getChildren().add(nameLabel);	
		bookPane.getChildren().add(projectImage);	
		bookFlowPane.getChildren().add(bookPane);

		//evento de click para que al hacer click sobre un contenedor de proyecto se quede como seleccionado tanto el proyecto como el contenedor
		bookPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {			

				if (event.getClickCount() == 1) {
					selectedBook = l;
					selectedBookLabel.setText("Libro seleccionado: " + l.getNombre());
					if (errorLabel.isVisible()) errorLabel.setVisible(false);
				}

				if (event.getClickCount() == 2) {

					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InsideBookView.fxml"));
					BorderPane borderPane = null;
					try {
						borderPane = fxmlLoader.load();
					} catch (IOException e) {
					}

					InsideBookViewController insideBookViewController = fxmlLoader.getController();
					insideBookViewController.setProject(project);
					insideBookViewController.setBook(selectedBook);
					insideBookViewController.setController(mainViewController);

					mainViewController.setView(borderPane);
				}
			}
		});
	}

	private void loadBooks() {		
		bookFlowPane.getChildren().removeAll(bookFlowPane.getChildren());

		for (Libro l : project.getLibros()) {
			convertBookToPane(l);
		}
	}

	public void setController(MainViewController controller) {
		mainViewController = controller;
	}

	public void setProyecto(Proyecto p) {
		project = p;
	}

}
