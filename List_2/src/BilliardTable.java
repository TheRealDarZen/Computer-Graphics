import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class Ball {
    double x, y, vx, vy;
    static final double RADIUS = 3.5;
    static final double ELASTICITY = 0.9;
    static final double FRICTION = 0.9999;

    public Ball(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public void move(double speedMultiplier) {
        x += vx * speedMultiplier;
        y += vy * speedMultiplier;
        vx *= FRICTION;
        vy *= FRICTION;

        if (Math.abs(vx) < 0.01) vx = 0;
        if (Math.abs(vy) < 0.01) vy = 0;
    }

    public void checkWallCollision(int width, int height) {
        if (x - RADIUS < 0 || x + RADIUS > width) {
            vx = -vx;
            x = Math.max(RADIUS, Math.min(width - RADIUS, x));
        }
        if (y - RADIUS < 0 || y + RADIUS > height) {
            vy = -vy;
            y = Math.max(RADIUS, Math.min(height - RADIUS, y));
        }
    }
}

class BilliardTable extends JPanel implements ActionListener {
    private final int WIDTH = 600, HEIGHT = 400;
    private ArrayList<Ball> balls;
    private Timer timer;
    private int ballCollisionCounter = 0;

    public BilliardTable() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(34, 139, 34));
        balls = new ArrayList<>();
        initializeBalls();
        timer = new Timer(1, this);
        timer.start();
    }

    private void initializeBalls() {
        double startX = WIDTH / 1.5;
        double startY = HEIGHT / 2.0;
        int ballRows = 10;

        for (int row = 0; row < ballRows; row++) {
            for (int col = 0; col <= row; col++) {
                balls.add(new Ball(startX + row * Ball.RADIUS * 2,
                        startY + col * Ball.RADIUS * 2 - row * Ball.RADIUS,
                        0, 0));
            }
        }
        balls.add(new Ball(100, HEIGHT / 2.0, 4, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.drawString("Ball collisions: " + ballCollisionCounter, 20, 20);
        for (Ball ball : balls) {
            g.fillOval((int) (ball.x - Ball.RADIUS), (int) (ball.y - Ball.RADIUS), (int) (Ball.RADIUS * 2), (int) (Ball.RADIUS * 2));
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < balls.size(); i++) {
            Ball a = balls.get(i);
            a.checkWallCollision(getWidth() > 0 ? getWidth() : WIDTH, getHeight() > 0 ? getHeight() : HEIGHT);
            for (int j = i + 1; j < balls.size(); j++) {
                Ball b = balls.get(j);
                double dx = b.x - a.x;
                double dy = b.y - a.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                double minDist = Ball.RADIUS * 2;

                if (dist < minDist) {
                    ballCollisionCounter++;
                    double overlap = minDist - dist;
                    double nx = dx / dist;
                    double ny = dy / dist;

                    a.x -= nx * overlap / 2;
                    a.y -= ny * overlap / 2;
                    b.x += nx * overlap / 2;
                    b.y += ny * overlap / 2;


//                    if (a.x < b.x){
//                        a.x -= overlap / 2;
//                        b.x += overlap / 2;
//                    }
//                    else {
//                        a.x += overlap / 2;
//                        b.x -= overlap / 2;
//                    }
//
//                    if (a.y < b.y){
//                        a.y -= overlap / 2;
//                        b.y += overlap / 2;
//                    }
//                    else {
//                        a.y += overlap / 2;
//                        b.y -= overlap / 2;
//                    }

                    double dotProduct = (b.vx - a.vx) * nx + (b.vy - a.vy) * ny;
                    double impulse = (1 + Ball.ELASTICITY) * dotProduct / 2;

                    a.vx += impulse * nx;
                    a.vy += impulse * ny;
                    b.vx -= impulse * nx;
                    b.vy -= impulse * ny;
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Ball ball : balls) {
            ball.move(1.0);
        }
        checkCollisions();
        repaint();
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Billiard Simulation");
        BilliardTable table = new BilliardTable();
        frame.add(table);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
