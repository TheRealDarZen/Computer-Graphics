import java.awt.*;

public abstract class Shape {
    protected Point start, end;
    protected Color color;

    public Shape(Point start, Point end, Color color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public abstract void draw(Graphics g);
}

class Line extends Shape {
    public Line(Point start, Point end, Color color) {
        super(start, end, color);
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.drawLine(start.x, start.y, end.x, end.y);
    }
}

class Rectangle extends Shape {
    public Rectangle(Point start, Point end, Color color) {
        super(start, end, color);
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.drawRect(Math.min(start.x, end.x), Math.min(start.y, end.y),
                Math.abs(start.x - end.x), Math.abs(start.y - end.y));
    }
}

class Circle extends Shape {
    public Circle(Point start, Point end, Color color) {
        super(start, end, color);
    }

    public void draw(Graphics g) {
        g.setColor(color);
        int radius = (int) start.distance(end);
        g.drawOval(start.x - radius, start.y - radius, radius * 2, radius * 2);
    }
}
