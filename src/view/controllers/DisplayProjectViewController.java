package view.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import model.Libro;
import model.Proyecto;

public class DisplayProjectViewController implements Initializable {
	
	@FXML public BorderPane borderPane;
	@FXML public ImageView imageView;
	@FXML public Label nameLabel;
	@FXML public TextArea descriptionText;
	@FXML public ListView<Libro> bookList;
	@FXML public Button closeButton;
	
	private static double xOffset;
	private static double yOffset;
	
	private Proyecto project;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				setInformation();
			}
		});

		// eventos de click para poder mover la ventana dado que no tiene barra de titulo
		borderPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = borderPane.getScene().getWindow().getX() - event.getScreenX();
				yOffset = borderPane.getScene().getWindow().getY() - event.getScreenY();
			}
		});
		borderPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				borderPane.getScene().getWindow().setX(event.getScreenX() + xOffset);
				borderPane.getScene().getWindow().setY(event.getScreenY() + yOffset);
			}
		});

		closeButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				borderPane.getScene().getWindow().hide();				
			}
		});

	}
	
	private void setInformation() {
		
		File imageFile = null;

		try { imageFile = new File(project.getImagen()); } catch (Exception e) {}		
		if (project.getImagen() == null || project.getImagen().equals("") || !imageFile.exists()) imageView.setImage(new Image("resources/proyecto.png"));
		else {			
			imageView.setImage(new Image(imageFile.toURI().toString()));
		}
		
		nameLabel.setText("Nombre: " + project.getNombre());
		descriptionText.setText(project.getDescripcion());
		bookList.getItems().addAll(project.getLibros());
		
	}
	
	public void setProject(Proyecto p) {
		project = p;
	}

}
