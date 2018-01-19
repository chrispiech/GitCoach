package stanford.edu.gitviewer;


import java.util.List;


import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class GitGrapher {
	
	private static final double EPSILON = 0.000000001;

	final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
	final LineChart<Number,Number> lineChart = 
            new LineChart<Number,Number>(xAxis,yAxis);
	private Double maxY = null;
	private XYChart.Series<Number,Number> vertical;

	public LineChart<Number,Number> getView() {
		lineChart.setAnimated(false);
		return lineChart;
	}
	
	public void drawGraph(List<Intermediate> history) {
		lineChart.getData().clear();
		xAxis.setLabel("Time into Problem (hours)");
		yAxis.setLabel("Characters");
		
        addCommentSeries(history);
        addCodeSeries(history);
        addVerticalMarker();
	}

	private void addVerticalMarker() {
		vertical = new Series<Number, Number>();
        vertical.setName("Selection");
        setVerticalLocation(0);
        lineChart.getData().add(vertical);
	}
	
	private void addCodeSeries(List<Intermediate> history) {
		XYChart.Series<Number,Number> codeSeries = new XYChart.Series<Number,Number>();
        codeSeries.setName("Code");
        for(Intermediate intermediate : history) {
        		double x = intermediate.workingHours;
        		if(intermediate.nonComments == 0) {
        			continue;
        		}
        		double y = intermediate.nonComments;
        		codeSeries.getData().add(new XYChart.Data<Number, Number>(x, y));
        		if(maxY == null || y > maxY) maxY = y;
        }
        lineChart.getData().add(codeSeries);
	}

	private void addCommentSeries(List<Intermediate> history) {
		XYChart.Series<Number,Number> commentSeries = new XYChart.Series<Number,Number>();
        commentSeries.setName("Comments");
        for(Intermediate intermediate : history) {
        		double x = intermediate.workingHours;
        		if(intermediate.totalComments == 0) {
        			continue;
        		}
        		double y = intermediate.totalComments;
        		commentSeries.getData().add(new XYChart.Data<Number, Number>(x, y));
        		if(maxY == null || y > maxY) maxY = y;
        }
        lineChart.getData().add(commentSeries);
	}
	
	public void setSelectedTime(double time) {
		setVerticalLocation(time);
	}

	private void setVerticalLocation(double loc) {
		vertical.getData().clear();
		vertical.getData().add(new XYChart.Data<Number, Number>(loc, 0));
        vertical.getData().add(new XYChart.Data<Number, Number>(loc + EPSILON, maxY));
	}


}
