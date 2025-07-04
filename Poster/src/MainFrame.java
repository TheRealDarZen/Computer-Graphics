import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private PosterCanvas posterCanvas;

    public MainFrame() {
        super("Poster");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        ImagePanel imagePanel = new ImagePanel();
        ShapePanel shapePanel = new ShapePanel();
        posterCanvas = new PosterCanvas();
        ControlPanel controlPanel = new ControlPanel(posterCanvas);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(imagePanel, BorderLayout.CENTER);
        leftPanel.add(shapePanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(posterCanvas, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }
}
