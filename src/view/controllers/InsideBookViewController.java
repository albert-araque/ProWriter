package view.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import model.Libro;
import model.Proyecto;

public class InsideBookViewController implements Initializable {
	
	@FXML public BorderPane bookViewPane;
	@FXML public Pane characterPane;
	@FXML public Pane chapterPane;
	@FXML public Label bookNameDisplay;
	@FXML public Button backButton;
	
	private MainViewController mainViewController;
	private Libro book;
	private Proyecto project;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				bookNameDisplay.setText(project.getNombre() + " > " + book.getNombre());
			}
		});
		
		backButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InsideProjectView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane =fxmlLoader.load();
				} catch (IOException e) {
				}
				
				InsideProjectViewController insideProjectViewController = fxmlLoader.getController();
				insideProjectViewController.setProyecto(project);
				insideProjectViewController.setController(mainViewController);

				mainViewController.setView(borderPane);
			}
		});
		
		characterPane.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CharacterInsideBookView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane =fxmlLoader.load();
				} catch (IOException e) {
				}
				
				CharacterInsideBookViewController characterInsideBookViewController = fxmlLoader.getController();
				characterInsideBookViewController.setBook(book);
				characterInsideBookViewController.setProject(project);
				characterInsideBookViewController.setController(mainViewController);

				mainViewController.setView(borderPane);				
			}
		});
		
		chapterPane.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ChapterInsideBookView.fxml"));
				BorderPane borderPane = null;
				try {
					borderPane =fxmlLoader.load();
				} catch (IOException e) {
				}
				
				ChapterInsideBookViewController chapterInsideBookViewController = fxmlLoader.getController();
				chapterInsideBookViewController.setBook(book);
				chapterInsideBookViewController.setProject(project);
				chapterInsideBookViewController.setController(mainViewController);

				mainViewController.setView(borderPane);	
				
			}
		});
	}
	
	public void setProject(Proyecto p) {
		project = p;
	}
	
	public void setBook(Libro l) {
		book = l;
	}
	
	public void setController(MainViewController controller) {
		mainViewController = controller;
	}

}
