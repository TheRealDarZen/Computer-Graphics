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
                boolean isUpload1 = (det % 2 == 1);
                int color = isUpload1 ? upload1.getRGB(x, y) :upload2.getRGB(x, y);

                image.setRGB(x, y, color);
            }
        }

        imageSaver.saveImage(image, "task_4a");
        image = emptyImage(width, height);



    }
}
