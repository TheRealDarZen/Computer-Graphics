import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;

class ImageElement extends PosterElement {
    private static final long serialVersionUID = 1L;

    private transient BufferedImage image;
    private byte[] imageData;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private double originalWidth;
    private double originalHeight;

    public ImageElement(BufferedImage image, double x, double y, double rotation) {
        super(image.getWidth(), image.getHeight(), x, y, rotation);
        this.image = image;
        this.originalWidth = image.getWidth();
        this.originalHeight = image.getHeight();

        // Save image data for serialization
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            imageData = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2d) {

        if (image == null && imageData != null) {
            try {
                image = ImageIO.read(new ByteArrayInputStream(imageData));
                originalWidth = image.getWidth();
                originalHeight = image.getHeight();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        AffineTransform oldTransform = g2d.getTransform();

        g2d.transform(transform);

        g2d.drawImage(image, 0, 0, (int) width, (int) height, null);

        g2d.setTransform(oldTransform);
    }

    @Override
    public void setSize(double width, double height) {
        super.setSize(width, height);
        this.scaleX = width / originalWidth;
        this.scaleY = height / originalHeight;
    }

    @Override
    public boolean contains(Point2D point) {
        try {

            AffineTransform inverse = transform.createInverse();
            Point2D transformedPoint = inverse.transform(point, null);

            // Check if a point is inside an image
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

        // Read
        if (imageData != null) {
            try {
                image = ImageIO.read(new ByteArrayInputStream(imageData));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
