import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {

    private static final String IMAGE_DIR = "images/";

    public ImagePanel() {
        setLayout(new WrapLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(200, 400));
        setBackground(Color.LIGHT_GRAY);
        loadImages();
    }

    private void loadImages() {
        File folder = new File(IMAGE_DIR);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Folder z obrazami nie istnieje: " + IMAGE_DIR);
            return;
        }

        File[] files = folder.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
        });

        if (files == null) return;

        for (File file : files) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img == null) continue;

                ImageIcon icon = new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
                JLabel label = new JLabel(icon);
                label.setTransferHandler(new ImageTransferHandler(img));

                label.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        JComponent comp = (JComponent) e.getSource();
                        TransferHandler handler = comp.getTransferHandler();
                        handler.exportAsDrag(comp, e, TransferHandler.COPY);
                    }
                });

                add(label);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Obsługuje przeciąganie obrazów
    private static class ImageTransferHandler extends TransferHandler {
        private final BufferedImage image;

        public ImageTransferHandler(BufferedImage image) {
            this.image = image;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new ImageSelection(image);
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }

    // Klasa pomocnicza do "przenoszenia" obrazu jako Transferable
    private static class ImageSelection implements Transferable {
        private final BufferedImage image;
        private static final DataFlavor[] FLAVORS = { DataFlavor.imageFlavor };

        public ImageSelection(BufferedImage image) {
            this.image = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return FLAVORS;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
            return image;
        }
    }
}

