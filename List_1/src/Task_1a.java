import java.awt.image.*;
import java.awt.Color;

public class Task_1a {
    public static void main(String[] args) {
        ImageSaver imageSaver = new ImageSaver();

        int width = 1000, height = 1000;
        int w = 25;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int centerX = width / 2;
        int centerY = height / 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double d = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                int intensity = (int) ((Math.sin(d * Math.PI / w) + 1) * 128);
                Color color = new Color(
                        Math.max(0, Math.min(255, intensity)),
                        Math.max(0, Math.min(255, intensity)),
                        Math.max(0, Math.min(255, intensity))
                );
                image.setRGB(x, y, color.getRGB());
            }
        }

        imageSaver.saveImage(image, "task_1a");
    }
}
