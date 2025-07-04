import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ShapePanel extends JPanel {

    public ShapePanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(200, 120));
        setBackground(new Color(230, 230, 250)); // Jasny fiolet
        initShapes();
    }

    private void initShapes() {
        addShape("Kwadrat", ShapeType.RECTANGLE, Color.BLUE);
        addShape("Koło", ShapeType.CIRCLE, Color.RED);
        addTextLabel("Tekst");
    }

    private void addShape(String label, ShapeType type, Color color) {
        JLabel shapeLabel = new JLabel(label, SwingConstants.CENTER);
        shapeLabel.setPreferredSize(new Dimension(80, 40));
        shapeLabel.setOpaque(true);
        shapeLabel.setBackground(color);
        shapeLabel.setForeground(Color.WHITE);
        shapeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        shapeLabel.setTransferHandler(new ShapeTransferHandler(type, color));

        shapeLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, e, TransferHandler.COPY);
            }
        });

        add(shapeLabel);
    }

    private void addTextLabel(String text) {
        JLabel textLabel = new JLabel(text, SwingConstants.CENTER);
        textLabel.setPreferredSize(new Dimension(80, 40));
        textLabel.setOpaque(true);
        textLabel.setBackground(Color.DARK_GRAY);
        textLabel.setForeground(Color.WHITE);
        textLabel.setTransferHandler(new TextTransferHandler("Nowy tekst"));

        textLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, e, TransferHandler.COPY);
            }
        });

        add(textLabel);
    }

    // TransferHandler do przenoszenia kształtów
    private static class ShapeTransferHandler extends TransferHandler {
        private final ShapeType type;
        private final Color color;

        public ShapeTransferHandler(ShapeType type, Color color) {
            this.type = type;
            this.color = color;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new ShapeSelection(type, color);
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }

    // TransferHandler do przenoszenia tekstów
    private static class TextTransferHandler extends TransferHandler {
        private final String text;

        public TextTransferHandler(String text) {
            this.text = text;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new StringSelection(text);
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }

    // Klasa Transferable do kształtów
    private static class ShapeSelection implements Transferable {
        private static final DataFlavor SHAPE_FLAVOR = new DataFlavor(ShapeType.class, "ShapeType");
        private final ShapeType type;
        private final Color color;

        public ShapeSelection(ShapeType type, Color color) {
            this.type = type;
            this.color = color;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{SHAPE_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return SHAPE_FLAVOR.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
            return new Object[]{type, color};
        }
    }
}
