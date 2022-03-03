package main_package;

import java.util.ArrayList;

// class for analyzing chart
public class ChartAnalyzer {
    public ArrayList<Integer> minMaxPoints; // list with all the extremums - minimum and maximum points of the chart
    int[] chart; // array with the chart values: chart[x] = y;
    int width, height;
    // width - width of the screen(see ChartConverter);
    // height - height of the screen(see ChartConverter);

    int chartHeight; // height of the chart - height without upper and lower empty parts
    double minDiffOfHeight; // minimal difference of height we need to determine if a point is Min or Max or none

    final int PERCENT = 1; // percent of chart height with which we calculate minDiffOfHeight

    //final Color black = new Color(0, 0, 0);

    private static final double QUARTER = 0.25; // the value to compare with

    ChartAnalyzer() { // constructor, in which we analyze the chart to get extremums
        ChartConverter converter = new ChartConverter();
        chart = converter.chart;
        if (chart==null) return;

        width = converter.width;
        height = converter.height;

        setChartHeight();
        setMinDiffOfHeight();

        getMinMaxPoints();
        //getClosest();
    }
    private void setMinDiffOfHeight() { // setter of minDiffOfHeight - it's (percent/100) * height (of the chart)
        minDiffOfHeight = (double) chartHeight * (PERCENT/100.0);
    }

    private void setChartHeight() { // setter of chart height - difference between max and min heights
        int min = height, max = 0;
        for (int x=0; x<width; x++) {
            min = Math.min(min, chart[x]);
            max = Math.max(max, chart[x]);
        }
        chartHeight = max - min;
    }

    private boolean isMinPoint(int pointX) { // check if the point(on x-axis) if minimum point
        int pointY = chart[pointX];
        boolean minLeft = false, minRight = false;
        for (int x=pointX-1; x>=0; x--) {
            int diff = chart[x] - pointY;
            if (diff>=0) return false;
            if (-diff>minDiffOfHeight) {
                minLeft = true; break;
            }
        }
        for (int x=pointX+1; x<width; x++) {
            int diff = chart[x] - pointY;
            if (diff>0) return false;
            if (-diff>minDiffOfHeight) {
                minRight = true; break;
            }
        }
        if (minLeft && minRight) return true;
        return false;
    }

    private boolean isMaxPoint(int pointX) { // check if the point(on x-axis) is maximum point
        int pointY = chart[pointX];
        boolean minLeft = false, minRight = false;
        for (int x=pointX-1; x>=0; x--) {
            int diff = chart[x] - pointY;
            if (diff<=0) return false;
            if (diff>minDiffOfHeight) {
                minLeft = true; break;
            }
        }
        for (int x=pointX+1; x<width; x++) {
            int diff = chart[x] - pointY;
            if (diff<0) return false;
            if (diff>minDiffOfHeight) {
                minRight = true; break;
            }
        }
        if (minLeft && minRight) return true;
        return false;
    }

    private void getMinMaxPoints() { // here we get all the extremums
        minMaxPoints = new ArrayList<>();
        minMaxPoints.add(0);
        for (int x=1; x<width-1; x++) {
            if (isMinPoint(x) || isMaxPoint(x)) {
                minMaxPoints.add(x);
            }
        }
        minMaxPoints.add(width - 1);
    }

    int getMaxJump() { // here we get maximum possible jump(in pixels) - minimum difference between two(min or max) points
        int maxJump = width;
        for (int i=0; i<minMaxPoints.size() - 1; i++) {
            maxJump = Math.min(maxJump, minMaxPoints.get(i+1) - minMaxPoints.get(i));
        }
        return maxJump;
    }

    double calculatePart(int jump) { // here we calculate the value for a particular jump
                                    // (or no jump at all -  only extremums)
        boolean[] take = new boolean[width];
        if (jump>0) {
            for (int x=0; x<width; x+=jump) {
                take[x] = true;
            }
        }
        for (int x:minMaxPoints) take[x] = true;

        int sum = 0, cnt = 0;
        int prev = 0;
        for (int x=1; x<width; x++) { // we start from one, because zero always belongs to minMaxPoints
            if (take[x]) {
                //System.out.println(Math.abs(chart[x] - chart[prev]));
                sum += Math.abs(chart[x] - chart[prev]);
                prev = x;
                cnt++;
            }
        }
        double average = (double) sum/cnt;
        //System.out.println(average+" "+sum+" "+cnt);
        return average/(chartHeight + 2); // + 2 pixels since we should never be able to reach minimum and maximum height
    }

    double[] getClosest() { // calculate the value, closest to QUARTER
        int maxJump = getMaxJump();
        /*System.out.println(Arrays.toString(chart));
        System.out.println(MinMaxPoints);
        System.out.println(maxJump);*/
        double optimalPart = 1, optimalDiff = 1;
        int optimalJump = -1; // step by extrema
        for (int jump = 1; jump<=maxJump; jump++) {
            double part = calculatePart(jump);
            if (Math.abs(QUARTER - part)<optimalDiff) {
                optimalPart = part;
                optimalDiff = Math.abs(QUARTER - part);
                optimalJump = jump;
            }

        }
        double part = calculatePart(-1);
        if (Math.abs(QUARTER - part)<optimalDiff) {
            optimalPart = part;
            optimalDiff = Math.abs(QUARTER - part);
            optimalJump = -1;
        }
        //System.out.println(optimalPart+" "+optimalJump+" "+optimalDiff);
        return new double[]{optimalPart, optimalJump};
    }

    boolean checkChart() { // check if chart exists - if the user has chosen a rectangle
        if (chart==null) return false;
        return true;
    }
}
