import java.awt.image.*;
import java.awt.Color;

public class Task_2 {
    public static void main(String[] args) {
        ImageSaver imageSaver = new ImageSaver();

        int width = 1000, height = 1000;
        int gridSize = args.length > 0 ? Integer.parseInt(args[0]) : 50;
        int lineWidth = args.length > 1 ? Integer.parseInt(args[1]) : 15;
        Color gridColor = args.length > 2 ? Color.decode(args[2]) : Color.BLACK;
        Color bgColor = args.length > 3 ? Color.decode(args[3]) : Color.WHITE;

        int offset = 10;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean isGridLine = ((x - offset + gridSize) % gridSize < lineWidth) || ((y - offset + gridSize) % gridSize < lineWidth);
                Color color = isGridLine ? gridColor : bgColor;
                image.setRGB(x, y, color.getRGB());
            }
        }

        imageSaver.saveImage(image, "task_2");
    }
}
