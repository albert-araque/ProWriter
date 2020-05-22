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
import model.Personaje;

public class DisplaySceneViewController implements Initializable {
	
	@FXML public BorderPane borderPane;
	@FXML public Label nameLabel;
	@FXML public Label chapterLabel;
	@FXML public Label locationLabel;
	@FXML public TextArea descriptionText;
	@FXML public ListView<Personaje> characterList;
	@FXML public Button closeButton;
	
	private Escena scene;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	public void setScene(Escena e) {
		scene = e;
	}
	
}
