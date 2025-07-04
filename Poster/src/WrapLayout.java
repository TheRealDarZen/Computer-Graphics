import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;


public class WrapLayout extends FlowLayout {

    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getWidth();
            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            Insets insets = target.getInsets();
            int maxWidth = targetWidth - (insets.left + insets.right + getHgap() * 2);
            int x = 0, y = insets.top + getVgap();
            int rowHeight = 0;

            Dimension dim = new Dimension(0, 0);
            for (Component m : target.getComponents()) {
                if (!m.isVisible()) continue;

                Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                if (x == 0 || (x + d.width) <= maxWidth) {
                    if (x > 0) x += getHgap();
                    x += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                } else {
                    x = d.width;
                    y += getVgap() + rowHeight;
                    rowHeight = d.height;
                }

                dim.width = Math.max(dim.width, x);
                dim.height = y + rowHeight;
            }

            dim.width += insets.left + insets.right;
            dim.height += insets.bottom + getVgap();

            return dim;
        }
    }
}

