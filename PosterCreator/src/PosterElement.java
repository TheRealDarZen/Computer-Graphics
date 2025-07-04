import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

abstract class PosterElement implements Serializable {
    private static final long serialVersionUID = 1L;

    protected AffineTransform transform = new AffineTransform();
    protected double width;
    protected double height;
    protected double rotation = 0.0;

    public PosterElement(double width, double height, double x, double y, double rotation) {
        this.width = width;
        this.height = height;
        this.rotation = rotation;

        transform.translate(x, y);
        transform.rotate(rotation, width / 2, height / 2);
    }

    public abstract void draw(Graphics2D g2d);

    public abstract boolean contains(Point2D point);

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public Point2D getCenter() {
        Point2D center = new Point2D.Double(width / 2, height / 2);
        transform.transform(center, center);
        return center;
    }

    public Point2D getCorner(int index) {
        Point2D corner;
        switch (index) {
            case 0: // Top left
                corner = new Point2D.Double(0, 0);
                break;
            case 1: // Top right
                corner = new Point2D.Double(width, 0);
                break;
            case 2: // Bottom right
                corner = new Point2D.Double(width, height);
                break;
            case 3: // Bottom left
                corner = new Point2D.Double(0, height);
                break;
            default:
                return null;
        }

        transform.transform(corner, corner);
        return corner;
    }

    public Shape getTransformedOutline() {
        Rectangle2D outline = new Rectangle2D.Double(0, 0, width, height);
        return transform.createTransformedShape(outline);
    }

    public void scale(double scaleX, double scaleY) {
        width *= scaleX;
        height *= scaleY;

        AffineTransform newTransform = new AffineTransform(transform);
        newTransform.scale(scaleX, scaleY);
        transform = newTransform;
    }

    public void scaleUniform(double scale) {
        scale(scale, scale);
    }
}
