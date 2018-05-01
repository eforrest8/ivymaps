package ivy.makery.maps;

import java.util.ArrayList;

import javafx.scene.paint.Color;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClassSearchController {
	@FXML private MenuButton btnClassSubject;
	@FXML private MenuButton btnClassNumber;
	@FXML private TableColumn<Class, String> tblSubject;
	@FXML private TableColumn<Class, String> tblNumber;
	@FXML private TableColumn<Class, String> tblSection;
	@FXML private TableColumn<Class, String> tblTitle;
	@FXML private TableColumn<Class, String> tblDays;
	@FXML private TableColumn<Class, String> tblStartTime;
	@FXML private TableColumn<Class, String> tblInstructor;
	@FXML private TableView<Class> resultsTable;
	@FXML private Canvas canvas;
	private String subjectSelection;
	private String numberSelection;
	private ArrayList<String> subjectList;
	private ArrayList<ArrayList<String>> numberList;
	private ArrayList<Class> fullTable;
	private ObservableList<Class> tableData = FXCollections.observableArrayList();
	private final GraphicsContext gc = canvas.getGraphicsContext2D();
	
	//points for drawing graphics
	//Point2D.Double f320 = new Point2D.Double(810, 825);
	Room[] rooms = {
			new Room(810, 825, "320"),
			new Room(720, 1100, "321")
	};
	
	//methods
	public void initialize() {
		tblSubject.setCellValueFactory(new PropertyValueFactory<Class, String>("subject"));
		tblNumber.setCellValueFactory(new PropertyValueFactory<Class, String>("number"));
		tblSection.setCellValueFactory(new PropertyValueFactory<Class, String>("section"));
		tblTitle.setCellValueFactory(new PropertyValueFactory<Class, String>("title"));
		tblTitle.setCellValueFactory(new PropertyValueFactory<Class, String>("days"));
		tblStartTime.setCellValueFactory(new PropertyValueFactory<Class, String>("startTime"));
		tblInstructor.setCellValueFactory(new PropertyValueFactory<Class, String>("instructor"));
		resultsTable.setItems(tableData);
		
		resultsTable.getSelectionModel().selectedItemProperty().addListener(newSelection -> {
			//TODO: implement map highlights based on result selection
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			gc.setFill(Color.GREEN);
			gc.setStroke(Color.LIGHTGREEN);
			gc.setLineWidth(4);
			if (newSelection != null) {
				//SOMEthing is selected
				String primaryRoom = resultsTable.getSelectionModel().selectedItemProperty().getValue().getRooms()[0];
				for (Room x: rooms) {
					if (x.getRoomNumber() == primaryRoom) {
						gc.fillOval(x.getX(), x.getY(), 64, 64);
					}
				}
			}
		});
	}
	public void updateSubjectButton() {
		//this method updates the entries in btnClassSubject
		btnClassSubject.getItems().clear();
		
		subjectList.forEach(x -> {
			MenuItem item = new MenuItem(x);
			
			item.setOnAction(e -> {
				subjectSelection = item.getText();
				btnClassSubject.setText("Class Subject (" + subjectSelection + ")");
				updateNumberButton();
				updateResultsTable();
			});
			btnClassSubject.getItems().addAll(item);
		});
	}
	public void updateNumberButton() {
		//this method updates the entries in btnClassNumber
		btnClassNumber.getItems().clear();
		
		numberList.forEach(x -> {
			if (subjectList.get(numberList.indexOf(x)) == subjectSelection) {
				for (String x2: x) {
					MenuItem item = new MenuItem(x2);
					
					item.setOnAction(e -> {
						numberSelection = item.getText();
						btnClassNumber.setText("Class Number (" + numberSelection + ")");
						updateResultsTable();
					});
					btnClassNumber.getItems().addAll(item);
				}
				btnClassNumber.setDisable(false);
			} else {
				btnClassNumber.setDisable(true); // no class numbers for this subject
			}
		});
	}
	public void updateResultsTable() {
		if (!tableData.isEmpty()) {
			tableData.clear();
		}
		for (Class x: fullTable) {
			if (numberSelection != null) {
				if (x.getSubject().equals(subjectSelection) && x.getNumber().equals(numberSelection)) {
					tableData.add(x);
				}
			} else if (x.getSubject().equals(subjectSelection)) {
				tableData.add(x);
			}
		}
	}
	//setters
	public void setSubjectList(ArrayList<String> list) {
		this.subjectList = list;
	}
	public void setNumberList(ArrayList<ArrayList<String>> list) {
		this.numberList = list;
	}
	public void setFullTable(ArrayList<Class> list) {
		this.fullTable = list;
	}
}
