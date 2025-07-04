import java.awt.*;
import java.awt.geom.*;

class ShapeElement extends PosterElement {
    private static final long serialVersionUID = 1L;

    private Shape shape;
    private Color color;

    public ShapeElement(Shape shape, Color color, double x, double y, double rotation) {
        super(shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight(), x, y, rotation);
        this.shape = shape;
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g2d) {

        AffineTransform oldTransform = g2d.getTransform();

        g2d.transform(transform);

        g2d.setColor(color);
        g2d.fill(shape);

        g2d.setTransform(oldTransform);
    }

    @Override
    public boolean contains(Point2D point) {
        try {

            AffineTransform inverse = transform.createInverse();
            Point2D transformedPoint = inverse.transform(point, null);

            // Check if a point is inside a shape
            return shape.contains(transformedPoint);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateShapeForScale(double scaleX, double scaleY) {
        if (shape instanceof Rectangle2D) {
            shape = new Rectangle2D.Double(0, 0, width, height);
        } else if (shape instanceof Ellipse2D) {
            shape = new Ellipse2D.Double(0, 0, width, height);
        } else {
            AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
            shape = scaleTransform.createTransformedShape(shape);
        }
    }
}
