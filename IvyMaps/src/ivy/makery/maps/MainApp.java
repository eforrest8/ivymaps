package ivy.makery.maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

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
			File kioskdb = new File("kiosk.db");
			Boolean dbNeedsInit = !kioskdb.exists();
			con = DriverManager.getConnection("jdbc:sqlite:kiosk.db");
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
			
			//if db needs init, do it
			if (dbNeedsInit) {
				initDatabase(stmt);
			}
			
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
		    	
		    	/* 
		    	 * these things should probably be removed because this whole thing can be
		    	 * simplified by using a join in the initial query.
		    	 * there does need to be some effort exerted in preventing duplicate crns then, however.
		    	 */
		    	rs2 = stmt2.executeQuery("SELECT DISTINCT day FROM class_schedules WHERE class_number = \"" + crn + "\";");
		    	while (rs2.next()) {
		    		days.add(rs2.getString("day"));
		    	}
		    	
		    	rs2 = stmt2.executeQuery("SELECT person FROM instructors WHERE class = \"" + crn + "\";");
		    	while (rs2.next()) {
		    		instructors.add(rs2.getString("person"));
		    	}
		    	
		    	//rs2 = stmt2.executeQuery("SELECT DISTINCT time FROM class_schedules WHERE class_number = \"" + crn + "\" ORDER BY time ASC;");
		    	rs2 = stmt2.executeQuery("SELECT DISTINCT time FROM class_schedules WHERE class_number = \"" + crn + "\";");
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
		    	
		    	rs2 = stmt2.executeQuery("SELECT day, time FROM advisor_schedules WHERE advisor = \"" + name + "\" ORDER BY day ASC, time ASC;");
		    	while (rs2.next()) {
		    		days.add(rs2.getString("day"));
		    		times.add(rs2.getString("time"));
		    	}
		    	
		    	/*rs2 = stmt2.executeQuery("SELECT DISTINCT day FROM advisor_schedules WHERE advisor = \"" + name + "\" ORDER BY day ASC;");
		    	while (rs2.next()) {
		    		days.add(rs2.getString("day"));
		    	}
		    	
		    	rs2 = stmt2.executeQuery("SELECT DISTINCT time FROM advisor_schedules WHERE advisor = \"" + name + "\" ORDER BY day ASC, time ASC;");
		    	while (rs2.next()) {
		    		times.add(rs2.getString("time"));
		    	}*/
		    	
		    	advisorFullTable.add(new Advisor(name, days.toArray(new String[days.size()]), times.toArray(new String[times.size()]), room));
		    }
		    
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found");
		} catch (IOException e) {
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
		
		controller.setOtherNodes(rootLayout, mainOverview, advisorOverview);
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
	
	public void updateDatabase(Connection con) throws IOException, SQLException {
		//query strings
		//String queryVersion =
		//		"select version from meta;";
		String deleteClassTables =
				"delete from class;"
				+ "delete from classrooms;"
				+ "delete from instructors;"
				+ "delete from class_schedules;";
		String deleteAdvisorTables =
				"delete from advisor;"
				+ "delete from advisor_schedules;";
		
		Statement stmt = con.createStatement();
		//does the csv file exist?
		File classCSV = new File("ClassData.csv");
		File advisorCSV = new File("FacultySchedule.csv");
		if (classCSV.exists()) {
			//check version, quit if not new
			//not currently implemented; database will update every launch as long as there is a csv to update from

			stmt.executeUpdate(deleteClassTables);

			//main database (class data)
			Reader in = new FileReader(classCSV);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String course_id = record.get(2);
				String section_id = record.get(3);
				String crn = record.get(4);
				String title = record.get(5);
				String days = record.get(8);
				String time = record.get(9);
				String building = record.get(11);
				String room = record.get(12);
				String instructor = record.get(14);

				if (!room.isEmpty()) {
					if (building.equals("Fisher Campus") && room.substring(1, 2).equals("3")) {
						//this entry is a class taking place on the third floor (or higher, technically) of the fisher building
						String roomNum = room.substring(1);
						String subject = course_id.substring(0, 4);
						String courseNum = course_id.substring(4, 7);
						String[] daysArray = days.split("");
						ArrayList<String> timeBlocks = new ArrayList<String>();

						Boolean done = false;
						String startTime = time.split("-")[0];
						String endTime = time.split("-")[1];
						if (startTime.endsWith("pm") && startTime.startsWith("12")) {
							startTime = startTime.substring(0, startTime.length() - 2);
						} else if (startTime.endsWith("pm")) {
							startTime = Integer.toString(Integer.parseInt(startTime.split(":")[0]) + 12) + startTime.substring(startTime.indexOf(":"), startTime.length() - 2);
						} else {
							startTime = startTime.substring(0, startTime.length() - 2);
						}
						if (endTime.endsWith("pm") && endTime.startsWith("12")) {
							endTime = endTime.substring(0, endTime.length() - 2);
						} else if (endTime.endsWith("pm")) {
							endTime = Integer.toString(Integer.parseInt(endTime.split(":")[0]) + 12) + endTime.substring(endTime.indexOf(":"), endTime.length() - 2);
						} else {
							endTime = endTime.substring(0, endTime.length() - 2);
						}
						int hour = Integer.parseInt(startTime.split(":")[0]);
						int minute = Integer.parseInt(startTime.split(":")[1]);
						int endHour = Integer.parseInt(endTime.split(":")[0]);
						int endMinute = Integer.parseInt(endTime.split(":")[1]);
						while (done == false) {
							if (hour >= endHour) {
								if (minute >= endMinute) {
									timeBlocks.add(Integer.toString(hour) + ":" + Integer.toString(endMinute));
									done = true;
								} else {
									timeBlocks.add(Integer.toString(hour) + ":" + Integer.toString(minute));
									minute += 30;
								}
							} else {
								timeBlocks.add(Integer.toString(hour) + ":" + Integer.toString(minute));
								hour++;
							}
						}

						stmt.executeUpdate(
								"insert or ignore into room values (\"" + roomNum + "\");");
						stmt.executeUpdate(
								"insert or ignore into people values (\"" + instructor + "\");");
						stmt.executeUpdate(
								"insert or ignore into class values (\"" + crn + "\",\"" + subject + "\",\"" + courseNum + "\",\"" + section_id + "\",\"" + title + "\");");
						stmt.executeUpdate(
								"insert or ignore into classrooms values (\"" + roomNum + "\",\"" + crn + "\");");
						stmt.executeUpdate( //TODO: undo the last name first thing on the instructors
								"insert or ignore into instructors values (\"" + instructor + "\",\"" + crn + "\");");
						for (String x: daysArray) {
							String fullDay = "";
							switch (x) {
							case "M":
								fullDay = "Monday";
								break;
							case "T":
								fullDay = "Tuesday";
								break;
							case "W":
								fullDay = "Wednesday";
								break;
							case "R":
								fullDay = "Thursday";
								break;
							case "F":
								fullDay = "Friday";
								break;
							case "S":
								fullDay = "Saturday";
								break;
							}
							for (String x2: timeBlocks) {
								stmt.executeUpdate(
										"insert or ignore into class_schedules values (\"" + crn + "\",\"" + x2 + "\",\"" + fullDay + "\");");
							}
						}
					}
				}
			}
		}
		
		//now advisor data
		if (!advisorCSV.exists()) {
			return;
		}
		
		stmt.executeUpdate(deleteAdvisorTables);
		
		try(BufferedReader advisorStream = new BufferedReader(new FileReader(advisorCSV))) {
		    String line = advisorStream.readLine(); //this skips the first line, this is intentional
		    String advisorName = "";
		    while ((line = advisorStream.readLine()) != null) {
		        //parse this line
		    	String[] lineArray = line.split("\\^");
		    	if (lineArray[0].contains(" ")) { // if the first field contains a space, it is a name
		    		stmt.executeUpdate(
							"insert or ignore into people values (\"" + lineArray[0] + "\");");
		    		stmt.executeUpdate(
		    				"insert or ignore into advisor values (\"" + lineArray[0] + "\",\"" + lineArray[lineArray.length - 1].substring(1) + "\");");
		    		advisorName = lineArray[0];
		    	} else { // if no space, it is a weekday
		    		if (!lineArray[lineArray.length - 1].equals("N/A")) {
		    			for (int i = 0; i < (lineArray.length - 1) / 3; i++) {
		    				if (lineArray[3 + (i * 3)].equals("Office")) {
		    					String startTime = lineArray[1 + (i * 3)];
		    					String endTime = lineArray[2 + (i * 3)];
		    					ArrayList<String> timeBlocks = new ArrayList<String>();
		    					//convert to 24-hour
		    					if (startTime.endsWith("P") && startTime.startsWith("12")) {
		    						startTime = startTime.substring(0, startTime.length() - 1);
		    					} else if (startTime.endsWith("P")) {
		    						startTime = Integer.toString(Integer.parseInt(startTime.split(":")[0]) + 12) + startTime.substring(startTime.indexOf(":"), startTime.length() - 1);
		    					} else {
		    						startTime = startTime.substring(0, startTime.length() - 1);
		    					}
		    					if (endTime.endsWith("P") && endTime.startsWith("12")) {
		    						endTime = endTime.substring(0, endTime.length() - 1);
		    					} else if (endTime.endsWith("P")) {
		    						endTime = Integer.toString(Integer.parseInt(endTime.split(":")[0]) + 12) + endTime.substring(endTime.indexOf(":"), endTime.length() - 1);
		    					} else {
		    						endTime = endTime.substring(0, endTime.length() - 1);
		    					}
		    					Boolean done = false;
		    					int hour = Integer.parseInt(startTime.split(":")[0]);
		    					int minute = Integer.parseInt(startTime.split(":")[1]);
		    					int endHour = Integer.parseInt(endTime.split(":")[0]);
		    					int endMinute = Integer.parseInt(endTime.split(":")[1]);
		    					while (done == false) {
		    						if (hour >= endHour) {
		    							if (minute >= endMinute) {
		    								timeBlocks.add(Integer.toString(hour) + ":" + Integer.toString(endMinute));
		    								done = true;
		    							} else {
		    								timeBlocks.add(Integer.toString(hour) + ":" + Integer.toString(minute));
		    								minute += 30;
		    							}
		    						} else {
		    							timeBlocks.add(Integer.toString(hour) + ":" + Integer.toString(minute));
		    							hour++;
		    						}
		    					}
		    					for (String x: timeBlocks) {
		    						stmt.executeUpdate(
		    								"insert or ignore into advisor_schedules values (\"" + advisorName + "\",\"" + x + "0\",\"" + lineArray[0] + "\");");
		    					}
		    				}
		    			}
		    		}
		    	}
		    }
		}
		//cleanup unused space in sqlite database
		stmt.executeUpdate("VACUUM;");
	}
	
	private void initDatabase(Statement stmt) throws SQLException {
		stmt.executeUpdate(
				"CREATE TABLE room (\n" + 
				"    number CHAR(3),\n" + 
				"    PRIMARY KEY (number)\n" + 
				");\n" + 
				"CREATE TABLE people (\n" + 
				"    name VARCHAR(50),\n" + 
				"    PRIMARY KEY (name)\n" + 
				");\n" + 
				"CREATE TABLE class (\n" + 
				"    crn CHAR(5), --course reference number\n" + 
				"    subject CHAR(4),\n" + 
				"    course_number CHAR(3),\n" + 
				"    section CHAR(3),\n" + 
				"    title VARCHAR(50), --\"Title\" from csv, not long title\n" + 
				"    PRIMARY KEY (crn)\n" + 
				");\n" + 
				"CREATE TABLE classrooms (\n" + 
				"    room CHAR(3) REFERENCES room(number),\n" + 
				"    class CHAR(5) REFERENCES class(crn),\n" + 
				"    PRIMARY KEY (room, class)\n" + 
				");\n" + 
				"CREATE TABLE advisor (\n" + 
				"    person VARCHAR(50) REFERENCES people(name),\n" + 
				"    room_number CHAR(3) REFERENCES room(number),\n" + 
				"    PRIMARY KEY (person)\n" + 
				");\n" + 
				"CREATE TABLE instructors (\n" + 
				"    person VARCHAR(50) REFERENCES people(name),\n" + 
				"    class CHAR(5) REFERENCES class(crn),\n" + 
				"    PRIMARY KEY (person, class)\n" + 
				");\n" + 
				"CREATE TABLE class_schedules (\n" + 
				"    class_number CHAR(5) REFERENCES class(crn),\n" + 
				"    time CHAR(5),\n" + 
				"    day CHAR(9),\n" + 
				"    PRIMARY KEY (class_number, time, day)\n" + 
				");\n" + 
				"CREATE TABLE advisor_schedules (\n" + 
				"    advisor CHAR(5) REFERENCES advisor(person),\n" + 
				"    time CHAR(5),\n" + 
				"    day CHAR(9),\n" + 
				"    PRIMARY KEY (advisor, time, day)\n" + 
				");\n" + 
				"CREATE TABLE meta (\n" + 
				"    version CHAR(8), --latest academic period + revision number from csv, ex. \"20173001\"\n" + 
				"    PRIMARY KEY (version)\n" + 
				");");
	}
}
