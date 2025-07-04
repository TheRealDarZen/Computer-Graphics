import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.imageio.ImageIO;

public class ControlPanel extends JPanel {

    private PosterCanvas canvas;

    public ControlPanel(PosterCanvas canvas) {
        this.canvas = canvas;

        setLayout(new GridLayout(0, 1, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Dodaj element"));

        // Przyciski do dodawania elementów
        JButton imageButton = new JButton("Obraz");
        JButton rectButton = new JButton("Prostokąt");
        JButton circleButton = new JButton("Koło");
        JButton textButton = new JButton("Tekst");

        imageButton.addActionListener(this::addImage);
        rectButton.addActionListener(e -> addShape(ShapeType.RECTANGLE, Color.BLUE, new Point(100, 100)));
        circleButton.addActionListener(e -> addShape(ShapeType.CIRCLE, Color.GREEN, new Point(150, 150)));
        textButton.addActionListener(this::addText);

        add(imageButton);
        add(rectButton);
        add(circleButton);
        add(textButton);
    }

    private void addImage(ActionEvent e) {
        JFileChooser chooser = new JFileChooser("images/");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                Image img = ImageIO.read(file);
                if (img != null) {
                    canvas.addElement(new ImageElement(img, new Point(200, 200)));
                } else {
                    JOptionPane.showMessageDialog(this, "Nie można wczytać obrazu!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd podczas wczytywania obrazu:\n" + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addText(ActionEvent e) {
        String input = JOptionPane.showInputDialog(this, "Wprowadź tekst:");
        if (input != null && !input.trim().isEmpty()) {
            canvas.addElement(new TextElement(input.trim(), new Point(250, 250)));
        }
    }

    private void addShape(ShapeType type, Color color, Point position) {
        canvas.addElement(new ShapeElement(type, color, position));
    }

}

