import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Task_2 {
    public static void main(String[] args) {
        ImageSaver imageSaver = new ImageSaver();

        BufferedImage image;
        try {
            image = ImageIO.read(new File("images\\upload.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int width = image.getWidth();
        int height = image.getHeight();

        int centerX = width / 2;
        int centerY = height / 2;

        int ring_width = 20;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double d = Math.sqrt((double) (y - centerY) * (y - centerY) + (double) (x - centerX) * (x - centerX));
                int det = ((int) d) / ring_width;
                if ((det % 2) == 0) {
                    image.setRGB(x, y, new Color(0, 0, 0).getRGB());
                }
            }
        }

        imageSaver.saveImage(image, "task_2a");


    }
}
