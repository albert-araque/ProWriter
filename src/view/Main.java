package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	public static Stage mainStage;	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			mainStage = primaryStage;
			
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
			
			Scene scene = new Scene(root, 400, 400);			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setTitle("ProWriter");
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();	
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
