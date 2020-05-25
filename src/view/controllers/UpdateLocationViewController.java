package view.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import model.Localidad;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import dao.DAOManager;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Clase para modificar una localización
 * 
 * @author Albert Araque, Francisco José Ruiz
 * @version 1.0
 */
public class UpdateLocationViewController implements Initializable {

	@FXML
	public BorderPane borderPane;
	@FXML
	public Button addButton;
	@FXML
	public Button cancelButton;
	@FXML
	public TextField nameText;
	@FXML
	public TextArea descriptionText;

	private static double xOffset;
	private static double yOffset;

	private Localidad localidad;

	/**
	 * Método para inicializar la clase
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				nameText.setText(localidad.getNombre());
				descriptionText.setText(localidad.getDescripcion());
			}
		});

		// Inicializa la validación para que el campo de nombre no quede vacío
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText,
				Validator.createEmptyValidator("La localidad debe tener un nombre"));

		// Evento para poder mover la ventana dado que no tiene barra de
		// titulo
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

		// Evento para añadir el contenido
		addButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (validationSupport.isInvalid())
					return;

				updateLocation(nameText.getText(), descriptionText.getText());
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
	 * Método para actualizar la localización en la base de datos
	 * 
	 * @param name        Nombre de la localización
	 * @param description Descripción de la localización
	 */
	private void updateLocation(String name, String description) {

		localidad.setNombre(name);
		localidad.setDescripcion(description);

		DAOManager.getLocalidadDAO().updateLocalidad(localidad);
	}

	/**
	 * Método para seleccionar la localización
	 * 
	 * @param l Localización de entrada
	 */
	public void setLocation(Localidad l) {
		localidad = l;
	}
}
