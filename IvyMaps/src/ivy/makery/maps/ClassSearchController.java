package ivy.makery.maps;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
	private String subjectSelection;
	private String numberSelection;
	private ArrayList<String> subjectList;
	private ArrayList<ArrayList<String>> numberList;
	private ArrayList<Class> fullTable;
	private ObservableList<Class> tableData = FXCollections.observableArrayList();
	
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
			if (newSelection == null) {
				//nothing is selected
			} else {
				//SOMEthing is selected
			}
		});
	}
	public void updateSubjectButton() {
		//this method updates the entries in btnClassSubject
		btnClassSubject.getItems().clear();
		
		subjectList.forEach(x -> {
			MenuItem item = new MenuItem(x);
			
			item.setOnAction(e -> {
				//TODO: event handler; what entries do when clicked
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
						//TODO: event handler; what entries do when clicked
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
