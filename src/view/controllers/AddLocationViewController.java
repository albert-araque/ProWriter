package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import dao.DAOManager;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import model.Localidad;

/**
 * Controlador de la vista para a�adir una localizaci�n
 * 
 * @author Albert Araque, Francisco Jos� Ruiz
 * @version 1.0
 */
public class AddLocationViewController implements Initializable {

	@FXML public BorderPane borderPane;
	@FXML public Button addButton;
	@FXML public Button cancelButton;
	@FXML public TextField nameText;
	@FXML public TextArea descriptionText;

	private static double xOffset;
	private static double yOffset;

	/**
	 * M�todo para inicializar la clase
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Inicializa la validaci�n para que el campo de nombre no quede vac�o
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText,
				Validator.createEmptyValidator("La localidad debe tener un nombre"));

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

				if (validationSupport.isInvalid())
					return;

				addLocationToDB(nameText.getText(), descriptionText.getText());
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

	/**
	 * M�todo para a�adir la localizaci�n a la base de datos
	 * 
	 * @param name        Nombre de la localizaci�n
	 * @param description Descripci�n de la localizaci�n
	 */
	private void addLocationToDB(String name, String description) {

		Localidad locationToReturn = new Localidad(name, description, null);
		DAOManager.getLocalidadDAO().addLocalidad(locationToReturn);
	}
}
