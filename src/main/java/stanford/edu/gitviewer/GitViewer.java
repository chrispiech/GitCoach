package stanford.edu.gitviewer;
import java.util.List;

import graphs.GraphChoser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.geometry.Pos;

public class GitViewer extends Application {

	private static final String TEST_REPO_PATH = "/Users/anniehu/Desktop/GitCoach/exampleGits/aaldana_1";
	private static final String IMG_DIR = "file:/Users/anniehu/Desktop/GitCoach/"; // needs to be changed
	private static final String CURR_DIR = ".";
	
	private static final String REPO_PATH = TEST_REPO_PATH;

	private final ComboBox<String> comboBox = new ComboBox<String>();
	private final CodeEditor editor = new CodeEditor("hello world");
	private final ListView<String> listView = new ListView<String>();
	private List<Intermediate> history = null;
	private GraphChoser topGraph = new GraphChoser("SourceLength");
	private GraphChoser bottomGraph = new GraphChoser("Runs");
	private boolean shouldCompile = false;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		makeDisplay(primaryStage);
		displayFile(comboBox.getValue());
	}

	private void displayFile(String filePath) {
		editor.resetScroll();
		history = FileHistory.getHistory(REPO_PATH, filePath, shouldCompile);
		makeListView(history);
		topGraph.drawGraph(history);
		bottomGraph.drawGraph(history);
	}

	private ListView<String> makeListView(List<Intermediate> history) {
		ObservableList<String> data = FXCollections.observableArrayList();
		listView.setMinSize(200, 200);
		for (int i = 0; i < history.size(); i++) {
			Intermediate intermediate = history.get(i);
			double workingHours = intermediate.workingHours;
			String label = i +"\t" + formatTime(workingHours);
			if(intermediate.breakHours != null) {
				double breakHours = intermediate.breakHours;
				label += " (" + formatTime(breakHours) + ")";
			}
			data.add(label);
		}
		listView.setItems(data);
		Intermediate codeVersion = history.get(0);
		String code = codeVersion.code;
		editor.setCode(code);
		return listView;
	}

	private String formatTime(double workingHours) {
		int hours = (int)workingHours;
		int mins = (int) Math.round(60 * (workingHours - hours));
		return hours + "h " + mins + "m";
	}

	private void onIntermediateSelection(int index) {
		Intermediate codeVersion = history.get(index);
		String code = codeVersion.code;
		editor.setCode(code);
		topGraph.setSelectedTime(codeVersion.workingHours);
		bottomGraph.setSelectedTime(codeVersion.workingHours);
	}

	private StackPane makeImageView(String imgName) {
		Image img = new Image(IMG_DIR + imgName);
		ImageView imgView = new ImageView();
		imgView.setImage(img);
		imgView.setFitHeight(200);
		imgView.setPreserveRatio(true);
		imgView.setSmooth(true);
		imgView.setCache(true);
		StackPane progressView = new StackPane();
		progressView.getChildren().addAll(imgView);
		StackPane.setAlignment(imgView,  Pos.CENTER);
		return progressView;
	}
	
	private void makeDisplay(Stage primaryStage) {
		primaryStage.setTitle("CS106A Pensieve");        
		listView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov, 
							String oldValue, String newValue) {
						int index = listView.getSelectionModel().getSelectedIndex();
						if(index == -1) return;
						onIntermediateSelection(index);
					}
				});

		SplitPane graphCodeSplit = new SplitPane();
		graphCodeSplit.getItems().add(listView);
		
		WebView editorView = editor.getView(); 
		StackPane progressView = makeImageView("backward_0024.png");
		VBox center = new VBox();
		center.getChildren().add(progressView);
		center.getChildren().add(new Separator());
		center.getChildren().add(editorView);
		graphCodeSplit.getItems().add(center);

		VBox graphs = new VBox();
		graphs.getChildren().add(topGraph.getView());
		graphs.getChildren().add(new Separator());
		graphs.getChildren().add(bottomGraph.getView());

		graphCodeSplit.getItems().add(graphs);
		graphCodeSplit.setDividerPositions(0.12, 0.65);
		BorderPane border = new BorderPane();
		border.setCenter(graphCodeSplit);

		Label fileLabel = new Label("File: ");
		List<String> allFiles = FileHistory.getFiles(REPO_PATH);
		ObservableList<String> options = 
				FXCollections.observableArrayList(allFiles);
		comboBox.setItems(options);
		comboBox.setValue(comboBox.getItems().get(0));

//		ToggleSwitch runSwitch = new ToggleSwitch("Run: ");
//		runSwitch.setPadding(new Insets(4, 0, 0, 0));
//		runSwitch.selectedProperty().addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> observable,
//					Boolean oldValue, Boolean newValue) {
//				shouldCompile = newValue;
//				if(shouldCompile) {
//					String filePath = comboBox.getValue();
//					displayFile(filePath);
//				}
//			}
//		});

		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(fileLabel, comboBox);
		//hb.getChildren().addAll(runSwitch);
		hb.setSpacing(10);
		border.setTop(hb);

		comboBox.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				String filePath = comboBox.getValue();
				displayFile(filePath);
			}		    
		});

		Scene scene = new Scene(new Group());
		scene.setRoot(border);
		
		String style = getClass().getResource("css/program.css").toExternalForm();
		scene.getStylesheets().add(style);

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
