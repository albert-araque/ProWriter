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

/**
 * Clase para a�adir un cap�tulo
 * 
 * @author Albert Araque, Francisco Jos� Ruiz
 * @version 1.0
 */
public class AddChapterViewController implements Initializable {

	@FXML
	public BorderPane borderPane;
	@FXML
	public TextField nameText;
	@FXML
	public TextField chapterOrder;
	@FXML
	public TextArea descriptionText;
	@FXML
	public Button addButton;
	@FXML
	public Button cancelButton;

	private static double xOffset;
	private static double yOffset;

	private Libro book;

	/**
	 * M�todo para inicializar la clase
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		// Inicializa la validaci�n para que el campo de nombre no quede vac�o
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText,
				Validator.createEmptyValidator("El cap�tulo debe tener un nombre"));
		validationSupport.registerValidator(chapterOrder,
				Validator.createEmptyValidator("El cap�tulo debe tener un orden"));

		// Impide la introducci�n de car�cteres no num�ricos
		chapterOrder.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					chapterOrder.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

		// Evento para poder mover la ventana, dado que no tiene barra de t�tulo
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

		// Evento para a�adir el contenido
		addButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (validationSupport.isInvalid()) return;
				
				if (isChapterOrderRepeated()) {
					alertRepeated();
					return;
				}

				addChapterToDB(nameText.getText(), Integer.valueOf(chapterOrder.getText()), descriptionText.getText());
				borderPane.getScene().getWindow().hide();
			}
		});

		// Evento para cerrar la ventana
		cancelButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				borderPane.getScene().getWindow().hide();
			}
		});

	}
	private boolean isChapterOrderRepeated() {
	
	 */
	 * @return Devuelve true si est�n repetidos, false si no est�n repetidos
	 * 
	 * en el mismo libro
	/**
	 * M�todo que comprueba que no haya dos cap�tulos con el mismo n�mero de orden
		for (Capitulo cap : DAOManager.getCapituloDAO().getCapitulos()) {
			if (cap.getLibro().getId() == book.getId() && cap.getNumero() == Integer.valueOf(chapterOrder.getText()))
				return true;
		}
		return false;
	}

	/**
	 * M�todo que avisa mediante un di�logo, que ya hay un cap�tulo con ese n�mero
	 * de orden en el libro
	 */
	private void alertRepeated() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Orden de cap�tulo repetido");
		alert.setHeaderText("El orden del cap�tulo est� repetido");
		alert.setContentText(
				"El orden del cap�tulo que has introducido ya est� en ese libro, c�mbialo por otro n�mero que no est�");
		alert.showAndWait();
	}

	/**
	 * M�todo para a�adir el cap�tulo al libro, y guardarlo en la base de datos
	 * 
	 * @param name        Nombre del cap�tulo
	 * @param order       Orden del cap�tulo
	 * @param description Descripci�n del cap�tulo
	 */
	private void addChapterToDB(String name, int order, String description) {

		Capitulo chapterToReturn = new Capitulo(book, name, order, description);
		DAOManager.getCapituloDAO().addCapitulo(chapterToReturn);
		book.getCapitulos().add(chapterToReturn);
	}

	/**
	 * M�todo para seleccionar el libro
	 * 
	 * @param l Libro de entrada
	 */
	public void setBook(Libro l) {
		book = l;
	}

}
