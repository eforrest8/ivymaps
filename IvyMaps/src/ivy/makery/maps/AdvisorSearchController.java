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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class AdvisorSearchController {
	@FXML private AnchorPane root;
	@FXML private Button btnBack;
	@FXML private TableColumn<Advisor, String> tblName;
	@FXML private TableColumn<Advisor, String> tblIn;
	@FXML private TableView<Advisor> resultsTable;
	@FXML private Canvas canvas;
	private BorderPane rootLayout;
	private AnchorPane mainMenu;
	private ArrayList<Advisor> fullTable;
	private ObservableList<Advisor> tableData = FXCollections.observableArrayList();
	
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
		tblName.setCellValueFactory(new PropertyValueFactory<Advisor, String>("name"));
		tblIn.setCellValueFactory(new PropertyValueFactory<Advisor, String>("in"));
		resultsTable.setItems(tableData);
		//resultsTable.prefHeightProperty().bind(resultsTable.fixedCellSizeProperty().multiply(Bindings.size(resultsTable.getItems()).add(1)));
		//resultsTable.minHeightProperty().bind(resultsTable.prefHeightProperty());
		//resultsTable.maxHeightProperty().bind(resultsTable.prefHeightProperty());
		//updateResultsTable();
		
		//this thing should make word wrap work?
		Callback<TableColumn<Advisor, String>, TableCell<Advisor, String>> cb = new Callback<TableColumn<Advisor, String>, TableCell<Advisor, String>>() {
			@Override
			public TableCell<Advisor, String> call(TableColumn<Advisor, String> arg0) {
				TableCell<Advisor, String> cell = new TableCell<>();
				Text text = new Text();
				cell.setGraphic(text);
	            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
	            text.wrappingWidthProperty().bind(arg0.widthProperty());
	            text.textProperty().bind(cell.itemProperty());
	            return cell;
			}
		};
		
		//set word wrap
		tblName.setCellFactory(cb);
		tblIn.setCellFactory(cb);
		
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
				String room = resultsTable.getSelectionModel().getSelectedItem().getRoom();
				for (Room x: rooms) {
					if (x.getRoomNumber().equals(room)) {
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
			gc.setFill(Color.BLACK);
			gc.setStroke(Color.WHITE);
			gc.strokeText("You are here", 435, 420);
			gc.fillText("You are here", 435, 420);
		});
	}
	public void updateResultsTable() {
		tableData.addAll(fullTable);
		//for (Advisor x: fullTable) {//null pointer error here
		//	tableData.add(x);
		//}
	}
	@FXML
	private void back() {
		rootLayout.setCenter(mainMenu);
	}
	//setters
	public void setNodes(BorderPane rootLayout, AnchorPane mainMenu) {
		this.rootLayout = rootLayout;
		this.mainMenu = mainMenu;
	}
	public void setFullTable(ArrayList<Advisor> list) {
		this.fullTable = list;
	}
}
