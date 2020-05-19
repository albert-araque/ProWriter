package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class CharacterInsideBookViewController implements Initializable {
	
	@FXML public Label selectedCharacterLabel;
	@FXML public Label errorLabel;
	@FXML public Label bookNameDisplay;
	@FXML public Button backButton;
	@FXML public Button addCharacterButton;
	@FXML public Button updateCharacterButton;
	@FXML public Button deleteCharacterButton;
	@FXML public FlowPane characterFlowPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

}
