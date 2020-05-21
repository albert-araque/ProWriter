package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import dao.DAOManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import model.Capitulo;

public class UpdateChapterViewController implements Initializable {

	@FXML public BorderPane borderPane;
	@FXML public TextField nameText;
	@FXML public TextField chapterOrder;
	@FXML public TextArea descriptionText;
	@FXML public Button addButton;
	@FXML public Button cancelButton;	

	private static double xOffset;
	private static double yOffset;

	private Capitulo chapter;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				nameText.setText(chapter.getNombre());
				descriptionText.setText(chapter.getDescripcion());
				chapterOrder.setText(String.valueOf(chapter.getNumero()));				
			}
		});

		//inicializa la validacion para que el campo de nombre no se quede vacio
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText, Validator.createEmptyValidator("El capitulo tiene que tener un nombre"));
		validationSupport.registerValidator(chapterOrder, Validator.createEmptyValidator("El capitulo tiene que tener un orden"));

		//impide la introduccion de caracteres no numericos
		chapterOrder.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, 
					String newValue) {
				if (!newValue.matches("\\d*")) {
					chapterOrder.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

		//eventos de click para poder mover la ventana dado que no tiene barra de titulo
		borderPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = borderPane.getScene().getWindow().getX() - event.getScreenX();
				yOffset = borderPane.getScene().getWindow().getY() - event.getScreenY();
			}
		});
		borderPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				borderPane.getScene().getWindow().setX(event.getScreenX() + xOffset);
				borderPane.getScene().getWindow().setY(event.getScreenY() + yOffset);
			}
		});

		//evento de click para añadir el personaje
		addButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (validationSupport.isInvalid()) return;
				
				if (isOrderRepeated()) {
					alertRepeated();
					return;
				}

				updateChapter(nameText.getText(), Integer.valueOf(chapterOrder.getText()), descriptionText.getText());
				borderPane.getScene().getWindow().hide();
			}
		});

		//evento de click para cerrar la ventana
		cancelButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				borderPane.getScene().getWindow().hide();
			}
		});

	}
	
	private boolean isOrderRepeated() {
		for (Capitulo cap : DAOManager.getCapituloDAO().getCapitulos()) {
			if (cap.getNumero() == Integer.valueOf(chapterOrder.getText()) && Integer.valueOf(chapterOrder.getText()) != chapter.getNumero()) return true;
		}
		return false;
	}
	
	private void alertRepeated() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Orden de capitulo repetido");
		alert.setHeaderText("El orden del capitulo esta repetido");
		alert.setContentText("El orden del capitulo que has introducido esta repetido, cambialo por uno que no lo este");
		alert.showAndWait();
	}

	private void updateChapter(String name, int order, String description) {

		chapter.setNombre(name);
		chapter.setDescripcion(description);
		chapter.setNumero(order);
		
		DAOManager.getCapituloDAO().updateCapitulo(chapter);		
	}
	
	public void setChapter(Capitulo c) {
		chapter = c;
	}

}