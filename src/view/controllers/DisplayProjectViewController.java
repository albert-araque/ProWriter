package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
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
	
	private Proyecto project;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
	
	public void setProject(Proyecto p) {
		project = p;
	}

}
