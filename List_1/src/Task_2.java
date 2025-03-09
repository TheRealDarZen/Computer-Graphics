import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Task_2 {

    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ImageSaver imageSaver = new ImageSaver();

        BufferedImage image;

        image = loadImage("images/upload.png");

        int width = image.getWidth();
        int height = image.getHeight();

        int centerX = width / 2;
        int centerY = height / 2;

        // Concentric rings (2a)

        int ring_width = 20;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double d = Math.sqrt((double) (y - centerY) * (y - centerY) + (double) (x - centerX) * (x - centerX));
                int det = ((int) d) / ring_width;
                if ((det % 2) == 0) {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        imageSaver.saveImage(image, "task_2a");

        // Grid (2b)

        image = loadImage("images/upload.png");

        int gridSize = 50;
        int lineWidth = 15;
        int offset = 10;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean isGridLine = ((x - offset + gridSize) % gridSize < lineWidth) || ((y - offset + gridSize) % gridSize < lineWidth);
                if (isGridLine) {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        imageSaver.saveImage(image, "task_2b");

        // Chessboard (2c)

        image = loadImage("images/upload.png");

        int squareSize = 50;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean isColor1 = ((x / squareSize) % 2 == (y / squareSize) % 2);
                if (isColor1) {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        imageSaver.saveImage(image, "task_2c");
    }
}
