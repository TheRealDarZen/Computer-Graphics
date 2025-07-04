import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class TextElement extends PosterElement {
    private static final long serialVersionUID = 1L;

    private String text;
    private Font font;
    private Color color;
    private transient FontMetrics fontMetrics;
    private transient Rectangle2D textBounds;

    public TextElement(String text, Font font, Color color, double x, double y, double rotation) {
        super(10, 10, x, y, rotation);
        this.text = text;
        this.font = font;
        this.color = color;

    }

    @Override
    public void draw(Graphics2D g2d) {

        if (fontMetrics == null) {
            fontMetrics = g2d.getFontMetrics(font);
            textBounds = fontMetrics.getStringBounds(text, g2d);
            width = textBounds.getWidth();
            height = textBounds.getHeight();
        }

        AffineTransform oldTransform = g2d.getTransform();

        g2d.transform(transform);

        g2d.setFont(font);
        g2d.setColor(color);
        g2d.drawString(text, 0, (float) fontMetrics.getAscent());

        g2d.setTransform(oldTransform);
    }

    @Override
    public boolean contains(Point2D point) {
        try {
            AffineTransform inverse = transform.createInverse();
            Point2D transformedPoint = inverse.transform(point, null);

            return transformedPoint.getX() >= 0 && transformedPoint.getX() < width &&
                    transformedPoint.getY() >= 0 && transformedPoint.getY() < height;
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public void updateFontForScale(double scale) {
        // Font scaling
        float newSize = (float) (font.getSize() * scale);
        // Minimum font size
        newSize = Math.max(newSize, 8.0f);
        font = font.deriveFont(newSize);

        fontMetrics = null;
    }
}
