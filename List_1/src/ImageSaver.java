import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;

public class ImageSaver {
    public void saveImage(BufferedImage image, String fileName) {
        try {
            File output = new File("images\\" + fileName + ".png");
            ImageIO.write(image, "png", output);
            System.out.println("Image saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save the image.");
        }
    }
}
