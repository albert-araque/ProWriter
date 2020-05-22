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
import model.Capitulo;

public class DisplayChapterViewController implements Initializable {
	
	@FXML public BorderPane borderPane;
	@FXML public Label nameLabel;
	@FXML public Label orderLabel;
	@FXML public TextArea descriptionText;
	@FXML public ListView<Capitulo> sceneList;
	@FXML public Button closeButton;
	
	private Capitulo chapter;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
	
	public void setChapter(Capitulo c) {
		chapter = c;
	}

}
