package ivy.makery.maps;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MenuController {
	@FXML private AnchorPane root;
	@FXML private Button btnClass;
	@FXML private Button btnAdvisor;
	@FXML private Button btnHelp;
	private BorderPane rootLayout;
	private AnchorPane mainOverview;
	private AnchorPane advisorOverview;
	
	@FXML
	private void switchToClass() {
		rootLayout.setCenter(mainOverview);
	}
	
	@FXML
	private void switchToAdvisor() {
		rootLayout.setCenter(advisorOverview);
	}
	
	@FXML
	private void showHelp() {
		//TODO
	}
	
	@FXML
	private void hideHelp() {
		//TODO
	}
	
	public void setOtherNodes(BorderPane rootRoot, AnchorPane mainOverviewRoot, AnchorPane advisorOverviewRoot) {
		rootLayout = rootRoot;
		mainOverview = mainOverviewRoot;
		advisorOverview = advisorOverviewRoot;
	}
}
