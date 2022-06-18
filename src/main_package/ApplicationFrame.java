package main_package;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

public class ApplicationFrame extends JLabel {
    // information text
    public static final String TEXT = "The study of fluctuations in exchange quotations remains an extremely topical task. An extensive software and technological apparatus \n" +
            "was created to predict the market situation. However, its use is often ineffective in the specific practice of exchange trading. \n" +
            "Therefore, determination of a dependent (or independent) local randomness of an increase (or decrease) in the exchange price may be\n" +
            "in demand. \n" +
            "A nominal quote chart is a kind of broken line with points of highs and lows. We can copy it (or part of it) from the monitor screen \n" +
            "using my application Hitquoter 1.1 in Java 8 and turn it into a discrete array. Offered demo version of the program analyzes\n" +
            "a chart, which consists of red and green columns. Red columns mean decrease, green – increase.\n" +
            "We will compare the resulting array with independent random oscillations based on fractal constructions. \n" +
            "The “Chaos game\" method is known for the Sierpinski triangle. In its construction, we choose three random static points on the plane. \n" +
            "Let’s find the midpoint of the segment from any of these points (also chosen at random) to the fourth, random dynamic point. Now this \n" +
            "midpoint becomes a new nominal fourth dynamic point. The procedure is repeated.\n" +
            "We can simplify this construction and remove one vertex of the triangle. This has not been encountered in the topic of fractals in the \n" +
            "available literature. But the principle of randomness remains unchanged. The nominal third dynamic point will be defined as the \n" +
            "midpoint of the segment between the previous point and one randomly taken from the first two fixed points. If we take a pair of \n" +
            "adjacent dynamic points consecutively in pairs, they become segments of randomly varying length. \n" +
            "Let’s take the arithmetic mean of the lengths of these segments. At large number of iterations the ratio of this mean to the length of \n" +
            "the segment between the first two static points will be approximately equal to ¼. We propose to use this fact to analyze quotes for \n" +
            "local randomness.\n" +
            "My application Hitquoter 1.1 performs calculations of the change in value from the minimum one pixel step along the x-axis to the step \n" +
            "between the extremes. The program calculates the ratio of the amplitudes to the maximum difference of values in the selected fragment \n" +
            "of the chart and displays the result that is closest to ¼ in the window. In the result line, the first place is the absolute value of \n" +
            "the ratio, the second place takes that value in percent relative to ¼.\n" +
            "Approximation of the found values to ¼ allows us to consider changes in the chosen quote as locally independent (random).\n" +
            "The Hitquoter 1.1 program is written in Java 8 in the IntelliJ IDEA 2020.2.3 IDE using Git.\n" +
            "Huge thanks to my father for advice on the concept.\n" +
            "08 March 2022, Anna Zoikina";

    ApplicationFrame() {
        JFrame frame = new JFrame("Hitquoter 1.1"); // our main frame
        frame.setBounds(10, 20, 700, 500);
        frame.setMinimumSize(new Dimension(500, 350));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel greetingLabel = new JLabel("<html><body style='text-align: center'>Program for estimating the independence of" +
                "<br>local fluctuations in stock quotes, version 1.1"); // the label with the heading
        greetingLabel.setHorizontalAlignment(JLabel.CENTER);
        greetingLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        greetingLabel.setFont(new Font("Monotype Corsiva", Font.ITALIC, 35));
        greetingLabel.setForeground(new Color(10, 63, 222));
        greetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea output = new JTextArea("", 10, 20); // output for results
        output.setBorder(new EmptyBorder(5, 10, 10, 10));
        output.setLineWrap(true);
        output.setEditable(false);
        output.setFont(new Font("Verdana", Font.PLAIN, 18));

        JButton informationButton = new JButton("Information button"); // button for information about the program
        informationButton.setFont(new Font("Arial", Font.PLAIN, 20));
        informationButton.setPreferredSize(new Dimension(200, 50));
        informationButton.addActionListener(e -> { // show information on the project
            UIManager.put("OptionPane.okButtonText", "OK");
            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Arial", Font.ITALIC,20)));
            UIManager.put("Button.foreground", new Color(12, 12, 199));

            UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Arial", Font.BOLD, 16)));
            //UIManager.put("OptionPane.messageForeground", new Color(111, 105, 105));
            JOptionPane.showMessageDialog(frame, TEXT, "Information on the program", JOptionPane.PLAIN_MESSAGE);
        });

        JButton programButton = new JButton("Program button"); // button for running the main program - analyzing a chart
        programButton.setFont(new Font("Arial", Font.PLAIN, 20));
        programButton.setPreferredSize(new Dimension(200, 50));
        programButton.addActionListener(e -> { // analyze a chart
            ChartAnalyzer analyzer = new ChartAnalyzer();
            if (!analyzer.checkChart()) {
                output.append("The user hasn't chosen a rectangle!\n");
            } else if (!analyzer.checkIfValid()) {
                output.append("The rectangle isn't valid!\n");
            } else { // get and calculate values for output
                double absValue = analyzer.calculateRatio();
                String abs = String.format("%.3f", absValue);
                double percentage = 400 * absValue;
                String per = String.format("%.1f", percentage);
                output.append(abs+"   "+per+" percent\n");
            }
        });

        Container container = frame.getContentPane(); // container with all the components for frame
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(greetingLabel);


        JPanel mainPanel = new JPanel(); // panel for buttons

        GridLayout mainPanelLayout = new GridLayout(1, 2);
        mainPanelLayout.setVgap(20);
        mainPanelLayout.setHgap(40);
        mainPanel.setLayout(mainPanelLayout);
        mainPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        /*mainPanel.setLayout(new FlowLayout());
        ((FlowLayout)mainPanel.getLayout()).setHgap(100);
        ((FlowLayout) mainPanel.getLayout()).setVgap(25);*/
        mainPanel.add(informationButton);
        mainPanel.add(programButton);
        //mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        container.add(mainPanel);

        JLabel textLabel = new JLabel("Results(absolute value, percentage)"); // label above output
        textLabel.setFont(new Font("Monotype Corsiva", Font.ITALIC, 25));
        textLabel.setBorder(new EmptyBorder(0, 0, 5, 5));
        textLabel.setForeground(new Color(10, 63, 222));
        textLabel.setAlignmentX(CENTER_ALIGNMENT);

        JScrollPane resultsScroll = new JScrollPane(output); // JScrollPane - so that the user can scroll the output
        //resultsScroll.setPreferredSize(new Dimension(500, 500));
        //container.add(resultsScroll);

        JPanel textPanel = new JPanel(); // panel with output and its label
        //textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(textLabel);
        textPanel.add(resultsScroll);
        textPanel.setBorder(new EmptyBorder(20, 20, 20, 20));


        container.add(textPanel);

        frame.setIconImage(new ImageIcon(getClass().getResource("/Icon3.png")).getImage());
        //setting icon of the frame

        frame.setVisible(true); // setting frame visible
    }
}
