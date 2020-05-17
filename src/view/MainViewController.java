package view;

import java.net.URL;
import java.util.ResourceBundle;

import dao.DAOManager;
import dao.ProyectoDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import model.Proyecto;

public class MainViewController implements Initializable{
	
	private static final int MAX_LENGHT = 13;
	private static final int[] PANE_SIZE = {150, 180};
	private static final int[] IMAGE_FIT = {123, 117};
	private static final int IMAGE_LAYOUT = 14;
	private static final int FONT_SIZE = 11;
	private static final int LABEL_XYLAY = 8;
	private static final int NLABEL_YLAY = 144;
	private static final int BLABEL_YLAY = 161;
	private static final int[] FLOWPANE_MARGIN = {10, 8, 20, 8};
	
	@FXML public FlowPane projectFlowPane;	
	@FXML public Button button;
	
	private Pane projectPane;	
	private ProyectoDAO projectoDAO;
	
	public void addProject(String name, int bookCount) {
		
		projectPane = new Pane();
		ImageView projectImage = new ImageView("resources/proyecto.png");
		Label bookCountLabel = new Label("Numero de libros: " + bookCount);
		Label nameLabel = new Label();
		
		FlowPane.setMargin(projectPane, new Insets(FLOWPANE_MARGIN[0], FLOWPANE_MARGIN[1], FLOWPANE_MARGIN[2], FLOWPANE_MARGIN[3]));
		
		projectPane.setPrefSize(PANE_SIZE[0], PANE_SIZE[1]);
		projectPane.getStyleClass().add("pane");		
		
		projectImage.setFitHeight(IMAGE_FIT[0]);
		projectImage.setFitWidth(IMAGE_FIT[1]);
		projectImage.setLayoutX(IMAGE_LAYOUT);
		projectImage.setLayoutY(IMAGE_LAYOUT);
		projectImage.setPickOnBounds(true);
		projectImage.setPreserveRatio(true);		
			
		
		if (name.length() > MAX_LENGHT) nameLabel.setText("Nombre: " + name.substring(0, MAX_LENGHT) + ". .");
		else nameLabel.setText("Nombre: " + name);		
		nameLabel.setLayoutX(LABEL_XYLAY);
		nameLabel.setLayoutY(NLABEL_YLAY);
		nameLabel.setFont(new Font(FONT_SIZE));
		
		bookCountLabel.setLayoutX(LABEL_XYLAY);
		bookCountLabel.setLayoutY(BLABEL_YLAY);
		bookCountLabel.setFont(new Font(FONT_SIZE));
		
		projectPane.getChildren().add(nameLabel);
		projectPane.getChildren().add(bookCountLabel);		
		projectPane.getChildren().add(projectImage);	
		projectFlowPane.getChildren().add(projectPane);
				
	}
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		button.setOnAction(e -> addProject("", 0));
		
		projectFlowPane.prefWidthProperty().bind(Main.mainStage.widthProperty());
		projectFlowPane.prefHeightProperty().bind(Main.mainStage.heightProperty());
		
		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				projectoDAO = DAOManager.getProyectoDAO();	
				
				for (Proyecto p : projectoDAO.getProyectos()) {
					addProject(p.getNombre(), p.getLibros().size());
				}				
			}
		});
	}
	
	
		
}
