import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Test extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int NUM_TRIANGLES = 100;

    private BufferedImage bufferedImage;
    private ArrayList<Triangle> triangles;
    private Random random;
    private JPanel renderPanel;
    private JPanel controlPanel;
    private boolean renderToBuffer = true;  // true - BufferedImage, false - Graphics

    public Test() {
        super("Gouraud Shading");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());

        random = new Random();
        triangles = new ArrayList<>();
        bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        // Render panel
        renderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                long startTime = System.nanoTime();
                int totalPixels = 0;

                if (renderToBuffer) {

                    Graphics2D g2d = bufferedImage.createGraphics();
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, WIDTH, HEIGHT);
                    g2d.dispose();

                    for (Triangle triangle : triangles) {
                        triangle.renderGouraud(bufferedImage);
                        // Approximate number of pixels in a triangle
                        totalPixels += calculateTrianglePixels(triangle);
                    }

                    g.drawImage(bufferedImage, 0, 0, this);

                } else {

                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, WIDTH, HEIGHT);

                    for (Triangle triangle : triangles) {
                        triangle.renderGouraud(g);
                        // Approximate number of pixels in a triangle
                        totalPixels += calculateTrianglePixels(triangle);
                    }
                }

                long endTime = System.nanoTime();
                double timeInSeconds = (endTime - startTime) / 1_000_000_000.0;
                double trianglesPerSecond = triangles.size() / timeInSeconds;
                double pixelsPerSecond = totalPixels / timeInSeconds;

                // Statistics
                g.setColor(Color.WHITE);
                g.drawString(String.format("Render Time: %.4f s", timeInSeconds), 10, 20);
                g.drawString(String.format("Triangles/s: %.2f", trianglesPerSecond), 10, 40);
                g.drawString(String.format("Pixels/s: %.2f", pixelsPerSecond), 10, 60);
                g.drawString(String.format("Triangle count: %d", triangles.size()), 10, 80);
                g.drawString(String.format("Render Method: %s", renderToBuffer ? "To Buffer" : "On Screen"), 10, 120);
            }
        };
        renderPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(renderPanel, BorderLayout.CENTER);

        // Control Panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        // Test buttons
        JButton addButton = new JButton("Add a triangle");
        addButton.addActionListener(e -> {
            addRandomTriangle();
            renderPanel.repaint();
        });

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            triangles.clear();
            bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            renderPanel.repaint();
        });

        JButton testCaseButton = new JButton("Test cases");
        testCaseButton.addActionListener(e -> {
            triangles.clear();
            addTestCases();
            renderPanel.repaint();
        });

        JButton benchmarkButton = new JButton("Performance test");
        benchmarkButton.addActionListener(e -> {
            triangles.clear();
            for (int i = 0; i < NUM_TRIANGLES; i++) {
                addRandomTriangle();
            }
            renderPanel.repaint();
        });

        JCheckBox bufferCheckBox = new JCheckBox("Render to buffer", renderToBuffer);
        bufferCheckBox.addActionListener(e -> {
            renderToBuffer = bufferCheckBox.isSelected();
            renderPanel.repaint();
        });

        controlPanel.add(addButton);
        controlPanel.add(clearButton);
        controlPanel.add(testCaseButton);
        controlPanel.add(benchmarkButton);
        controlPanel.add(bufferCheckBox);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addRandomTriangle() {
        // Random triangle points
        int x1 = random.nextInt(WIDTH);
        int y1 = random.nextInt(HEIGHT);
        int x2 = random.nextInt(WIDTH);
        int y2 = random.nextInt(HEIGHT);
        int x3 = random.nextInt(WIDTH);
        int y3 = random.nextInt(HEIGHT);

        // Random colors
        Color c1 = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color c2 = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color c3 = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        triangles.add(new Triangle(x1, y1, x2, y2, x3, y3, c1, c2, c3));
    }

    // Add set triangles for testing
    private void addTestCases() {
        // Test Case 1: Equilateral triangle
        triangles.add(new Triangle(
                400, 100,
                300, 300,
                500, 300,
                Color.RED, Color.GREEN, Color.BLUE
        ));

        // Test Case 2: Long and narrow triangle
        triangles.add(new Triangle(
                100, 400,
                700, 450,
                400, 500,
                Color.YELLOW, Color.CYAN, Color.MAGENTA
        ));

        // Test Case 3: Triangle with slightly different colors
        triangles.add(new Triangle(
                200, 200,
                300, 500,
                100, 500,
                new Color(200, 100, 100),
                new Color(210, 110, 110),
                new Color(220, 120, 120)
        ));

        // Test Case 4: Degenerated triangle
        triangles.add(new Triangle(
                600, 400,
                601, 395,
                700, 300,
                Color.WHITE, Color.GRAY, Color.DARK_GRAY
        ));
    }

    private int calculateTrianglePixels(Triangle triangle) {
        int[] xPts = triangle.getXPts();
        int[] yPts = triangle.getYPts();

        // Finding an area using Heron formula
        double a = distance(xPts[0], yPts[0], xPts[1], yPts[1]);
        double b = distance(xPts[1], yPts[1], xPts[2], yPts[2]);
        double c = distance(xPts[2], yPts[2], xPts[0], yPts[0]);

        double s = (a + b + c) / 2;
        double area = Math.sqrt(s * (s - a) * (s - b) * (s - c));

        return (int) area;
    }

    private double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

}