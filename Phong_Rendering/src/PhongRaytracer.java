import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PhongRaytracer {

    private int width;
    private int height;
    private String outputFileName;
    private Sphere sphere;
    private List<Light> lights;
    private Color ambientLight;

    private static class Vector3 {
        double x, y, z;

        public Vector3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3 add(Vector3 v) {
            return new Vector3(x + v.x, y + v.y, z + v.z);
        }

        public Vector3 subtract(Vector3 v) {
            return new Vector3(x - v.x, y - v.y, z - v.z);
        }

        public Vector3 multiply(double scalar) {
            return new Vector3(x * scalar, y * scalar, z * scalar);
        }

        public double dot(Vector3 v) {
            return x * v.x + y * v.y + z * v.z;
        }

        public double length() {
            return Math.sqrt(x * x + y * y + z * z);
        }

        public Vector3 normalize() {
            double len = length();
            if (len > 0) {
                return new Vector3(x / len, y / len, z / len);
            }
            return new Vector3(0, 0, 0);
        }
    }

    private static class Light {
        Vector3 position;
        Color intensity;
        double c2;
        double c1;
        double c0;

        public Light(Vector3 position, Color intensity, double c2, double c1, double c0) {
            this.position = position;
            this.intensity = intensity;
            this.c2 = c2;
            this.c1 = c1;
            this.c0 = c0;
        }
    }

    private static class Sphere {
        Vector3 center;
        double radius;
        Color diffuse;     // Diffuse reflection coefficients (kdR, kdG, kdB)
        Color specular;    // Specular reflection coefficients (ksR, ksG, ksB)
        Color ambient;     // Ambient reflection coefficients (kaR, kaG, kaB)
        Color selfLuminance; // Self luminance (SR, SG, SB)
        double glossiness; // Glossiness coefficient g

        public Sphere(Vector3 center, double radius, Color diffuse, Color specular,
                      Color ambient, Color selfLuminance, double glossiness) {
            this.center = center;
            this.radius = radius;
            this.diffuse = diffuse;
            this.specular = specular;
            this.ambient = ambient;
            this.selfLuminance = selfLuminance;
            this.glossiness = glossiness;
        }

        // Check if a ray intersects with this sphere
        public double intersect(Vector3 rayOrigin, Vector3 rayDirection) {
            Vector3 oc = rayOrigin.subtract(center);
            double a = rayDirection.dot(rayDirection);
            double b = 2.0 * oc.dot(rayDirection);
            double c = oc.dot(oc) - radius * radius;
            double discriminant = b * b - 4 * a * c;

            if (discriminant < 0) {
                return -1.0; // No intersection
            } else {
                double t = (-b - Math.sqrt(discriminant)) / (2.0 * a);
                if (t > 0) {
                    return t; // Return the distance to intersection
                }
                return -1.0;
            }
        }

        // Calculate normal at a point on the sphere
        public Vector3 getNormalAt(Vector3 point) {
            return point.subtract(center).normalize();
        }
    }

    // Constructor
    public PhongRaytracer() {
        this.lights = new ArrayList<>();
    }

    // Calculate light attenuation based on distance
    private double calculateAttenuation(double distance, double c2, double c1, double c0) {
        double attenuation = 1.0 / (c2 * distance * distance + c1 * distance + c0);
        return Math.min(attenuation, 1.0);
    }

    // Load scene from text file
    public boolean loadScene(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            // Read image resolution
            line = reader.readLine().trim();
            String[] resolution = line.split("\\s+");
            width = Integer.parseInt(resolution[0]);
            height = Integer.parseInt(resolution[1]);

            // Read output filename
            outputFileName = reader.readLine().trim();

            // Read ambient light
            line = reader.readLine().trim();
            String[] ambientLightValues = line.split("\\s+");
            float ambientR = Float.parseFloat(ambientLightValues[0]);
            float ambientG = Float.parseFloat(ambientLightValues[1]);
            float ambientB = Float.parseFloat(ambientLightValues[2]);
            ambientLight = new Color(
                    Math.min(1.0f, ambientR),
                    Math.min(1.0f, ambientG),
                    Math.min(1.0f, ambientB)
            );

            // Read sphere data
            line = reader.readLine().trim();
            String[] sphereData = line.split("\\s+");

            Vector3 sphereCenter = new Vector3(
                    Double.parseDouble(sphereData[0]),
                    Double.parseDouble(sphereData[1]),
                    Double.parseDouble(sphereData[2])
            );
            double sphereRadius = Double.parseDouble(sphereData[3]);

            // Read sphere material properties
            // Diffuse coefficients
            line = reader.readLine().trim();
            String[] diffuseData = line.split("\\s+");
            float kdR = Float.parseFloat(diffuseData[0]);
            float kdG = Float.parseFloat(diffuseData[1]);
            float kdB = Float.parseFloat(diffuseData[2]);
            Color diffuse = new Color(
                    Math.min(1.0f, kdR),
                    Math.min(1.0f, kdG),
                    Math.min(1.0f, kdB)
            );

            // Specular coefficients
            line = reader.readLine().trim();
            String[] specularData = line.split("\\s+");
            float ksR = Float.parseFloat(specularData[0]);
            float ksG = Float.parseFloat(specularData[1]);
            float ksB = Float.parseFloat(specularData[2]);
            Color specular = new Color(
                    Math.min(1.0f, ksR),
                    Math.min(1.0f, ksG),
                    Math.min(1.0f, ksB)
            );

            // Ambient reflection coefficients
            line = reader.readLine().trim();
            String[] ambientReflData = line.split("\\s+");
            float kaR = Float.parseFloat(ambientReflData[0]);
            float kaG = Float.parseFloat(ambientReflData[1]);
            float kaB = Float.parseFloat(ambientReflData[2]);
            Color ambient = new Color(
                    Math.min(1.0f, kaR),
                    Math.min(1.0f, kaG),
                    Math.min(1.0f, kaB)
            );

            // Self luminance
            line = reader.readLine().trim();
            String[] selfLuminanceData = line.split("\\s+");
            float sR = Float.parseFloat(selfLuminanceData[0]);
            float sG = Float.parseFloat(selfLuminanceData[1]);
            float sB = Float.parseFloat(selfLuminanceData[2]);
            Color selfLuminance = new Color(
                    Math.min(1.0f, sR),
                    Math.min(1.0f, sG),
                    Math.min(1.0f, sB)
            );

            // Read glossiness
            line = reader.readLine().trim();
            double glossiness = Double.parseDouble(line);

            // Create sphere
            sphere = new Sphere(sphereCenter, sphereRadius, diffuse, specular, ambient, selfLuminance, glossiness);

            // Read attenuation coefficients
            line = reader.readLine().trim();
            String[] attenuationCoeffs = line.split("\\s+");
            double c2 = Double.parseDouble(attenuationCoeffs[0]);
            double c1 = Double.parseDouble(attenuationCoeffs[1]);
            double c0 = Double.parseDouble(attenuationCoeffs[2]);

            // Read number of lights
            line = reader.readLine().trim();
            int numLights = Integer.parseInt(line);

            // Read each light
            for (int i = 0; i < numLights; i++) {
                line = reader.readLine().trim();
                String[] lightData = line.split("\\s+");

                Vector3 lightPos = new Vector3(
                        Double.parseDouble(lightData[0]),
                        Double.parseDouble(lightData[1]),
                        Double.parseDouble(lightData[2])
                );

                Color lightIntensity = new Color(
                        Math.min(1.0f, Float.parseFloat(lightData[3])),
                        Math.min(1.0f, Float.parseFloat(lightData[4])),
                        Math.min(1.0f, Float.parseFloat(lightData[5]))
                );

                lights.add(new Light(lightPos, lightIntensity, c2, c1, c0));
            }

            return true;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading scene: " + e.getMessage());
            return false;
        }
    }

    public void render() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Camera is positioned at origin, facing along negative z-axis
        Vector3 cameraPosition = new Vector3(0, 0, 5);

        // Define view plane
        double viewPlaneDistance = 1.0;
        double viewPlaneWidth = 2.0;
        double viewPlaneHeight = 2.0 * height / width;

        // For each pixel in the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Convert pixel coordinates to view plane coordinates
                double viewX = (x / (double) width - 0.5) * viewPlaneWidth;
                double viewY = (0.5 - y / (double) height) * viewPlaneHeight;

                Vector3 rayDirection = new Vector3(0, 0, -1).normalize();
                Vector3 rayOrigin = new Vector3(viewX, viewY, cameraPosition.z - viewPlaneDistance);

                double t = sphere.intersect(rayOrigin, rayDirection);

                if (t > 0) {
                    // Calculate intersection point
                    Vector3 intersectionPoint = rayOrigin.add(rayDirection.multiply(t));

                    // Calculate surface normal at intersection point
                    Vector3 normal = sphere.getNormalAt(intersectionPoint);

                    // Calculate color at intersection point using Phong model
                    Color pixelColor = calculatePhongIllumination(intersectionPoint, normal, rayDirection);
                    image.setRGB(x, y, pixelColor.getRGB());
                } else {
                    // No intersection, set background color to black
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        // Save the rendered image
        try {
            File outputFile = new File(outputFileName);
            ImageIO.write(image, "PNG", outputFile);
            System.out.println("Image saved to " + outputFileName);

            // Optional: display image in a window
            displayImage(image);
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

    private Color calculatePhongIllumination(Vector3 point, Vector3 normal, Vector3 rayDirection) {
        // Initial color is the self-luminance of the surface
        float r = sphere.selfLuminance.getRed() / 255.0f;
        float g = sphere.selfLuminance.getGreen() / 255.0f;
        float b = sphere.selfLuminance.getBlue() / 255.0f;

        // Add ambient light contribution
        r += (ambientLight.getRed() / 255.0f) * (sphere.ambient.getRed() / 255.0f);
        g += (ambientLight.getGreen() / 255.0f) * (sphere.ambient.getGreen() / 255.0f);
        b += (ambientLight.getBlue() / 255.0f) * (sphere.ambient.getBlue() / 255.0f);

        // Observer direction is opposite to ray direction
        Vector3 observerDirection = rayDirection.multiply(-1).normalize();

        // Process each light source
        for (Light light : lights) {
            // Vector from intersection point to light
            Vector3 lightDirection = light.position.subtract(point).normalize();

            // Calculate distance to light for attenuation
            double distanceToLight = point.subtract(light.position).length();

            // Calculate light attenuation factor
            double attenuation = calculateAttenuation(distanceToLight, light.c2, light.c1, light.c0);

            // Diffuse reflection (Lambert's law)
            double diffuseFactor = Math.max(0, normal.dot(lightDirection));

            if (diffuseFactor > 0) {
                // Add diffuse component
                r += attenuation * diffuseFactor * (light.intensity.getRed() / 255.0f) * (sphere.diffuse.getRed() / 255.0f);
                g += attenuation * diffuseFactor * (light.intensity.getGreen() / 255.0f) * (sphere.diffuse.getGreen() / 255.0f);
                b += attenuation * diffuseFactor * (light.intensity.getBlue() / 255.0f) * (sphere.diffuse.getBlue() / 255.0f);

                // Specular reflection (Phong model)
                // Calculate reflection vector
                Vector3 reflectionVector = normal.multiply(2 * normal.dot(lightDirection)).subtract(lightDirection).normalize();

                // Calculate specular factor
                double specularFactor = Math.max(0, reflectionVector.dot(observerDirection));

                if (specularFactor > 0) {
                    // Apply glossiness (shininess)
                    specularFactor = Math.pow(specularFactor, sphere.glossiness);

                    // Add specular component
                    r += attenuation * specularFactor * (light.intensity.getRed() / 255.0f) * (sphere.specular.getRed() / 255.0f);
                    g += attenuation * specularFactor * (light.intensity.getGreen() / 255.0f) * (sphere.specular.getGreen() / 255.0f);
                    b += attenuation * specularFactor * (light.intensity.getBlue() / 255.0f) * (sphere.specular.getBlue() / 255.0f);
                }
            }
        }

        // Clamp color values to [0,1]
        r = Math.min(1.0f, Math.max(0.0f, r));
        g = Math.min(1.0f, Math.max(0.0f, g));
        b = Math.min(1.0f, Math.max(0.0f, b));

        return new Color(r, g, b);
    }

    // Display the rendered image in a window
    private void displayImage(BufferedImage image) {
        Frame frame = new Frame("Phong Raytracer Rendering");
        Canvas canvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, null);
            }
        };

        canvas.setSize(width, height);
        frame.add(canvas);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                frame.dispose();
            }
        });
    }

    // Main method
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java PhongRaytracer <scene-file>");
            System.exit(1);
        }

        PhongRaytracer raytracer = new PhongRaytracer();
        if (raytracer.loadScene(args[0])) {
            raytracer.render();
        } else {
            System.err.println("Failed to load scene from: " + args[0]);
        }
    }
}

