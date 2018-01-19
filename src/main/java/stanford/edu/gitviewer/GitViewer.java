package stanford.edu.gitviewer;
import java.util.List;

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
import javafx.scene.web.WebView;
import javafx.geometry.Pos;

public class GitViewer extends Application {

	private static final String REPO_PATH = "/Users/piech/Desktop/gits/apleus_2";

	private final ComboBox<String> comboBox = new ComboBox<String>();
	private final CodeEditor editor = new CodeEditor("hello world");
	private final ListView<String> listView = new ListView<String>();
	private List<Intermediate> history = null;
	private GitGrapher grapher = new GitGrapher();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		makeDisplay(primaryStage);
		displayFile(comboBox.getValue());
	}

	private void displayFile(String filePath) {
		history = FileHistory.getHistory(REPO_PATH, filePath);
		makeListView(history);
		grapher.drawGraph(history);
	}

	private ListView<String> makeListView(List<Intermediate> history) {
		ObservableList<String> data = FXCollections.observableArrayList();
		listView.setMinSize(200, 200);
		for (int i = 0; i < history.size(); i++) {
			Intermediate intermediate = history.get(i);
			double workingHours = intermediate.workingHours;
			String label = i +"\t" + Util.round(workingHours, 2) + "h";
			if(intermediate.breakHours != null) {
				double breakHours = intermediate.breakHours;
				label += " (" + Util.round(breakHours, 2) + "h)";
			}
			data.add(label);
		}
		listView.setItems(data);
		Intermediate codeVersion = history.get(0);
		String code = codeVersion.code;
		editor.setCode(code);
		return listView;
	}

	private void onIntermediateSelection(int index) {
		Intermediate codeVersion = history.get(index);
		String code = codeVersion.code;
		editor.setCode(code);
		grapher.setSelectedTime(codeVersion.workingHours);
	}

	private void makeDisplay(Stage primaryStage) {
		primaryStage.setTitle("CS106A Coach");        
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
		graphCodeSplit.getItems().add(grapher.getView());
		graphCodeSplit.setDividerPositions(0.12, 0.65);
		BorderPane border = new BorderPane();
		border.setCenter(graphCodeSplit);

		Label fileLabel = new Label("File: ");
		ObservableList<String> options = 
				FXCollections.observableArrayList(FileHistory.getFiles(REPO_PATH));
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

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
