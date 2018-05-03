package ivy.makery.maps;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class ClassSearchController {
	@FXML private AnchorPane root;
	@FXML private Button btnBack;
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
	private BorderPane rootLayout;
	private AnchorPane mainMenu;
	private String subjectSelection;
	private String numberSelection;
	private ArrayList<String> subjectList;
	private ArrayList<ArrayList<String>> numberList;
	private ArrayList<Class> fullTable;
	private ObservableList<Class> tableData = FXCollections.observableArrayList();
	
	//points for drawing graphics
	Room[] rooms = {
			new Room(340, 350, "320"),
			new Room(305, 465, "321"),
			new Room(245, 350, "322"),
			new Room(165, 465, "323"),
			new Room(135, 350, "324"),
			new Room(100, 550, "326"),
			new Room(100, 465, "327"),
			new Room(165, 550, "328"),
			new Room(203, 550, "330"),
			new Room(240, 550, "332"),
			new Room(278, 550, "334"),
			new Room(590, 550, "336"),
			new Room(690, 350, "337"),
			new Room(630, 465, "338"),
			new Room(690, 350, "339"),
			new Room(765, 465, "340"),
			new Room(800, 350, "341"),
			new Room(800, 575, "343"),
			new Room(690, 575, "344"),
			new Room(590, 575, "345")
	};
	
	//methods
	public void initialize() {
		tblSubject.setCellValueFactory(new PropertyValueFactory<Class, String>("subject"));
		tblNumber.setCellValueFactory(new PropertyValueFactory<Class, String>("number"));
		tblSection.setCellValueFactory(new PropertyValueFactory<Class, String>("section"));
		tblTitle.setCellValueFactory(new PropertyValueFactory<Class, String>("title"));
		tblDays.setCellValueFactory(new PropertyValueFactory<Class, String>("days"));
		tblStartTime.setCellValueFactory(new PropertyValueFactory<Class, String>("startTime"));
		tblInstructor.setCellValueFactory(new PropertyValueFactory<Class, String>("instructor"));
		resultsTable.setItems(tableData);
		resultsTable.prefHeightProperty().bind(resultsTable.fixedCellSizeProperty().multiply(Bindings.size(resultsTable.getItems()).add(1)));
		resultsTable.minHeightProperty().bind(resultsTable.prefHeightProperty());
		resultsTable.maxHeightProperty().bind(resultsTable.prefHeightProperty());
		
		//this thing should make word wrap work?
		Callback<TableColumn<Class, String>, TableCell<Class, String>> cb = new Callback<TableColumn<Class, String>, TableCell<Class, String>>() {
			@Override
			public TableCell<Class, String> call(TableColumn<Class, String> arg0) {
				TableCell<Class, String> cell = new TableCell<>();
				Text text = new Text();
				cell.setGraphic(text);
	            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
	            text.wrappingWidthProperty().bind(arg0.widthProperty());
	            text.textProperty().bind(cell.itemProperty());
	            return cell;
			}
		};
		
		//set word wrap
		tblSubject.setCellFactory(cb);
		tblNumber.setCellFactory(cb);
		tblSection.setCellFactory(cb);
		tblTitle.setCellFactory(cb);
		tblDays.setCellFactory(cb);
		tblStartTime.setCellFactory(cb);
		tblInstructor.setCellFactory(cb);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFont(Font.getDefault());
		gc.setLineWidth(4);
		gc.setFill(Color.GREEN);
		gc.setStroke(Color.LIGHTGREEN);
		gc.fillOval(459, 400, 16, 16);
		gc.strokeOval(459, 400, 16, 16);
		gc.setFill(Color.BLACK);
		gc.setStroke(Color.WHITE);
		gc.strokeText("You are here", 435, 420);
		gc.fillText("You are here", 435, 420);
		
		resultsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			gc.setFill(Color.GREEN);
			gc.setStroke(Color.LIGHTGREEN);
			gc.fillOval(459, 400, 16, 16);
			gc.strokeOval(459, 400, 16, 16);
			if (newSelection != null) {
				//SOMEthing is selected
				String[] classRooms = resultsTable.getSelectionModel().getSelectedItem().getRooms();
				for (Room x: rooms) {
					for (String x2: classRooms) {
						if (x.getRoomNumber().equals(x2)) {
							gc.setFill(Color.GREEN);
							gc.setStroke(Color.LIGHTGREEN);
							gc.strokeRect(x.getX(), x.getY() - 4, 52, 8);
							gc.fillOval(x.getX() - 16, x.getY() - 16, 32, 32);
							gc.strokeOval(x.getX() - 16, x.getY() - 16, 32, 32);
							gc.fillRect(x.getX(), x.getY() - 4, 52, 8);
							gc.setFill(Color.BLACK);
							gc.setStroke(Color.WHITE);
							gc.strokeText("F" + x.getRoomNumber(), x.getX() + 20, x.getY() + 5);
							gc.fillText("F" + x.getRoomNumber(), x.getX() + 20, x.getY() + 5);
						}
					}
				}
			}
			gc.setFill(Color.BLACK);
			gc.setStroke(Color.WHITE);
			gc.strokeText("You are here", 435, 420);
			gc.fillText("You are here", 435, 420);
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
		numberSelection = null;
		btnClassNumber.setText("Class Number");
		
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
				if (btnClassNumber.getItems() == null) {
					btnClassNumber.setDisable(true); // no class numbers for this subject
				}
			}
		});
	}
	public void updateResultsTable() {
		resultsTable.getSelectionModel().clearSelection();
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
	@FXML
	private void back() {
		tableData.clear();
		subjectSelection = null;
		numberSelection = null;
		btnClassSubject.setText("Class Subject");
		btnClassNumber.setText("Class Number");
		btnClassNumber.setDisable(true);
		rootLayout.setCenter(mainMenu);
		//root.setVisible(false);
	}
	//setters
	public void setNodes(BorderPane rootLayout, AnchorPane mainMenu) {
		this.rootLayout = rootLayout;
		this.mainMenu = mainMenu;
	}
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
