import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Klasa reprezentuje kształt (prostokąt lub koło) na plakacie.
 */
public class ShapeElement implements PosterElement {
    private ShapeType type;
    private Color color;
    private int size = 100; // Domyślny rozmiar
    private AffineTransform transform;

    public ShapeElement(ShapeType type, Color color, Point location) {
        this.type = type;
        this.color = color;
        this.transform = AffineTransform.getTranslateInstance(location.x, location.y);
    }

    @Override
    public void draw(Graphics2D g2, boolean selected) {
        AffineTransform old = g2.getTransform();
        g2.transform(transform);

        Shape shape;
        // W zależności od typu rysujemy koło lub kwadrat
        if (type == ShapeType.CIRCLE) {
            shape = new Ellipse2D.Double(0, 0, size, size);
        } else { // RECTANGLE
            shape = new Rectangle2D.Double(0, 0, size, size);
        }
        g2.setColor(color);
        g2.fill(shape);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.draw(shape);

        if (selected) {
            // Obrys zaznaczonego elementu – np. czerwona ramka
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            g2.draw(shape);
        }

        g2.setTransform(old);
    }

    @Override
    public boolean contains(Point2D point) {
        try {
            AffineTransform inverse = transform.createInverse();
            Point2D localPoint = inverse.transform(point, null);
            Shape shape;
            if (type == ShapeType.CIRCLE) {
                shape = new Ellipse2D.Double(0, 0, size, size);
            } else {
                shape = new Rectangle2D.Double(0, 0, size, size);
            }
            return shape.contains(localPoint);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public DragHandle getHandleAtPoint(Point2D point) {
        try {
            AffineTransform inverse = transform.createInverse();
            Point2D localPoint = inverse.transform(point, null);
            Rectangle2D bounds = new Rectangle2D.Double(0, 0, size, size);
            if (bounds.contains(localPoint)) {
                double cornerThreshold = 10;
                if (Math.abs(localPoint.getX()) < cornerThreshold ||
                        Math.abs(localPoint.getX() - size) < cornerThreshold ||
                        Math.abs(localPoint.getY()) < cornerThreshold ||
                        Math.abs(localPoint.getY() - size) < cornerThreshold) {
                    return DragHandle.CORNER;
                } else {
                    return DragHandle.CENTER;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void translate(double dx, double dy) {
        transform.translate(dx, dy);
    }

    @Override
    public void scaleAndRotate(Point2D from, Point2D to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double angle = Math.atan2(dy, dx);
        double scale = 1.0 + Math.hypot(dx, dy) / 100.0;
        transform.rotate(angle);
        transform.scale(scale, scale);
    }

    @Override
    public AffineTransform getTransform() {
        return transform;
    }

    @Override
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }
}

