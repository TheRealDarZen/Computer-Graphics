import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Interface extends JFrame {
    private DrawPanel drawPanel;
    private JRadioButton lineButton, rectButton, circleButton;
    private JTextField rField, gField, bField;

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

        add(controls, BorderLayout.SOUTH);
        setVisible(true);
    }

    private class DrawPanel extends JPanel {
        private ArrayList<Shape> shapes = new ArrayList<>();
        private Point startPoint;

        public DrawPanel() {
            setBackground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint();
                }

                public void mouseReleased(MouseEvent e) {
                    Point endPoint = e.getPoint();
                    Color color = new Color(
                            Math.min(255, Math.max(0, Integer.parseInt(rField.getText()))),
                            Math.min(255, Math.max(0, Integer.parseInt(gField.getText()))),
                            Math.min(255, Math.max(0, Integer.parseInt(bField.getText())))
                    );

                    if (lineButton.isSelected()) {
                        shapes.add(new Line(startPoint, endPoint, color));
                    } else if (rectButton.isSelected()) {
                        shapes.add(new RectangleShape(startPoint, endPoint, color));
                    } else if (circleButton.isSelected()) {
                        shapes.add(new Circle(startPoint, endPoint, color));
                    }

                    repaint();
                }
            });
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Shape shape : shapes) {
                shape.draw(g);
            }
        }
    }

    private abstract class Shape {
        protected Point start, end;
        protected Color color;

        public Shape(Point start, Point end, Color color) {
            this.start = start;
            this.end = end;
            this.color = color;
        }

        public abstract void draw(Graphics g);
    }

    private class Line extends Shape {
        public Line(Point start, Point end, Color color) {
            super(start, end, color);
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.drawLine(start.x, start.y, end.x, end.y);
        }
    }

    private class RectangleShape extends Shape {
        public RectangleShape(Point start, Point end, Color color) {
            super(start, end, color);
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.drawRect(Math.min(start.x, end.x), Math.min(start.y, end.y),
                    Math.abs(start.x - end.x), Math.abs(start.y - end.y));
        }
    }

    private class Circle extends Shape {
        public Circle(Point start, Point end, Color color) {
            super(start, end, color);
        }

        public void draw(Graphics g) {
            g.setColor(color);
            int radius = (int) start.distance(end);
            g.drawOval(start.x - radius, start.y - radius, radius * 2, radius * 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Interface::new);
    }
}
