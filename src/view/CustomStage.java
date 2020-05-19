package view;

import javafx.stage.Stage;
import model.Libro;
import model.Proyecto;
import view.controllers.AddBookViewController;
import view.controllers.AddProjectViewController;
import view.controllers.UpdateProjectViewController;

public class CustomStage extends Stage {
	
	public Proyecto showAndReturn(AddProjectViewController controller) {
		super.showAndWait();
		return controller.getProyecto();
	}
	
	public Proyecto showAndReturn(UpdateProjectViewController controller) {
		super.showAndWait();
		return controller.getProyecto();
	}
	
	public Libro showAndReturn(AddBookViewController controller) {
		super.showAndWait();
		return controller.getLibro();
	}

}
