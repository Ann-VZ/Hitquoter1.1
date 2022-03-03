package main_package;

import java.awt.Color;
import java.awt.image.BufferedImage;

// class for converting chart from the image form into array form
public class ChartConverter {
    public BufferedImage screen, bw; // the part of screenshot, user wants to analyze
    int[] chart; // array with the chart values: chart[x] = y;
    int width, height;
    // width - width of the screen;
    // height - height of the screen;

    boolean[][] bwScreen; // black and white screen, put into boolean matrix
    // true - black, false - white

    final int MAX_GRAY = 164; // maximum value of gray, up to which we count our color black
    // if color is more than maxGray, than it's white

    //final Color black = new Color(0, 0, 0);
    //final Color white = new Color(255, 255, 255);

    ChartConverter() { // constructor, in which we transform chart from image to array
        ResizableRectangle myResRect = new ResizableRectangle();
        screen = myResRect.getScreenShot();
        if (screen==null) return;

        setWidthHeight();

        getBlackWhiteImage();

        getChart();
    }

    private void setWidthHeight() { // setter of width and height of image
        width = screen.getWidth();
        height = screen.getHeight();
    }

    private boolean checkClosestColor(Color color) { // here we get which color is closer to the  given color - black or white
        double Y = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
        if (Y<=MAX_GRAY) return true;
        else return false;
    }

    private void getBlackWhiteImage() { // method in which we transform image from colorful to black and white
        bwScreen = new boolean[width][height];

        //bw = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x=0; x<screen.getWidth(); x++) {
            for (int y=0; y<screen.getHeight(); y++) {
                int pixel = screen.getRGB(x, y);
                Color pixelColor = new Color(pixel);
                boolean isBlack = checkClosestColor(pixelColor);
                bwScreen[x][y] = isBlack;

                /*if (isBlack) bw.setRGB(x, y, black.getRGB());
                else bw.setRGB(x, y, white.getRGB());*/
            }
        }

        /*try {
            ImageIO.write(bw, "PNG",
                    new FileOutputStream("C:\\Компьютер\\Anna\\Java\\MyProjects\\ScreenShotAnalyzeProject\\MyBlackWhite.png"));
        } catch (IOException e) {
            return;
        }*/
    }

    void getChart() { // here we transform chart from black and white image to array
        chart = new int[width];
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                if (bwScreen[x][y]) {
                    chart[x] = y; break;
                }
            }
        }
    }

}
