import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class PosterCanvas extends JPanel {

    private final List<PosterElement> elements = new ArrayList<>();
    private PosterElement selectedElement = null;
    private Point dragStartPoint = null;
    private DragHandle activeHandle = null;

    public PosterCanvas() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        setupDnD();
        setupMouse();
        setFocusable(true);
    }

    // Konfiguracja "Drag & Drop"
    private void setupDnD() {
        new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);

                try {
                    Transferable transferable = dtde.getTransferable();
                    Point dropPoint = dtde.getLocation();

                    if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                        Image img = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                        elements.add(new ImageElement(img, dropPoint));
                    } else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        String text = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                        elements.add(new TextElement(text, dropPoint));
                    } else {
                        DataFlavor shapeFlavor = new DataFlavor(ShapeType.class, "ShapeType");
                        if (transferable.isDataFlavorSupported(shapeFlavor)) {
                            Object[] data = (Object[]) transferable.getTransferData(shapeFlavor);
                            ShapeType type = (ShapeType) data[0];
                            Color color = (Color) data[1];
                            elements.add(new ShapeElement(type, color, dropPoint));
                        }
                    }

                    repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, true);
    }

    private void setupMouse() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                dragStartPoint = e.getPoint();
                selectedElement = getElementAtPoint(dragStartPoint);

                if (SwingUtilities.isRightMouseButton(e) && selectedElement != null) {
                    // PPM = usuń
                    elements.remove(selectedElement);
                    selectedElement = null;
                    repaint();
                    return;
                }

                if (selectedElement != null) {
                    activeHandle = selectedElement.getHandleAtPoint(dragStartPoint);
                }

                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedElement == null || dragStartPoint == null) return;

                Point dragEnd = e.getPoint();
                double dx = dragEnd.getX() - dragStartPoint.getX();
                double dy = dragEnd.getY() - dragStartPoint.getY();

                if (activeHandle == DragHandle.CENTER) {
                    selectedElement.translate(dx, dy);
                } else if (activeHandle == DragHandle.CORNER) {
                    selectedElement.scaleAndRotate(dragStartPoint, dragEnd);
                }

                dragStartPoint = dragEnd;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragStartPoint = null;
                activeHandle = null;
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void addElement(PosterElement element) {
        elements.add(element);
        repaint();
    }

    public PosterElement getElementAtPoint(Point2D point) {
        ListIterator<PosterElement> it = elements.listIterator(elements.size());

        while (it.hasPrevious()) {
            PosterElement element = it.previous();

            try {
                AffineTransform inverse = element.getTransform().createInverse();
                Point2D localPoint = inverse.transform(point, null);

                if (element.contains(localPoint)) {
                    return element;
                }
            } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Lepsza jakość renderowania
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (PosterElement element : elements) {
            element.draw(g2, element == selectedElement);
        }

        g2.dispose();
    }

}

