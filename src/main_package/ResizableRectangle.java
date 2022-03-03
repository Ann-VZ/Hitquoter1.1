package main_package;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;

public class ResizableRectangle {
    static JLabel screenLabel; // label with the screenshot, which we present to the user
    static BufferedImage screen, screenCopy;
    // screen - our screenshot
    // screenCopy - a copy of screen to allow us repainting the chosen rectangle, when we change it

    static Rectangle captureRect; //rectangle a user selects on the screen

    boolean upperHeld = false, lowerHeld = false, leftHeld = false, rightHeld = false;
    boolean rectangleHeld = false;
    // upperHeld - variable, which shows us if the user wants to move upper side of the rectangle
    // lowerHeld - variable, which shows us if the user wants to move lower side of the rectangle
    // leftHeld - variable, which shows us if the user wants to move left side of the rectangle
    // rightHeld - variable, which shows us if the user wants to move right side of the rectangle
    // rectangleHeld - variable, which shows us if the user wants to move the whole rectangle
    // originally user doesn't want to move anything

    int diffStartX = 0, diffStartY = 0;
    // variables to calculate upper left point when we're moving the whole rectangle
    // diffStartX - difference between X ot the upper left point and X of the point on the rectangle we're holding
    // diffStartY - difference between Y ot the upper left point and Y of the point on the rectangle we're holding

    final int DIFF_AREA = 10, MIN_SIZE = 45;
    // diffArea - the integer value in pixels;
    // if we have the side of our rectangle than ------------|------------ , meaning:
    //                                           diffArea  side  diffArea
    // when user wants to take and move the side of the rectangle, he/she might not be able to get the mouse directly on top of this side
    // that there's diffArea - if the user get into it from the left or right(top or bottom) than he/she is still holding the side

    // minSize - minimum size of the side of the rectangle

    Point start = new Point(); // upper left point of the future rectangle;
    // needed to create the first instance of the rectangle

    ResizableRectangle() { // simple constructor, allowing us to create an instance of the class
    }

    ResizableRectangle(final BufferedImage screen) { //consructor in which we create joptionpane
        // which allows us to get a rectangle of interest on the screenshot
        screenCopy = new BufferedImage(
                screen.getWidth(),
                screen.getHeight(),
                screen.getType());
        screenLabel = new JLabel(new ImageIcon(screenCopy));
        JScrollPane screenScroll = new JScrollPane(screenLabel);

        screenScroll.setPreferredSize(new Dimension(
                (int)(screen.getWidth()*0.90),
                (int)(screen.getHeight()*0.70)));

        final JLabel messageLabel = new JLabel("Please, choose the part of a chart You want to copy for program work" +
                "(drag a rectangle in the screen shot)");
        messageLabel.setFont(new Font("MONOTYPE CORSIVA", Font.ITALIC,25));
        messageLabel.setForeground(Color.BLUE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(screenScroll, BorderLayout.CENTER);
        panel.add(messageLabel, BorderLayout.SOUTH);

        repaint(screen, screenCopy);
        screenLabel.repaint();
        screenLabel.addMouseListener(new MouseEventListener());
        screenLabel.addMouseMotionListener(new MouseMotionEventListener());

        UIManager.put("OptionPane.okButtonText", "Confirm");
        //UIManager.put("OptionPane.okButtonColor", Color.BLUE);
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("MONOTYPE CORSIVA", Font.ITALIC,50)));
        UIManager.put("Button.foreground", new Color(80, 150, 255));

        JOptionPane.showMessageDialog(null, panel, "Screenshot window for forecast", JOptionPane.PLAIN_MESSAGE);
    }

    public void repaint(BufferedImage orig, BufferedImage copy) { // repaints the screenshot adding a rectangle
        Graphics2D g = copy.createGraphics();
        g.drawImage(orig,0,0, null);
        if (captureRect!=null) {
            g.setColor(Color.BLUE);
            g.draw(captureRect);
            g.setColor(new Color(160,245,170,200));
            g.fill(captureRect);
        }
        g.dispose();
    }

    public BufferedImage getScreenShot() { // takes screenshot and returns the part of the it the user chooses
        try {
            Robot robot = new Robot();
            final Dimension screenSize = Toolkit.getDefaultToolkit().
                    getScreenSize();
            screen = robot.createScreenCapture(
                    new Rectangle(screenSize));

            new ResizableRectangle(screen);

            BufferedImage resultingScreen = cutScreenShot(screen, captureRect);
            return resultingScreen;
        } catch (AWTException e) {
            return null;
        }
    }

    public BufferedImage cutScreenShot(BufferedImage screen, Rectangle rect) { // cuts out preselected rectangle from the screenshot
        // and returns the resulting image

        if (rect==null) { // the user didn't choose a rectangle
            return null;
        }

        int maxWidth = screen.getWidth(), maxHeight = screen.getHeight();
        int xTopLeft = rect.x, yTopLeft = rect.y;
        xTopLeft = Math.min(Math.max(xTopLeft, 1), maxWidth); yTopLeft = Math.min(Math.max(yTopLeft, 1), maxHeight);

        int xLowerRight = rect.x + rect.width, yLowerRight = rect.y + rect.height;
        xLowerRight = Math.min(Math.max(xLowerRight, 1), maxWidth); yLowerRight = Math.min(Math.max(yLowerRight, 1), maxHeight);

        BufferedImage clippedScreen = screen.getSubimage(xTopLeft, yTopLeft,
                xLowerRight - xTopLeft + 1, yLowerRight - yTopLeft + 1);

        return clippedScreen;
    }

    class MouseEventListener extends MouseAdapter { // class is called when user clicks(presses or releases) the mouse
        @Override
        public void mousePressed(MouseEvent e) { // mouse is pressed - the user either wants to create a new rectangle or
            // change the one already created
            super.mousePressed(e);

            start = e.getPoint();
            clearBorders();

            if (captureRect!=null) {
                int width = captureRect.width, height = captureRect.height;

                Point upperLeft = new Point(captureRect.x, captureRect.y);
                Point upperRight = new Point(captureRect.x + width, captureRect.y);
                Point lowerLeft = new Point(captureRect.x, captureRect.y + height);
                Point lowerRight = new Point(captureRect.x + width, captureRect.y + height);

                if (upperLeft.y - DIFF_AREA<start.y && start.y<upperLeft.y + DIFF_AREA && // We select the upper side
                        upperLeft.x<start.x && start.x<upperRight.x) {                  // of rectangle for moving
                    //System.out.println("We're prepared to move upper side!");
                    upperHeld = true;
                } else if (lowerLeft.y - DIFF_AREA<start.y && start.y<lowerLeft.y + DIFF_AREA && // We select the lower side
                        lowerLeft.x<start.x && start.x<lowerRight.x) {                         // of rectangle for moving
                    //System.out.println("We're prepared to move lower side!");
                    lowerHeld = true;
                } else if (upperLeft.x - DIFF_AREA<start.x && start.x<upperLeft.x + DIFF_AREA && // We select the left side
                        upperLeft.y<start.y && start.y<lowerLeft.y) {                          // of rectangle for moving
                    //System.out.println("We're prepared to move left side!");
                    leftHeld = true;
                } else if (upperRight.x - DIFF_AREA<start.x && start.x<upperRight.x + DIFF_AREA && // We select the right side
                        upperRight.y<start.y && start.y<lowerRight.y) {                          // of rectangle for moving
                    //System.out.println("We're prepared to move right side!");
                    rightHeld = true;
                } else if (upperLeft.x<start.x && upperLeft.y<start.y && // We select the whole rectangle for moving
                        lowerRight.x>start.x && lowerRight.y>start.y) {
                    //System.out.println("We want to move our full rectangle!");
                    rectangleHeld = true;
                    diffStartX = start.x - upperLeft.x;
                    diffStartY = start.y - upperLeft.y;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) { // mouse is released: we're not doing anything with the rectangle at the moment
            super.mouseReleased(e);
            clearBorders();
        }

        public void clearBorders() { // set variables: the user doesn't want to move any sides or the whole rectangle
            upperHeld = false;
            lowerHeld = false;
            leftHeld = false;
            rightHeld = false;
            rectangleHeld = false;
        }
    }

    class MouseMotionEventListener extends MouseMotionAdapter { // class we call when the mouse is dragged
        // because we need to update or create a rectangle
        @Override
        public void mouseDragged(MouseEvent e) { // mouse is dragged across the screenshot - we need to update(or create) our rectangle
            super.mouseDragged(e);
            Point end = e.getPoint();

            Rectangle newRect;
            if (captureRect!=null) newRect = new Rectangle(captureRect);

            if (upperHeld) {                                                    // We're moving upper side
                Point upperLeftPoint = captureRect.getLocation();
                newRect = new Rectangle(new Point(upperLeftPoint.x, end.y),
                        new Dimension(captureRect.width, captureRect.height + (upperLeftPoint.y - end.y)));
            } else if (lowerHeld) {                                             // We're moving lower side
                Point upperLeftPoint = captureRect.getLocation();
                newRect = new Rectangle(upperLeftPoint,
                        new Dimension(captureRect.width, end.y - upperLeftPoint.y));
            } else if (leftHeld) {                                              // We're moving left side
                Point upperLeftPoint = captureRect.getLocation();
                newRect = new Rectangle(new Point(end.x, upperLeftPoint.y),
                        new Dimension(captureRect.width + (upperLeftPoint.x - end.x), captureRect.height));
            } else if (rightHeld) {                                             // We're moving right side
                Point upperLeftPoint = captureRect.getLocation();
                newRect = new Rectangle(upperLeftPoint,
                        new Dimension(end.x - upperLeftPoint.x, captureRect.height));
            } else if (rectangleHeld) {                                         // We're moving the whole rectangle
                newRect = new Rectangle(new Point(end.x - diffStartX, end.y - diffStartY),
                        captureRect.getSize());
            } else {                                                            // We're creating a new rectangle
                // (with width and height of at least minSize)
                newRect = new Rectangle(start,
                        new Dimension(Math.max(MIN_SIZE, end.x-start.x), Math.max(MIN_SIZE, end.y-start.y)));
            }

            if (newRect.width<MIN_SIZE || newRect.height<MIN_SIZE) return; // if a user is trying to change or create
            // a rectangle of less than minimum size we ignore that attempt

            captureRect = new Rectangle(newRect); // update the rectangle

            repaint(screen, screenCopy); // update the image with screenshot
            screenLabel.repaint(); // update the label which holds this image
        }
    }
}