package ivy.makery.maps;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.stage.Stage;

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
	private ArrayList<Advisor> advisorFullTable;
	private AnchorPane mainMenu;
	
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
			String queryAdvisorFullTable =
					"select * from advisor;";
			
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
		    
		    advisorFullTable = new ArrayList<Advisor>();
		    rs = stmt.executeQuery(queryAdvisorFullTable);
		    
		    while (rs.next()) {
		    	String name = rs.getString("person");
		    	String room = rs.getString("room_number");
		    	ArrayList<String> days = new ArrayList<String>();
		    	ArrayList<String> times = new ArrayList<String>();
		    	ResultSet rs2;
		    	
		    	rs2 = stmt2.executeQuery("SELECT DISTINCT day FROM advisor_schedules WHERE advisor = \"" + name + "\";");
		    	while (rs2.next()) {
		    		days.add(rs2.getString("day"));
		    	}
		    	
		    	rs2 = stmt2.executeQuery("SELECT DISTINCT time FROM advisor_schedules WHERE advisor = \"" + name + "\" ORDER BY time ASC;");
		    	while (rs2.next()) {
		    		times.add(rs2.getString("time"));
		    	}
		    	
		    	advisorFullTable.add(new Advisor(name, days.toArray(new String[days.size()]), times.toArray(new String[times.size()]), room));
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
		
		//some weird layout stuff that should work???
		initRootLayout();
		MenuController controller = initMainMenu();
		AnchorPane mainOverview = showMainOverview(mainMenu); //TODO: implement ui reset after extended inactivity
		AnchorPane advisorOverview = showAdvisorOverview(mainMenu);
		showMainMenu(controller, mainOverview, advisorOverview);
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
	
	public MenuController initMainMenu() {
		MenuController controller = null;
		try {
			//Load main overview
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainMenu.fxml"));
			mainMenu = (AnchorPane) loader.load();
			controller = loader.<MenuController>getController();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return controller;
	}
	
	public void showMainMenu(MenuController controller, AnchorPane mainOverview, AnchorPane advisorOverview) {
		//Set main overview into the center of root layout
		rootLayout.setCenter(mainMenu);
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/MainMenu.fxml"));
		
		controller.setOtherNodes(rootLayout, mainOverview, advisorOverview); // TODO: add advisorOverview
	}
	
	//Show the main overview inside the root layout.
	
	public AnchorPane showMainOverview(AnchorPane mainMenu) {
		AnchorPane mainOverview = null;
		try {
			//Load main overview
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainOverview.fxml"));
			mainOverview = (AnchorPane) loader.load();
			//mainOverview.setVisible(false);
			
			//Set main overview into the center of root layout
			rootLayout.setCenter(mainOverview);
			ClassSearchController controller = loader.<ClassSearchController>getController();
			controller.setSubjectList(classSubjects);
			controller.setNumberList(classNumbers);
			controller.setFullTable(classFullTable);
			controller.setNodes(rootLayout, mainMenu);
			controller.updateSubjectButton(); //call this only after setting all lists
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mainOverview;
	}
	
	public AnchorPane showAdvisorOverview(AnchorPane mainMenu) {
		AnchorPane mainOverview = null;
		try {
			//Load main overview
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/AdvisorOverview.fxml"));
			mainOverview = (AnchorPane) loader.load();
			//mainOverview.setVisible(false);
			
			//Set main overview into the center of root layout
			rootLayout.setCenter(mainOverview);
			AdvisorSearchController controller = loader.<AdvisorSearchController>getController();
			controller.setFullTable(advisorFullTable);
			controller.setNodes(rootLayout, mainMenu);
			controller.updateResultsTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mainOverview;
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
		//String queryVersion =
		//		"select version from meta;";
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
