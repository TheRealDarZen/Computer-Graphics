import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Interface extends JFrame {
    private DrawPanel drawPanel;
    private JRadioButton lineButton, rectButton, circleButton;
    private JTextField rField, gField, bField;
    private JButton saveButton, loadButton, saveImageButton;

    public Interface() {
        setTitle("Vector Graphic Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        lineButton = new JRadioButton("Line", true);
        rectButton = new JRadioButton("Rectangle");
        circleButton = new JRadioButton("Circle");

        ButtonGroup shapeGroup = new ButtonGroup();
        shapeGroup.add(lineButton);
        shapeGroup.add(rectButton);
        shapeGroup.add(circleButton);

        controls.add(lineButton);
        controls.add(rectButton);
        controls.add(circleButton);

        rField = new JTextField("0", 3);
        gField = new JTextField("0", 3);
        bField = new JTextField("0", 3);

        controls.add(new JLabel("R:"));
        controls.add(rField);
        controls.add(new JLabel("G:"));
        controls.add(gField);
        controls.add(new JLabel("B:"));
        controls.add(bField);

        saveButton = new JButton("Save");
        loadButton = new JButton("Load");
        saveImageButton = new JButton("Save as PNG");

        saveButton.addActionListener(e -> drawPanel.saveShapes());
        loadButton.addActionListener(e -> drawPanel.loadShapes());
        saveImageButton.addActionListener(e -> drawPanel.saveAsImage());

        controls.add(saveButton);
        controls.add(loadButton);
        controls.add(saveImageButton);

        add(controls, BorderLayout.SOUTH);
        setVisible(true);
    }

    private class DrawPanel extends JPanel {
        private ArrayList<Shape> shapes = new ArrayList<>();
        private Point startPoint, previewEndPoint;
        private Shape previewShape = null;
        private Shape selectedShape = null;
        private boolean isDragging = false;
        private boolean isResizingStart = false;
        private boolean isResizingEnd = false;
        private Point selectedPoint = null;

        public DrawPanel() {
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint();
                    selectedShape = null;
                    selectedPoint = null;

                    for (Shape shape : shapes) {

                        if (SwingUtilities.isRightMouseButton(e)) {
                            if (isNearPoint(startPoint, getShapeCenter(shape))) {
                                shapes.remove(shape);
                                repaint();
                                break;
                            }
                        }

                        if (shape instanceof Line) {
                            Line line = (Line) shape;
                            if (isNearPoint(getShapeCenter(line), startPoint)) {
                                selectedShape = shape;
                                selectedPoint = getShapeCenter(line);
                                isDragging = true;
                                return;
                            }
                            else if(isNearPoint(line.start, startPoint) || isNearPoint(line.end, startPoint)) {
                                if (line.start.distance(startPoint) <= line.end.distance(startPoint) ) {
                                    selectedShape = shape;
                                    selectedPoint = line.start;
                                    isResizingStart = true;
                                }
                                else {
                                    selectedShape = shape;
                                    selectedPoint = line.end;
                                    isResizingEnd = true;
                                }
                                return;
                            }

                        } else if (shape instanceof Rectangle) {
                            Rectangle rect = (Rectangle) shape;
                            if (isNearPoint(getShapeCenter(rect), startPoint)) {
                                selectedShape = shape;
                                selectedPoint = getShapeCenter(rect);
                                isDragging = true;
                                return;
                            }
                        } else if (shape instanceof Circle) {
                            Circle circle = (Circle) shape;
                            if (isNearPoint(getShapeCenter(circle), startPoint)) {
                                selectedShape = shape;
                                selectedPoint = getShapeCenter(circle);
                                isDragging = true;
                                return;
                            }
                        }
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    if (selectedShape == null) {
                        Point endPoint = e.getPoint();
                        Color color = new Color(
                                Integer.parseInt(rField.getText()),
                                Integer.parseInt(gField.getText()),
                                Integer.parseInt(bField.getText())
                        );

                        if (lineButton.isSelected()) {
                            shapes.add(new Line(startPoint, endPoint, color));
                        } else if (rectButton.isSelected()) {
                            shapes.add(new Rectangle(startPoint, endPoint, color));
                        } else if (circleButton.isSelected()) {
                            shapes.add(new Circle(startPoint, endPoint, color));
                        }
                    }
                    previewShape = null;
                    isDragging = false;
                    isResizingStart = false;
                    isResizingEnd = false;
                    selectedPoint = null;
                    selectedShape = null;
                    repaint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (isResizingEnd && selectedShape instanceof Line line) {
                        line.end = e.getPoint();
                        repaint();
                    }
                    else if (isResizingStart && selectedShape instanceof Line line) {
                        line.start = e.getPoint();
                        repaint();
                    }
                    else if (isDragging && selectedShape != null) {

                        switch (selectedShape) {
                            case Line line -> {
                                int dx = e.getX() - startPoint.x;
                                int dy = e.getY() - startPoint.y;
                                line.start.x += dx;
                                line.start.y += dy;
                                line.end.x += dx;
                                line.end.y += dy;
                            }
                            case Rectangle rect -> {
                                int dx = e.getX() - startPoint.x;
                                int dy = e.getY() - startPoint.y;
                                rect.start.x += dx;
                                rect.start.y += dy;
                                rect.end.x += dx;
                                rect.end.y += dy;
                            }
                            case Circle circle -> {
                                int dx = e.getX() - startPoint.x;
                                int dy = e.getY() - startPoint.y;
                                circle.start.x += dx;
                                circle.start.y += dy;
                                circle.end.x += dx;
                                circle.end.y += dy;
                            }
                            default -> {
                            }
                        }
                        startPoint = e.getPoint();
                    }
                    else {

                        if (startPoint == null) return;

                        previewEndPoint = e.getPoint();
                        Color color = new Color(
                                Integer.parseInt(rField.getText()),
                                Integer.parseInt(gField.getText()),
                                Integer.parseInt(bField.getText())
                        );

                        if (lineButton.isSelected()) {
                            previewShape = new Line(startPoint, previewEndPoint, color);
                        } else if (rectButton.isSelected()) {
                            previewShape = new Rectangle(startPoint, previewEndPoint, color);
                        } else if (circleButton.isSelected()) {
                            previewShape = new Circle(startPoint, previewEndPoint, color);
                        }
                    }
                    repaint();
                }
            });

        }

        private boolean isNearPoint(Point p1, Point p2) {
            return p1.distance(p2) <= 20;
        }

        private Point getShapeCenter(Shape shape) {
            if (shape instanceof Line line) {
                return new Point((line.start.x + line.end.x) / 2, (line.start.y + line.end.y) / 2);
            } else if (shape instanceof Rectangle rect) {
                return new Point((rect.start.x + rect.end.x) / 2, (rect.start.y + rect.end.y) / 2);
            } else if (shape instanceof Circle circle) {
                return new Point(circle.start.x, circle.start.y);
            }
            return null;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Shape shape : shapes) {
                shape.draw(g);
            }

            if (previewShape != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                float[] dashPattern = { 5, 5 };
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
                previewShape.draw(g2d);
                g2d.dispose();
            }
        }

        public void saveAsImage() {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".png");
                BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                paint(g2d);
                g2d.dispose();
                try {
                    ImageIO.write(image, "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void saveShapes() {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    for (Shape shape : shapes) {
                        writer.write(shape.getClass().getSimpleName() + "," + shape.start.x + "," + shape.start.y + "," + shape.end.x +
                                "," + shape.end.y + "," + shape.color.getRed() + "," + shape.color.getGreen() + "," +
                                shape.color.getBlue() + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void loadShapes() {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".txt")) {
                    JOptionPane.showMessageDialog(this, "Invalid file format. Please select a .txt file.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                shapes.clear();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length != 8) continue;
                        Color color = new Color(Integer.parseInt(parts[5]), Integer.parseInt(parts[6]), Integer.parseInt(parts[7]));
                        switch (parts[0]) {
                            case "Line" ->
                                    shapes.add(new Line(new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])),
                                            new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])), color));
                            case "Rectangle" ->
                                    shapes.add(new Rectangle(new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])),
                                            new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])), color));
                            case "Circle" ->
                                    shapes.add(new Circle(new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])),
                                            new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])), color));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                repaint();
            }
        }
    }
}
