package ivy.makery.maps;

import java.io.IOException;


import javafx.application.Application;
import javafx.stage.Stage;

//import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;
//import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Ivy Maps");
		
		initRootLayout();
		
		showMainOverview();
	}

	//Initializes the root layout
	
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			//Show the scene containing the root layout
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			//primaryStage.setFullScreen(true); //probably disable esc to close thing later
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Show the main overview inside the root layout.
	
	public void showMainOverview() {
		try {
			//Load main overview
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainOverview.fxml"));
			AnchorPane mainOverview = (AnchorPane) loader.load();
			
			//Set main overview into the center of root layout
			rootLayout.setCenter(mainOverview);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Returns the main stage
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public static void scaleInterface(Scene scene) {
		//determine scale factor
		
		
		Scale scale = new Scale(scaleFactor, scaleFactor);
		scale.setPivotX(0);
		scale.setPivotY(0);
		scene.getRoot().getTransforms().setAll(scale);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
