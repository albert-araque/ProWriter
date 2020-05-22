package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class AddLocationViewController implements Initializable {
	
	@FXML public BorderPane borderPane;
	@FXML public Button addButton;
	@FXML public Button cancelButton;
	@FXML public TextField nameText;
	@FXML public TextArea descriptionText;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

}
