package main_package;

import java.awt.Color;
import java.awt.image.BufferedImage;

// class for converting chart from the image form into array form
public class ChartConverter {
    public BufferedImage screen; // the part of screenshot, user wants to analyze
    Column[] chart; // array with the chart values: chart[x] = y;

    int width, height;
    double minimumHeight;
    // width - width of the screen;
    // height - height of the screen;
    // minimum height of one column(in pixels)

    int[][] convertedScreen; // screen, in which pixels are converted into numbers
    // 0 - black or white, 1 - red, 2 - green

    final double PERCENT = 0.5; // percent of height for the calculation of minimumHeight
    // (if we've got a column with height less than PERCENT of the full height, than we ignore it)

    //final Color black = new Color(0, 0, 0);
    //final Color white = new Color(255, 255, 255);

    ChartConverter() { // constructor, in which we transform chart from image to array
        ResizableRectangle myResRect = new ResizableRectangle();
        screen = myResRect.getScreenShot();
        if (screen==null) return;

        setWidthHeight();

        getLimitedColorImage();

        getChart();
    }

    private void setWidthHeight() { // setter of width and height of image, and minimumHeight
        width = screen.getWidth();
        height = screen.getHeight();
        minimumHeight = (double) height * PERCENT / 100.0;
    }

    private double colorDistance(Color c1, Color c2) { // function for the calculation of distance between two colors
        int red1 = c1.getRed();
        int red2 = c2.getRed();
        int rmean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
    }

    private void getLimitedColorImage() { // here we check color of each pixel, than convert each color to a number (see 'convertedScreen')
        convertedScreen = new int[width][height];
        Color[] constantColors = new Color[] {Color.black, Color.white, Color.red, Color.green};

        //bw = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x=0; x<screen.getWidth(); x++) {
            for (int y=0; y<screen.getHeight(); y++) {
                Color pixel = new Color(screen.getRGB(x, y));
                double nearestDistance = Integer.MAX_VALUE;
                int nearestColor = 0;

                for (int i=0; i<4; i++) { // find the color with minimum distance to current pixel color
                    double distance = colorDistance(pixel, constantColors[i]);
                    if (distance<nearestDistance) {
                        nearestDistance = distance;
                        nearestColor = i;
                    }
                }
                convertedScreen[x][y] = Math.max(nearestColor-1, 0);

                /*if (nearestColor==0 || nearestColor==1) bw.setRGB(x, y, Color.white.getRGB());
                else bw.setRGB(x, y, constantColors[nearestColor].getRGB());*/
            }
        }

        /*try {
            ImageIO.write(bw, "PNG",
                    new FileOutputStream("C:\\Компьютер\\Anna\\Java\\MyProjects\\Hitquoter\\MyBlackWhite.png"));
        } catch (IOException e) {
            return;
        }*/
    }

    void getChart() { // here we transform chart from image to array
        // in each x we find the longest segment of colorful(red or green) pixels
        chart = new Column[width];
        for (int x=0; x<width; x++) {
            int red = 0, green = 0;
            int diff = Integer.MIN_VALUE, min = 0, max = 0, type = 0;
            for (int y=0; y<height; y++) {
                if (convertedScreen[x][y]==1) { // if we stand on red pixel
                    red++; green = 0;
                    if (red>diff) { // check if current red segment is longer than current maximum
                        diff = red;
                        min = y - diff + 1; max = y; type = 1;
                    }
                } else if (convertedScreen[x][y]==2) { // if we stand on green pixel
                    green++; red = 0;
                    if (green>diff) { // check if current green segment is longer than current maximum
                        diff = green;
                        min = y - diff + 1; max = y; type = 2;
                    }
                } else { // if we stand on white
                    red = 0; green = 0;
                }
            }
            if (diff>minimumHeight) {
                chart[x] = new Column(min, max, type);
            } else {
                chart[x] = new Column(0, height-1, 0);
            }
        }
    }
}
