package lowlevel;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Random;

public class Utils {

    private static final String ERROR_STRING = "****ERROR**** ";
    private static final Random randomizer = new Random();

    public static void drawRect(Graphics g, Color color, int x, int y, int WIDTH, int HEIGHT) {
        g.setColor(color);
        g.drawRect(x, y, WIDTH, HEIGHT);
    }

    static void drawRect(Graphics g, int x, int y, int WIDTH, int HEIGHT) {
        drawRect(g, g.getColor(), x, y, WIDTH, HEIGHT);
    }

    static void fillRect(Graphics g, int x, int y, int WIDTH, int HEIGHT) {
         fillRect(g, g.getColor(), x, y, WIDTH, HEIGHT);
    }

    public static void fillRect(Graphics g, Color color, int x, int y, int WIDTH, int HEIGHT) {
        g.setColor(color);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }

    public static void drawRoundRect(Graphics g, Color color, int x, int y, int WIDTH, int HEIGHT) {
        g.setColor(color);
        g.drawRoundRect(x, y, WIDTH, HEIGHT,10,10);
    }

    static void drawRoundRect(Graphics g, int x, int y, int WIDTH, int HEIGHT) {
        drawRoundRect(g, g.getColor(), x, y, WIDTH, HEIGHT);
    }

    public static void fillRoundRect(Graphics g, Color color, int x, int y, int WIDTH, int HEIGHT) {
        g.setColor(color);
        g.fillRoundRect(x,y, WIDTH, HEIGHT,10,10);
    }

    static void fillRoundRect(Graphics g, int x, int y, int WIDTH, int HEIGHT) {
        fillRoundRect(g, g.getColor(), x, y, WIDTH, HEIGHT);
    }

    public static void drawTriangle(Graphics g, Color color, int x1, int y1, int x2, int y2, int x3, int y3) {
        g.setColor(color);
        Polygon poly = new Polygon();
        poly.addPoint(x1, y1);
        poly.addPoint(x2, y2);
        poly.addPoint(x3, y3);
        g.drawPolygon(poly);
    }

    public static void fillTriangle(Graphics g, Color color, int x1, int y1, int x2, int y2, int x3, int y3) {
        g.setColor(color);
        Polygon poly = new Polygon();
        poly.addPoint(x1, y1);
        poly.addPoint(x2, y2);
        poly.addPoint(x3, y3);
        g.fillPolygon(poly);
    }

    public static void drawCircle(Graphics g, Color color, int x, int y,int width, int height) {
        g.setColor(color);
        g.drawArc(x, y,width, height, 1, 360);
    }

    public static void fillCircle(Graphics g, Color color, int x, int y,int width, int height) {
        g.setColor(color);
        g.fillArc(x, y, width, height, 1, 360);
    }

    public static void fillCircle(Graphics g, int x, int y,int width, int height) {
        fillCircle(g, g.getColor(), x, y, width, height);
    }

    static Image loadImage(String path) {
        Image image = null;
        try {
            log("LOADIMAGE: Attempting to load: " + path);
            image = new javax.swing.ImageIcon(Utils.class.getResource(path)).getImage();
        } catch (Exception e) {
            _E("error loading image ("+path+") "+e.getMessage());
        }
        return image;
    }

    static void backGround(Graphics g, Color c, int WIDTH, int HEIGHT) {
        g.setColor(c);
        fillRect(g,0,0, WIDTH, HEIGHT);
    }

    static void drawImage(Graphics g, Image i, int x, int y, ImageObserver observer) {
        g.drawImage(i,x-(i.getWidth(observer)/2),y-(i.getHeight(observer)/2), observer);
    }

    public static void log(String s) {
        System.out.println(s);
    }

    static void _E(String s) {
        CustomCanvas.playErrorSound();
        log(ERROR_STRING+s);
    }

    // get random val between min and max, both inclusive
    public static int getRand(int min, int max) {
        int r = Math.abs(randomizer.nextInt());
        return (r % ((max - min + 1))) + min;
    }
}
