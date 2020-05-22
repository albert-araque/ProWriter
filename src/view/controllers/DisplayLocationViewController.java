package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import model.Escena;
import model.Localidad;

public class DisplayLocationViewController implements Initializable {
	
	@FXML public BorderPane borderPane;
	@FXML public Label nameLabel;
	@FXML public TextArea descriptionText;
	@FXML public ListView<Escena> sceneList;
	@FXML public Button closeButton;
	
	private Localidad location;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
	
	public void setLocation(Localidad l) {
		location = l;
	}

}
