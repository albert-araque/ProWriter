package view;

import javafx.stage.Stage;
import model.Proyecto;

public class CustomStage extends Stage {
	
	public Proyecto showAndReturn(AddProjectViewController controller) {
		super.showAndWait();
		return controller.getProyecto();
	}
	
	public Proyecto showAndReturn(UpdateProjectViewController controller) {
		super.showAndWait();
		return controller.getProyecto();
	}

}
