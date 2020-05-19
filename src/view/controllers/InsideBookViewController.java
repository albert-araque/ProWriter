package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class InsideBookViewController implements Initializable {
	
	@FXML public Pane characterPane;
	@FXML public Pane scenePane;
	@FXML public Label projectNameDisplay;
	@FXML public Button backButton;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

}
