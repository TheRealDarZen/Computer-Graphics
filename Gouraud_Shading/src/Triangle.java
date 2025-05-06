import java.awt.*;
import java.awt.image.BufferedImage;

public class Triangle {

    private int[] xPts;
    private int[] yPts;

    private Color[] colors;

    public Triangle(int x1, int y1, int x2, int y2, int x3, int y3,
                    Color c1, Color c2, Color c3) {
        this.xPts = new int[]{x1, x2, x3};
        this.yPts = new int[]{y1, y2, y3};
        this.colors = new Color[]{c1, c2, c3};
    }
    
    public void renderGouraud(BufferedImage image) {

        // Square bounding a triangle (opposite points)
        int sqMinX = Math.min(Math.min(xPts[0], xPts[1]), xPts[2]);
        int sqMaxX = Math.max(Math.max(xPts[0], xPts[1]), xPts[2]);
        int sqMinY = Math.min(Math.min(yPts[0], yPts[1]), yPts[2]);
        int sqMaxY = Math.max(Math.max(yPts[0], yPts[1]), yPts[2]);

        // Avoid getting out of images' bounds
        sqMinX = Math.max(sqMinX, 0);
        sqMinY = Math.max(sqMinY, 0);
        sqMaxX = Math.min(sqMaxX, image.getWidth() - 1);
        sqMaxY = Math.min(sqMaxY, image.getHeight() - 1);

        // For each pixel in bounding square
        for (int y = sqMinY; y <= sqMaxY; y++) {
            for (int x = sqMinX; x <= sqMaxX; x++) {
                // If inside a triangle
                if (isInside(x, y)) {
                    // Find barycentric coordinates for interpolation
                    double[] baryCoords = findBarycentricCoordinates(x, y);
//                    Note to myself about barycentric coordinates (a, b, c):
//                        For each point (px, py) inside a triangle:
//                            a + b + c = 1
//                            a, b, c >= 0
//                            px = a * x1 + b * x2 + c * x3
//                            py = a * y1 + b * y2 + c * y
//                        Finding barycentric coordinates:
//                            P - point; A, B, C - triangle points
//                            a = Area(PBC) / Area(ABC)
//                            b = Area(APC) / Area(ABC)
//                            c = Area(ABP) / Area(ABC)
//                        We "link" a to A, b to B, c to C:
//                            P = A -> (a, b, c) = (1, 0, 0)
//                            P = B -> (a, b, c) = (0, 1, 0)
//                            P = C -> (a, b, c) = (0, 0, 1)

                    // Interpolate pixel color
                    Color interpolatedColor = interpolateColor(baryCoords);

                    // Set pixel color
                    image.setRGB(x, y, interpolatedColor.getRGB());
                }
            }
        }
    }

    public void renderGouraud(Graphics g) {

        // Square bounding a triangle (opposite points)
        int minX = Math.min(Math.min(xPts[0], xPts[1]), xPts[2]);
        int maxX = Math.max(Math.max(xPts[0], xPts[1]), xPts[2]);
        int minY = Math.min(Math.min(yPts[0], yPts[1]), yPts[2]);
        int maxY = Math.max(Math.max(yPts[0], yPts[1]), yPts[2]);

        // For each pixel in bounding square
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                // If inside a triangle
                if (isInside(x, y)) {
                    // Find barycentric coordinates for interpolation
                    double[] baryCoords = findBarycentricCoordinates(x, y);

                    // Interpolate pixel color
                    Color interpolatedColor = interpolateColor(baryCoords);

                    // Set graphics color and draw a pixel
                    g.setColor(interpolatedColor);
                    g.fillRect(x, y, 1, 1);
                }
            }
        }
    }

    private boolean isInside(int x, int y) {
        double[] baryCoords = findBarycentricCoordinates(x, y);
        return baryCoords[0] >= 0 && baryCoords[1] >= 0 && baryCoords[2] >= 0
                && (baryCoords[0] + baryCoords[1] + baryCoords[2] - 1) < 0.001;
    }

    private double[] findBarycentricCoordinates(int x, int y) {
        int x1 = xPts[0], y1 = yPts[0];
        int x2 = xPts[1], y2 = yPts[1];
        int x3 = xPts[2], y3 = yPts[2];

        double areaTotal = Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0);

        // Finding areas using matrix' determinant
        double areaA = Math.abs((x * (y2 - y3) + x2 * (y3 - y) + x3 * (y - y2)) / 2.0);
        double areaB = Math.abs((x1 * (y - y3) + x * (y3 - y1) + x3 * (y1 - y)) / 2.0);
        double areaC = Math.abs((x1 * (y2 - y) + x2 * (y - y1) + x * (y1 - y2)) / 2.0);

        double[] baryCoords = new double[3];
        if (areaTotal == 0) {
            baryCoords[0] = baryCoords[1] = baryCoords[2] = 0;
        } else {
            baryCoords[0] = areaA / areaTotal;
            baryCoords[1] = areaB / areaTotal;
            baryCoords[2] = areaC / areaTotal;
        }

        return baryCoords;
    }

    private Color interpolateColor(double[] baryCoords) {

        int[] r = new int[3];
        int[] g = new int[3];
        int[] b = new int[3];

        for (int i = 0; i < 3; i++) {
            r[i] = colors[i].getRed();
            g[i] = colors[i].getGreen();
            b[i] = colors[i].getBlue();
        }

        // Linear interpolation
        int interpR = (int)(r[0] * baryCoords[0] + r[1] * baryCoords[1] + r[2] * baryCoords[2]);
        int interpG = (int)(g[0] * baryCoords[0] + g[1] * baryCoords[1] + g[2] * baryCoords[2]);
        int interpB = (int)(b[0] * baryCoords[0] + b[1] * baryCoords[1] + b[2] * baryCoords[2]);

        interpR = Math.min(255, Math.max(0, interpR));
        interpG = Math.min(255, Math.max(0, interpG));
        interpB = Math.min(255, Math.max(0, interpB));

        return new Color(interpR, interpG, interpB);
    }

    public int[] getXPts() {
        return xPts;
    }

    public int[] getYPts() {
        return yPts;
    }

}