package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class CharacterViewController implements Initializable {
	
	private MainViewController mainViewController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
	
	public void setController(MainViewController controller) {
		mainViewController = controller;
	}

}
