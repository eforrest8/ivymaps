package ivy.makery.maps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class MenuController {
	@FXML private AnchorPane root;
	@FXML private Button btnClass;
	@FXML private Button btnAdvisor;
	@FXML private Button btnHelp;
	private BorderPane rootLayout;
	private AnchorPane mainOverview;
	private AnchorPane advisorOverview;
	
	private ScrollPane help = new ScrollPane();
	//private File helpFile = new File("src/help.txt");
	//private File helpFile;
	public String helpText;
	
	public void initialize() {
		try {
			/*
			helpFile = new File(getClass().getResource("rsrc:/help.txt").toURI());
			byte[] encoded = Files.readAllBytes(Paths.get(helpFile.getAbsolutePath()));
			helpText = new String(encoded, StandardCharsets.UTF_8);
			*/
			InputStream input = getClass().getResourceAsStream("/help.txt");
			File file = File.createTempFile("help", ".tmp");
			OutputStream out = new FileOutputStream(file);
			int read;
			byte[] bytes = new byte[1024];
			while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
			out.close();
			file.deleteOnExit();
			byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			helpText = new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Text helpTextText = new Text(helpText);
		helpTextText.setStyle("-fx-font-size: 18");
		
		help.setContent(helpTextText);
		help.setPrefHeight(root.getPrefHeight());
		help.setMinWidth(root.getPrefWidth() / 2);
		help.setMaxWidth(root.getPrefWidth() / 2);
		help.setLayoutX((root.getPrefWidth() / 2) - (help.getMinWidth() / 2));
		helpTextText.setWrappingWidth(help.getMinWidth() - 50);
		
	}
	
	@FXML
	private void switchToClass() {
		rootLayout.setCenter(mainOverview);
	}
	
	@FXML
	private void switchToAdvisor() {
		rootLayout.setCenter(advisorOverview);
	}
	
	@FXML
	private void showHelp() {
		if (root.getChildren().contains(help)) {
			root.getChildren().remove(help);
			btnHelp.setText("Show Help");
		} else {
			root.getChildren().add(help);
			btnHelp.setText("Hide Help");
		}
	}
	
	public void setOtherNodes(BorderPane rootRoot, AnchorPane mainOverviewRoot, AnchorPane advisorOverviewRoot) {
		rootLayout = rootRoot;
		mainOverview = mainOverviewRoot;
		advisorOverview = advisorOverviewRoot;
	}
}
