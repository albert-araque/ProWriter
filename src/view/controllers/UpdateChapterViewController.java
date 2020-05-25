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

/**
 * Controlador de la vista para modificar un cap�tulo
 * 
 * @author Albert Araque, Francisco Jos� Ruiz
 * @version 1.0
 */
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

	/**
	 * M�todo para inicializar la clase
	 */
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

		// Inicializa la validaci�n para que el campo de nombre no quede vac�o
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText,
				Validator.createEmptyValidator("El capitulo debe tener un nombre"));
		validationSupport.registerValidator(chapterOrder,
				Validator.createEmptyValidator("El capitulo debe tener un orden"));

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

				if (validationSupport.isInvalid())
					return;

				if (isOrderRepeated()) {
					alertRepeated();
					return;
				}

				updateChapter(nameText.getText(), Integer.valueOf(chapterOrder.getText()), descriptionText.getText());
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
	 * M�todo que comprueba que no haya dos cap�tulos con el mismo n�mero de orden
	 * en el mismo libro
	 * 
	 * @return Devuelve true si est�n repetidos, false si no est�n repetidos
	 */
	private boolean isOrderRepeated() {
		for (Capitulo cap : DAOManager.getCapituloDAO().getCapitulos()) {
			if (cap.getNumero() == Integer.valueOf(chapterOrder.getText())
					&& Integer.valueOf(chapterOrder.getText()) != chapter.getNumero())
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
				"El orden del cap�tulo que has introducido est� repetido, c�mbialo por otro que no lo est�");
		alert.showAndWait();
	}

	/**
	 * M�todo para actualizar el cap�tulo en la base de datos
	 * 
	 * @param name        Nombre del cap�tulo
	 * @param order       Orden del cap�tulo
	 * @param description Descripci�n del cap�tulo
	 */
	private void updateChapter(String name, int order, String description) {

		chapter.setNombre(name);
		chapter.setDescripcion(description);
		chapter.setNumero(order);

		DAOManager.getCapituloDAO().updateCapitulo(chapter);
	}

	/**
	 * M�todo para seleccionar el cap�tulo
	 * 
	 * @param c Cap�tulo de entrada
	 */
	public void setChapter(Capitulo c) {
		chapter = c;
	}

}