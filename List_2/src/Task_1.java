import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Task_1 {
    public static void main(String[] args) {
        double period = 2.0;
        double amplitude = 30.0;

        // Taking arguments from command line (if available)
        if (args.length >= 2) {
            try {
                period = Double.parseDouble(args[0]);
                amplitude = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Incorrect parameters. Usage: java Clock <period> <amplitud>");
                System.exit(1);
            }
        }

        ClockWindow wnd = new ClockWindow(period, amplitude);
        wnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wnd.setBounds(70, 70, 400, 500);
        wnd.setVisible(true);
    }
}

class ClockPane extends JPanel implements ActionListener {
    final int TICK_LEN = 10;
    int center_x, center_y;
    int r_outer, r_inner;
    GregorianCalendar calendar;
    Timer timer;
    double pendulumAngle = 0;
    double period;
    double amplitude;
    long startTime;

    ClockPane(double period, double amplitude) {
        super();
        setBackground(new Color(200, 200, 255));
        calendar = new GregorianCalendar();
        this.period = period;
        this.amplitude = amplitude;
        startTime = System.currentTimeMillis();
        timer = new Timer(16, this);
        timer.start();
    }

    public void DrawTickMark(double angle, Graphics g) {
        int xw, yw, xz, yz;
        angle = Math.PI * angle / 180.0;
        xw = (int) (center_x + r_inner * Math.sin(angle));
        yw = (int) (center_y - r_inner * Math.cos(angle));
        xz = (int) (center_x + r_outer * Math.sin(angle));
        yz = (int) (center_y - r_outer * Math.cos(angle));
        g.drawLine(xw, yw, xz, yz);
    }

    public void DrawHand(double angle, int length, Graphics g) {
        int xw, yw;
        angle = Math.PI * angle / 180.0;
        xw = (int) (center_x + length * Math.sin(angle));
        yw = (int) (center_y - length * Math.cos(angle));
        g.drawLine(center_x, center_y, xw, yw);
    }

    public void DrawDial(Graphics g) {
        g.drawOval(center_x - r_outer, center_y - r_outer, 2 * r_outer, 2 * r_outer);
        for (int i = 0; i <= 11; i++)
            DrawTickMark(i * 30.0, g);
    }

    public void DrawPendulum(Graphics g) {
        int pivotX = center_x;
        int pivotY = center_y + r_outer;
        int length = 2 * r_outer;

        int bobX = (int) (pivotX + length * Math.sin(Math.toRadians(pendulumAngle)));
        int bobY = (int) (pivotY + length * Math.cos(Math.toRadians(pendulumAngle)));

        g.setColor(Color.BLACK);
        g.drawLine(pivotX, pivotY, bobX, bobY);
        g.fillOval(bobX - 15, bobY - 15, 30, 30);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Dimension size = getSize();
        center_x = size.width / 2;
        center_y = size.height / 3;
        r_outer = Math.min(size.width, size.height) / 4;
        r_inner = r_outer - TICK_LEN;
        Date time = new Date();
        calendar.setTime(time);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR) % 12;
        int second = calendar.get(Calendar.SECOND);
        DrawDial(g);
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(5));
        DrawHand(360.0 * (hour * 60 + minute) / (60.0 * 12), (int) (0.75 * r_inner), g);
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        DrawHand(360.0 * (minute * 60 + second) / 3600.0, (int) (0.97 * r_outer), g);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        DrawHand(second * 6.0, (int) (0.97 * r_inner), g);
        DrawPendulum(g);
    }

    public void actionPerformed(ActionEvent e) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        double timeInSeconds = elapsedTime / 1000.0;
        pendulumAngle = amplitude * Math.cos((2 * Math.PI / period) * timeInSeconds);
        repaint();
    }
}

class ClockWindow extends JFrame {
    public ClockWindow(double period, double amplitude) {
        setContentPane(new ClockPane(period, amplitude));
        setTitle("Clock with Pendulum");
    }
}

