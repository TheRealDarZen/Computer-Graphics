import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SolarSystem extends JPanel implements ActionListener {
    private final int centerX = 500;
    private final int centerY = 500;
    private final int[] orbitRadii = {6, 11, 15, 23, 78, 143, 288, 452};
    private final double[] orbitalSpeeds = {0.04, 0.016, 0.01, 0.0053, 0.00084, 0.00034, 0.00012, 0.000061};
    private final double[] angles = new double[8];
    private double moonAngle = 0;
    private long lastUpdateTime = System.nanoTime();
    private int fps = 240;

    private Timer timer;

    public SolarSystem() {
        timer = new Timer(1000/fps, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Prepare the canvas
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw orbits
        g.setColor(Color.WHITE);
        for (int radius : orbitRadii) {
            g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        }

        // Draw the Sun
        g.setColor(Color.YELLOW);
        g.fillOval(centerX - 5, centerY - 5, 10, 10);

        // Draw the planets
        Color[] planetColors = {
                Color.GRAY,  // Mercury
                Color.ORANGE, // Venus
                Color.BLUE,  // Earth
                Color.RED,   // Mars
                Color.CYAN,  // Jupiter
                Color.LIGHT_GRAY, // Saturn
                Color.GREEN, // Uran
                Color.MAGENTA  // Neptune
        };

        for (int i = 0; i < 8; i++) {
            int planetX = (int) (centerX + orbitRadii[i] * Math.cos(angles[i]));
            int planetY = (int) (centerY + orbitRadii[i] * Math.sin(angles[i]));
            g.setColor(planetColors[i]);
            g.fillOval(planetX - 8, planetY - 8, 16, 16);

            // Draw the Moon
            if (i == 2) {
                int moonX = (int) (planetX + 15 * Math.cos(moonAngle));
                int moonY = (int) (planetY + 15 * Math.sin(moonAngle));
                g.setColor(Color.LIGHT_GRAY);
                g.fillOval(moonX - 4, moonY - 4, 8, 8);
            }
        }

        // Draw the years counter
        g.drawString("Years elapsed: " + (int) (angles[2] / (2 * Math.PI)), 20, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0; // Seconds
        lastUpdateTime = currentTime;

        // Update planets' angles
        for (int i = 0; i < 8; i++) {
            angles[i] += orbitalSpeeds[i] * deltaTime * 60;
        }

        // Update moons' angle
        moonAngle += 0.135 * deltaTime * 60;

        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Solar System");
        SolarSystem panel = new SolarSystem();
        frame.add(panel);
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
