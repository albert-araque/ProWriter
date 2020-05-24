package view.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

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

public class UpdateSceneViewController implements Initializable {
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
	private Escena scene;
	private ArrayList<String> arrayLocalidades;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				nameText.setText(scene.getNombre());

				descriptionText.setText(scene.getDescripcion());

				arrayLocalidades = new ArrayList<>();
				for (Localidad l : DAOManager.getLocalidadDAO().getLocalidades()) {
					arrayLocalidades.add(l.getNombre());
				}
				locationChoiceBox.getItems().addAll(FXCollections.observableArrayList(arrayLocalidades));
				for(Localidad l : DAOManager.getLocalidadDAO().getLocalidades())
				{
					if(l.getId() == scene.getLocalidad().getId())
					{
						locationChoiceBox.setValue(l.getNombre());
						break;
					}
				}

				characterList.getItems().addAll(FXCollections.observableList(new ArrayList<Personaje>(chapter.getLibro().getPersonajes())));

				checkExistingCharacters();
			}
		});

		// inicializa la validacion para que el campo de nombre no se quede vacio
		ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(nameText,
				Validator.createEmptyValidator("El libro debe tener un nombre"));

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

		addButton.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {

				if (validationSupport.isInvalid())
					return;
				
				//Cuando alguna de las localidades de la base de datos
				//coincida con la de locationChoiceBox, la guardamos y salimos
				for (Localidad l : DAOManager.getLocalidadDAO().getLocalidades())
				{
					if (l.getNombre().equals(locationChoiceBox.getSelectionModel().getSelectedItem()))
					{
						updateScene(nameText.getText(), descriptionText.getText(), l,
								new HashSet<Personaje>(characterList.getCheckModel().getCheckedItems()));
					}
				}
				
				borderPane.getScene().getWindow().hide();
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
	
	private void updateScene(String name, String description, Localidad selectedLocalidad, HashSet<Personaje> characters) {
		scene.setNombre(name);
		scene.setDescripcion(description);
		scene.setLocalidad(selectedLocalidad);
		scene.setPersonajes(characters);
		
		DAOManager.getEscenaDAO().updateEscena(scene);
	}
	
	private void checkExistingCharacters() {
		for (Personaje character : characterList.getItems()) {
			for (Personaje existingCharacters : scene.getPersonajes()) {
				if (character.getId() == existingCharacters.getId()) {
					characterList.getCheckModel().check(character);
				}
			}
		}
	}
	
	public void setChapter(Capitulo c) {
		chapter = c;
	}

	public void setScene(Escena e) {
		scene = e;
	}
}