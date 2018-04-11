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
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.geometry.Pos;

public class GitViewerStudent extends Application {

	private static final String TEST_REPO_PATH = "/Users/piech/Documents/Teaching/cs106a/GitCoach/exampleGits/aaldana_1";
	private static final String ERROR_REPO_PATH = "/Users/piech/Documents";
	private static final String CURR_DIR = ".";

	private static final String REPO_PATH = ERROR_REPO_PATH;

	private final ComboBox<String> comboBox = new ComboBox<String>();
	private final CodeEditor editor = new CodeEditor("hello world");
	private final ListView<String> listView = new ListView<String>();
	private List<Intermediate> history = null;
	private boolean shouldCompile = false;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("CS106A Pensive");
		List<String> allFiles = FileHistory.getFiles(REPO_PATH);
		if(allFiles != null) {
			makeDisplay(primaryStage, allFiles);
			displayFile(comboBox.getValue());
		} else {
			makeErrorDisplay(primaryStage);
		}
	}

	private void makeErrorDisplay(Stage primaryStage) {

		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		
		String errorMessage = "";
		errorMessage += "Oops we couldn't find\n";
		errorMessage += "code history in this folder.\n";
		errorMessage += "no worries.\n";
		errorMessage += "Talk to an SL!";
		
		Text text = new Text(10, 40, errorMessage);
		text.setFont(new Font(40));
		
		Scene scene = new Scene(new Group(text));

		String style = getClass().getResource("css/program.css").toExternalForm();
		scene.getStylesheets().add(style);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void displayFile(String filePath) {
		editor.resetScroll();
		history = FileHistory.getHistory(REPO_PATH, filePath, shouldCompile);
		makeListView(history);
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
	}

	private void makeDisplay(Stage primaryStage, List<String> allFiles) {
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
		WebView editorView = editor.getView(); 
		graphCodeSplit.getItems().add(listView);
		graphCodeSplit.getItems().add(editorView);

		graphCodeSplit.setDividerPositions(0.10);
		BorderPane border = new BorderPane();
		border.setCenter(graphCodeSplit);

		Label fileLabel = new Label("File: ");

		ObservableList<String> options = 
				FXCollections.observableArrayList(allFiles);
		comboBox.setItems(options);
		comboBox.setValue(comboBox.getItems().get(0));

		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(fileLabel, comboBox);
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
