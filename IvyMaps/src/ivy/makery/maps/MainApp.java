package ivy.makery.maps;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javafx.application.Application;
import javafx.stage.Stage;

//import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

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
		Connection con2 = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try {
			con = DriverManager.getConnection("jdbc:sqlite:kiosk.db"); //TODO: make a real db when we have the data
			con2 = DriverManager.getConnection("jdbc:sqlite:kiosk.db");
			stmt = con.createStatement();
			Statement stmt2 = con2.createStatement();
			ResultSet rs;
			
			//query strings
			String queryClassSubjects =
					"select DISTINCT subject from class ORDER BY subject ASC;";
			String queryClassNumbers =
					"select DISTINCT course_number from class where subject = ? ORDER BY course_number ASC;";
			String queryFullTable =
					"select * from class;";
			
			//function to update database if new csv files are available
			updateDatabase(con);
			
			//fill some arrays with db contents
			classSubjects = new ArrayList<String>();
		    rs = stmt.executeQuery(queryClassSubjects);
		    
		    while (rs.next()) {
		        classSubjects.add(rs.getString("subject"));
		    }
		    
		    classNumbers = new ArrayList<ArrayList<String>>();
		    pstmt = con.prepareStatement(queryClassNumbers);
		    
		    for (String x: classSubjects) {
		    	ArrayList<String> tempNumbers = new ArrayList<String>();
		    	pstmt.setString(1, x);
		    	rs = pstmt.executeQuery();
		    	
		    	while (rs.next()) {
			        tempNumbers.add(rs.getString("course_number"));
			    }
		    	
		    	classNumbers.add(new ArrayList<String>(tempNumbers));
		    }
		    
		    classFullTable = new ArrayList<Class>();
		    rs = stmt.executeQuery(queryFullTable);
		    
		    while (rs.next()) {
		    	String crn = rs.getString("crn");
		    	String subject = rs.getString("subject");
		    	String courseNumber = rs.getString("course_number");
		    	String section = rs.getString("section");
		    	String title = rs.getString("title");
		    	ArrayList<String> days = new ArrayList<String>();
		    	ArrayList<String> instructors = new ArrayList<String>();
		    	ArrayList<String> times = new ArrayList<String>();
		    	ArrayList<String> rooms = new ArrayList<String>();
		    	ResultSet rs2;
		    	
		    	rs2 = stmt2.executeQuery("SELECT DISTINCT day FROM class_schedules WHERE class_number = \"" + crn + "\";");
		    	while (rs2.next()) {
		    		days.add(rs2.getString("day"));
		    	}
		    	
		    	rs2 = stmt2.executeQuery("SELECT person FROM instructors WHERE class = \"" + crn + "\";");
		    	while (rs2.next()) {
		    		instructors.add(rs2.getString("person"));
		    	}
		    	
		    	rs2 = stmt2.executeQuery("SELECT DISTINCT time FROM class_schedules WHERE class_number = \"" + crn + "\" ORDER BY time ASC;");
		    	while (rs2.next()) {
		    		times.add(rs2.getString("time"));
		    	}
		    	
		    	rs2 = stmt2.executeQuery("SELECT room FROM classrooms WHERE class = \"" + crn + "\";");
		    	while (rs2.next()) {
		    		rooms.add(rs2.getString("room"));
		    	}
		    	
		    	classFullTable.add(new Class(subject, courseNumber, section, title,
		    			days.toArray(new String[days.size()]), instructors.toArray(new String[instructors.size()]),
		    			times.toArray(new String[times.size()]), rooms.toArray(new String[rooms.size()])));
		    }
		    
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: File not found");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		showMainOverview(); //TODO: implement ui reset after extended inactivity
		
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
			System.out.println(e);
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
	
	public void updateDatabase(Connection con) throws IOException {
		//query strings
		String queryVersion =
				"select version from meta;";
		/*
		//main database
		Reader in = new FileReader("szrcsch_16713180.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
		for (CSVRecord record : records) {
			String crn = record.get(4);
			String room = record.get(12);
			if (room.startsWith("F")) {
				System.out.println(crn);
			}
		}*/
	}
}
