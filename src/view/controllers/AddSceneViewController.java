package view.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import org.controlsfx.control.CheckListView;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import dao.DAOManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import model.Capitulo;
import model.Escena;
import model.Localidad;
import model.Personaje;

public class AddSceneViewController implements Initializable {

	@FXML
	public TextField nameText;
	@FXML
	public TextArea descriptionText;
	@FXML
	public ChoiceBox<String> locationChoiceBox;
	@FXML
	public CheckListView<Personaje> characterList;
	@FXML
	public Button addButton;
	@FXML
	public Button cancelButton;
	@FXML
	public BorderPane borderPane;

	private static double xOffset;
	private static double yOffset;

	private Capitulo chapter;
	
	private ArrayList<String> arrayLocalidades;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		// inicia un hilo para cargar los personajes en el checklistview para añadirlos
		// al proyecto
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				//Cargamos el nombre de la localidad en donde podría desarrollarse la escena
				arrayLocalidades = new ArrayList<>();
				
				for(Localidad l : DAOManager.getLocalidadDAO().getLocalidades())
				{
					arrayLocalidades.add(l.getNombre());
				}
				
				//Añadimos el aviso de que se debe señalizar una localidad para continuar,
				//seguidamente añadimos los nombres de arrayLocalidades
				locationChoiceBox.getItems().add("Seleccione una localidad para continuar");
				
				locationChoiceBox.getItems()
				.addAll(FXCollections.observableArrayList(arrayLocalidades));
				
				//Le indicamos al usuario que debe seleccionar una localidad para continuar
				locationChoiceBox.setValue("Seleccione una localidad para continuar");
				
				//Cargamos la lista de personajes que aparecen en la escena
				characterList.getItems()
						.addAll(FXCollections.observableList(DAOManager.getPersonajeDAO().getPersonajes()));
			}
		});

		// inicializa la validacion para que el campo de nombre no se quede vacio
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText,
				Validator.createEmptyValidator("La escena debe tener un nombre"));

		// eventos de click para poder mover la ventana dado que no tiene barra de
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

		// evento de click para añadir la escena
		addButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (validationSupport.isInvalid())
					return;
				
				for (Localidad l : DAOManager.getLocalidadDAO().getLocalidades())
				{
					//Cuando alguna de las localidades de la base de datos
					//coincida con la de locationChoiceBox, la guardamos y salimos
					if (l.getNombre().equals(locationChoiceBox.getSelectionModel().getSelectedItem()))
					{
						addSceneToDB(nameText.getText(), descriptionText.getText(), l,
								new HashSet<Personaje>(characterList.getCheckModel().getCheckedItems()));
						borderPane.getScene().getWindow().hide();
						break;
					}
				}
			}
		});

		// evento de click para cerrar la ventana
		cancelButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				borderPane.getScene().getWindow().hide();
			}
		});
	}

	// metodo para añadir el proyecto a la base de datos
	private void addSceneToDB(String name, String description, Localidad localidad, Set<Personaje> characters) {

		Escena sceneToReturn = new Escena(chapter, localidad, name, description, characters);
		DAOManager.getEscenaDAO().addEscena(sceneToReturn);
		chapter.getEscenas().add(sceneToReturn);
	}

	public void setChapter(Capitulo c) {
		chapter = c;
	}
}
