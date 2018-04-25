package ivy.makery.maps;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
		
		//database setup, should this go in a separate function?
		Connection con = null;
		Statement stmt = null;
		try {
			con = DriverManager.getConnection("jdbc:sqlite:test.db"); //TODO: make a real db when we have the data
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		//query strings
		String queryClassSections =
				"select DISTINCT section from class;";
		//make some arrays with db contents
		ArrayList<String> classSections = new ArrayList<String>();
	    try {
	        stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(queryClassSections);
	        while (rs.next()) {
	            classSections.add(rs.getString("section"));
	        }
	    } catch (SQLException e ) {
	    	System.out.println(e.getMessage());
	    } finally {
	        //if (stmt != null) { stmt.close(); } uncomment this later
	    }
		//end database setup
		
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
	
	public static void main(String[] args) {
		launch(args);
	}
}
