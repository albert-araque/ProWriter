package view.controllers;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import model.Escena;
import model.Localidad;

public class DisplayLocationViewController implements Initializable {
	
	@FXML public BorderPane borderPane;
	@FXML public Label nameLabel;
	@FXML public TextArea descriptionText;
	@FXML public ListView<Escena> sceneList;
	@FXML public Button closeButton;
	
	private static double xOffset;
	private static double yOffset;
	
	private Localidad location;

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
		nameLabel.setText("Nombre: " + location.getNombre());
		descriptionText.setText(location.getDescripcion());
		sceneList.getItems().addAll(location.getEscenas());
	}
	
	public void setLocation(Localidad l) {
		location = l;
	}

}
