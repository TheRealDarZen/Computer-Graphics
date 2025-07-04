import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ImageElement implements PosterElement {
    private Image image;
    private int width, height;
    private AffineTransform transform;

    public ImageElement(Image image, Point location) {
        this.image = image;
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
        this.transform = AffineTransform.getTranslateInstance(location.x, location.y);
    }

    @Override
    public void draw(Graphics2D g2, boolean selected) {
        AffineTransform old = g2.getTransform();
        g2.transform(transform);
        g2.drawImage(image, 0, 0, null);

        if (selected) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(0, 0, width, height);

            // Rysujemy uchwyt skalujący
            g2.setColor(Color.BLUE);
            g2.fillRect(width - 5, height - 5, 10, 10);
        }

        g2.setTransform(old);
    }

    @Override
    public boolean contains(Point2D point) {
        try {
            AffineTransform inverse = transform.createInverse();
            Point2D local = inverse.transform(point, null);
            return new Rectangle2D.Double(0, 0, width, height).contains(local);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public DragHandle getHandleAtPoint(Point2D point) {
        try {
            AffineTransform inverse = transform.createInverse();
            Point2D local = inverse.transform(point, null);

            Rectangle2D bounds = new Rectangle2D.Double(0, 0, width, height);
            if (bounds.contains(local)) {
                // Uchwyty:
                double cornerX = width;
                double cornerY = height;
                Point2D handle = new Point2D.Double(cornerX, cornerY);
                if (handle.distance(local) < 10) {
                    return DragHandle.CORNER;
                }
                return DragHandle.CENTER;
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
        Point2D center = getCenter();

        double dx1 = from.getX() - center.getX();
        double dy1 = from.getY() - center.getY();
        double dx2 = to.getX() - center.getX();
        double dy2 = to.getY() - center.getY();

        double angle1 = Math.atan2(dy1, dx1);
        double angle2 = Math.atan2(dy2, dx2);
        double deltaAngle = angle2 - angle1;

        double dist1 = Math.hypot(dx1, dy1);
        double dist2 = Math.hypot(dx2, dy2);
        double scale = dist2 / dist1;

        transform.translate(center.getX(), center.getY());
        transform.rotate(deltaAngle);
        transform.scale(scale, scale);
        transform.translate(-center.getX(), -center.getY());
    }

    @Override
    public AffineTransform getTransform() {
        return transform;
    }

    @Override
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }

    private Point2D getCenter() {
        return transform.transform(new Point2D.Double(width / 2.0, height / 2.0), null);
    }
}
