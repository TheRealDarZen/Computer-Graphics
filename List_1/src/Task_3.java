import java.awt.*;
import java.awt.image.BufferedImage;

public class Task_3 {

    public static BufferedImage emptyImage(int width, int height){
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    public static void main(String[] args) {
        ImageSaver imageSaver = new ImageSaver();

        int width = 1000, height = 1000;

        BufferedImage image = emptyImage(width, height);

        // Pattern 1

        int circleSize = 200;
        int spacing = 300;
        Color circleColor = Color.BLACK;
        Color bgColor = Color.LIGHT_GRAY;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int row = y / spacing;
                int col = x / spacing;

                int centerX = col * spacing + spacing / 2;
                int centerY = row * spacing + spacing / 2;

                boolean isCircle = Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(circleSize / 2, 2);

                Color color = isCircle ? circleColor : bgColor;
                image.setRGB(x, y, color.getRGB());
            }
        }

        imageSaver.saveImage(image, "task_3a");
        image = emptyImage(width, height);

        // Pattern 2

        Color diamondColor = Color.BLACK;
        int diamondSize = 50;
        bgColor = Color.WHITE;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int row = y / diamondSize;
                int col = x / diamondSize;

                int centerX = col * diamondSize + diamondSize / 2;
                int centerY = row * diamondSize + diamondSize / 2;

                boolean isDiamond = Math.abs(x - centerX) + Math.abs(y - centerY) <= diamondSize / 2;

                Color color = isDiamond ? diamondColor : bgColor;
                image.setRGB(x, y, color.getRGB());
            }
        }

        imageSaver.saveImage(image, "task_3b");
        image = emptyImage(width, height);

        // Pattern 3

        int centerX = width / 2;
        int centerY = height / 2;
        int gapSize = 5;
        circleColor = Color.BLACK;
        bgColor = Color.WHITE;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                double sizeCoeff = Math.pow(distance / 100, 1.2);
                int ringNumber = (int) (distance / (gapSize + sizeCoeff));

                boolean isCircle = ringNumber % 2 == 0;
                Color color = isCircle ? circleColor : bgColor;

                image.setRGB(x, y, color.getRGB());
            }
        }

        imageSaver.saveImage(image, "task_3c");
        image = emptyImage(width, height);

        // Pattern 4

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

            }
        }

        imageSaver.saveImage(image, "task_3d");
        image = emptyImage(width, height);

        // Pattern 5

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

            }
        }

        imageSaver.saveImage(image, "task_3e");
        image = emptyImage(width, height);

    }
}
