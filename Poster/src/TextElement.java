import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Klasa reprezentuje tekst umieszczony na plakacie.
 */
public class TextElement implements PosterElement {
    private String text;
    private Font font = new Font("Serif", Font.PLAIN, 24);
    private AffineTransform transform;

    public TextElement(String text, Point location) {
        this.text = text;
        // Początkowa transformacja – przesunięcie tekstu do miejsca dropu
        this.transform = AffineTransform.getTranslateInstance(location.x, location.y);
    }

    @Override
    public void draw(Graphics2D g2, boolean selected) {
        AffineTransform old = g2.getTransform();
        g2.transform(transform);

        g2.setFont(font);
        g2.setColor(Color.BLACK);
        // Rysujemy tekst – domyślnie pozycjonowany w punkcie (0,0)
        g2.drawString(text, 0, 0);

        if (selected) {
            // Wyznaczamy obszar tekstu (bounding box) na potrzeby zaznaczenia
            FontMetrics metrics = g2.getFontMetrics(font);
            int width = metrics.stringWidth(text);
            int height = metrics.getHeight();
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(1));
            // Prostokąt otaczający tekst; uwzględniamy, że baseline tekstu nie jest górną krawędzią
            g2.drawRect(0, -metrics.getAscent(), width, height);
        }

        g2.setTransform(old);
    }

    @Override
    public boolean contains(Point2D point) {
        try {
            AffineTransform inverse = transform.createInverse();
            Point2D localPoint = inverse.transform(point, null);
            FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
            int width = metrics.stringWidth(text);
            int height = metrics.getHeight();
            // Ustalamy bounding box tekstu – przyjmujemy, że tekst jest rysowany od (0,0) z baseline na wysokości ascenzu
            Rectangle2D bounds = new Rectangle2D.Double(0, -metrics.getAscent(), width, height);
            return bounds.contains(localPoint);
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
            FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
            int width = metrics.stringWidth(text);
            int height = metrics.getHeight();
            Rectangle bounds = new Rectangle(0, -metrics.getAscent(), width, height);
            if (bounds.contains(localPoint)) {
                double cornerThreshold = 10;
                if (Math.abs(localPoint.getX()) < cornerThreshold ||
                        Math.abs(localPoint.getX() - width) < cornerThreshold ||
                        Math.abs(localPoint.getY() + metrics.getAscent()) < cornerThreshold ||
                        Math.abs(localPoint.getY() - (height - metrics.getAscent())) < cornerThreshold) {
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

