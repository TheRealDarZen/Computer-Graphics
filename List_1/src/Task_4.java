import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Task_4 {
    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage emptyImage(int width, int height){
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    public static void main(String[] args) {
        ImageSaver imageSaver = new ImageSaver();

        BufferedImage upload1, upload2;
        upload1 = loadImage("images/upload_4_1.png");
        upload2 = loadImage("images/upload_4_2.png");

        int width = upload1.getWidth();
        int height = upload1.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int centerX = width / 2;
        int centerY = height / 2;

        // Rings

        int ring_width = 20;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double d = Math.sqrt((double) (y - centerY) * (y - centerY) + (double) (x - centerX) * (x - centerX));
                int det = ((int) d) / ring_width;
                boolean isCircle = (det % 2 == 0);
                int color = isCircle ? upload2.getRGB(x, y) :upload1.getRGB(x, y);

                image.setRGB(x, y, color);
            }
        }

        imageSaver.saveImage(image, "task_4a");
        image = emptyImage(width, height);

        // Grid

        int gridSize = 50;
        int lineWidth = 15;
        int offset = 10;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean isGridLine = ((x - offset + gridSize) % gridSize < lineWidth) || ((y - offset + gridSize) % gridSize < lineWidth);
                int color = isGridLine ? upload2.getRGB(x, y) : upload1.getRGB(x, y);

                image.setRGB(x, y, color);
            }
        }

        imageSaver.saveImage(image, "task_4b");
        image = emptyImage(width, height);

    }
}
