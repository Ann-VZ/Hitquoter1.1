package main_package;

import java.util.ArrayList;
import java.util.Arrays;

// class for analyzing chart
public class ChartAnalyzer {
    Column[] chart; // array with the chart values: chart[x] = y;
    int width, height;
    // width - width of the screen(see ChartConverter);
    // height - height of the screen(see ChartConverter);

    int chartHeight; // height of the chart - height without upper and lower empty(white) parts
    double minDiffOfHeight, mean;
    // minimal difference of height needed to determine if two points are approximately on the same level
    // resulting mean(average) of column height

    final int PERCENT = 1; // percent of chart height with which we calculate minDiffOfHeight
    boolean valid = true; // shows us, if the rectangle the user selected is valid (has columns)

    //final Color black = new Color(0, 0, 0);

   // private static final double QUARTER = 0.25; // the value to compare with

    ChartAnalyzer() { // constructor, in which we analyze the chart to get columns, and afterwords - average height of these columns
        ChartConverter converter = new ChartConverter();
        chart = converter.chart;
        if (chart==null) return;

        width = converter.width;
        height = converter.height;

        ArrayList<Column> columns = getColumns(); // convert chart from matrix to array of columns

        if (columns.isEmpty()) {
            valid = false;
            return;
        }

        setChartHeight(columns);
        minDiffOfHeight = chartHeight * PERCENT / 100.0;

        mergeIdenticalColumns(columns);
    }

    double calculateRatio() { // calculate ratio: average/height of chart without empty upper and lower parts
        return mean/chartHeight;
    }

    void setChartHeight(ArrayList<Column> columns) { // setter of chart height without empty upper and lower parts
        int start = height, end = 0;
        for (Column c:columns) { // calculate lower and upper borders of our chart
            start = Math.min(start, c.start);
            end = Math.max(end, c.end);
        }
        chartHeight = end - start + 3;
    }

    ArrayList<Column> mergeIdenticalColumns(ArrayList<Column> columns) { // function, in which we merge
        // columns, if they are approximately the same and stand next to each other

        int size = columns.size();
        ArrayList<Column> columnChart = new ArrayList<>();
        columnChart.add(columns.get(0));
        long sum = 0;
        int cnt = 0;
        for (int i=1; i<size; i++) {
            Column curr = columns.get(i);
            Column prev = columns.get(i-1);

            // if a column if like the previous one, we simply ignore it
            if (curr.start>prev.start - minDiffOfHeight && curr.start<prev.start + minDiffOfHeight &&
                    curr.end>prev.end - minDiffOfHeight && curr.end<curr.end + minDiffOfHeight &&
                    curr.color == prev.color) {
                continue;
            }

            // else (if a column is normal), we calculate the mean height of these normal columns
            columnChart.add(curr);
            sum+=(curr.end - curr.start);
            cnt++;
        }
        mean = (double) sum/cnt;
        return columnChart;
    }

    ArrayList<Column> getColumns() { // function, in which we divide the chart into red and green columns
        // each column consists of several one-pixel columns, which we merge into a large one

        int red = 0, green = 0;
        double s = 0, t = 0;
        ArrayList<Column> columns = new ArrayList<>();

        // we calculate the mean of starting points and mean of ending points for each column
        // for this we calculate sum of starts and sum of ends, and then divide than by tha amount of these points

        for (int x=0; x<width; x++) {
            Column column = chart[x];
            if (column.color>0) {
                s+=column.start; t+=column.end;
            }

            if (column.color==1) red++;
            else if (column.color==2) green++;
            else {
                if (red>0) {
                    columns.add(new Column((int)s/red, (int)t/red, 1));
                }
                if (green>0) {
                    columns.add(new Column((int)s/green, (int)t/green, 2));
                }
                red = 0; green = 0; s = 0; t = 0;
            }
        }
        return columns;
    }

    boolean checkChart() { // check if chart exists - if the user has chosen a rectangle
        if (chart==null) return false;
        return true;
    }

    boolean checkIfValid() {
        return valid;
    }

    /*public static void main(String[] args) {
        ChartAnalyzer analyzer = new ChartAnalyzer();
    }*/
}
