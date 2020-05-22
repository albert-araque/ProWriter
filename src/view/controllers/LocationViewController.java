package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class LocationViewController implements Initializable {
	
	private MainViewController mainViewController;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}
	
	public void setController(MainViewController controller) {
		mainViewController = controller;
	}

}
