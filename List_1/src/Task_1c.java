import java.awt.image.*;
import java.awt.Color;

public class Task_1c {
    public static void main(String[] args) {
        ImageSaver imageSaver = new ImageSaver();

        int width = 1000, height = 1000;
        int squareSize = args.length > 0 ? Integer.parseInt(args[0]) : 100;
        Color color1 = args.length > 1 ? Color.decode(args[1]) : Color.WHITE;
        Color color2 = args.length > 2 ? Color.decode(args[2]) : Color.BLACK;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean isColor1 = ((x / squareSize) % 2 == (y / squareSize) % 2);
                Color color = isColor1 ? color1 : color2;
                image.setRGB(x, y, color.getRGB());
            }
        }

        imageSaver.saveImage(image, "task_1c");
    }
}
