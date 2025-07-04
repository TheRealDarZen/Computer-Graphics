import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public interface PosterElement {

    void draw(Graphics2D g2, boolean selected);

    boolean contains(Point2D point);

    DragHandle getHandleAtPoint(Point2D point);

    void translate(double dx, double dy);

    void scaleAndRotate(Point2D from, Point2D to);

    AffineTransform getTransform();

    void setTransform(AffineTransform transform);
}

