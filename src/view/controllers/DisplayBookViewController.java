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
import model.Capitulo;
import model.Libro;
import model.Personaje;
import model.Proyecto;

public class DisplayBookViewController implements Initializable {
	
	@FXML public BorderPane borderPane;
	@FXML public Label nameLabel;
	@FXML public Label genreLabel;
	@FXML public TextArea descriptionText;
	@FXML public ImageView imageView;
	@FXML public ListView<Personaje> characterList;
	@FXML public ListView<Proyecto> projectList;
	@FXML public ListView<Capitulo> chapterList;
	@FXML public Button closeButton;
	
	private Libro book;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}
	
	public void setBook(Libro l) {
		book = l;
	}

}
