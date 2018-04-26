package ivy.makery.maps;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.ObservableList;
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
	private ArrayList<String> classSubjects;
	private ArrayList<ArrayList<String>> classNumbers;
	private ArrayList<Class> classFullTable;
	
	@Override
	public void start(Stage primaryStage) {
		
		//database setup, should this go in a separate function?
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try {
			con = DriverManager.getConnection("jdbc:sqlite:test.db"); //TODO: make a real db when we have the data
			stmt = con.createStatement();
			ResultSet rs;
			
			//query strings
			String queryClassSubjects =
					"select DISTINCT section from class ORDER BY section ASC;"; //TODO: change section to subject in all these
			String queryClassNumbers =
					"select DISTINCT classNumber from class where section = ? ORDER BY classNumber ASC;";
			String queryFullTable =
					"select * from class ORDER BY inTime ASC;";
			
			//fill some arrays with db contents
			classSubjects = new ArrayList<String>();
		    rs = stmt.executeQuery(queryClassSubjects);
		    
		    while (rs.next()) {
		        classSubjects.add(rs.getString("section"));
		    }
		    
		    classNumbers = new ArrayList<ArrayList<String>>();
		    pstmt = con.prepareStatement(queryClassNumbers);
		    
		    for (String x: classSubjects) {
		    	ArrayList<String> tempNumbers = new ArrayList<String>();
		    	pstmt.setString(1, x);
		    	rs = pstmt.executeQuery();
		    	
		    	while (rs.next()) {
			        tempNumbers.add(rs.getString("classNumber"));
			    }
		    	
		    	classNumbers.add(new ArrayList<String>(tempNumbers));
		    }
		    
		    classFullTable = new ArrayList<Class>();
		    rs = stmt.executeQuery(queryFullTable);
		    
		    while (rs.next()) {
		    	classFullTable.add(
		    			new Class(rs.getString("section"), rs.getString("classNumber"), rs.getString("classIdentifier")));
		    }
		    
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
			ClassSearchController controller = loader.<ClassSearchController>getController();
			controller.setSubjectList(classSubjects);
			controller.setNumberList(classNumbers);
			controller.setFullTable(classFullTable);
			controller.updateSubjectButton(); //call this only after setting all lists
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
