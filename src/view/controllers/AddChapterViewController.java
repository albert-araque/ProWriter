package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import dao.DAOManager;
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
import model.Libro;

public class AddChapterViewController implements Initializable {

	@FXML public BorderPane borderPane;
	@FXML public TextField nameText;
	@FXML public TextField chapterOrder;
	@FXML public TextArea descriptionText;
	@FXML public Button addButton;
	@FXML public Button cancelButton;	

	private static double xOffset;
	private static double yOffset;

	private Libro book;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		//inicializa la validacion para que el campo de nombre no se quede vacio
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText, Validator.createEmptyValidator("El capítulo debe tener un nombre"));
		validationSupport.registerValidator(chapterOrder, Validator.createEmptyValidator("El capítulo debe tener un orden"));

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

		//evento de click para añadir el capítulo
		addButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (validationSupport.isInvalid()) return;
				
				if (bookIdAndChapterNumberAreTheSame()) {
					alertRepeated();
					return;
				}

				addChapterToDB(nameText.getText(), Integer.valueOf(chapterOrder.getText()), descriptionText.getText());
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
	
	private boolean bookIdAndChapterNumberAreTheSame() {
		for (Capitulo cap : DAOManager.getCapituloDAO().getCapitulos()) {
			if (cap.getLibro().getId() == book.getId() && cap.getNumero() == Integer.valueOf(chapterOrder.getText())) return true;
		}
		return false;
	}
	
	private void alertRepeated() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Orden de capítulo repetido");
		alert.setHeaderText("El orden del capítulo está repetido");
		alert.setContentText("El orden del capítulo que has introducido ya está en ese libro, cámbialo por otro número que no esté");
		alert.showAndWait();
	}

	private void addChapterToDB(String name, int order, String description) {

		Capitulo chapterToReturn = new Capitulo(book, name, order, description);
		DAOManager.getCapituloDAO().addCapitulo(chapterToReturn);
		book.getCapitulos().add(chapterToReturn);
	}

	public void setBook(Libro l) {
		book = l;
	}

}
