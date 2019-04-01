/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lowlevel;
import gamelogic.GameConfig;
import org.aeonbits.owner.ConfigFactory;

import java.awt.*;
import javax.swing.*;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.image.*;

public class Main {

    static JFrame frame;
    private static CustomCanvas canvas;
    private static final int WINDOWY_MINUS = 50;
    private static Bot bot;

    private static final int FRAME_DELAY_MILLIS = 50; //50;//20; // 20ms. implies 50fps (1000/20) = 50
    private static boolean gameThreadIsRunning = true;
    private static int windowX;
    private static int windowY;
    private static boolean isHidden;

     public static void main(String[] args) {
         log("Main called, Backgammon starting.");
         ConfigFactory.setProperty("configFileName", "backgammon.config");
         GameConfig config = ConfigFactory.create(GameConfig.class);
         frame = new JFrame();
         canvas = new CustomCanvas(config);
         bot = new Bot(canvas, frame);
         bot.start();
         frame.getContentPane().add(canvas);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
         frame.setSize(810, 500);
         frame.setIconImage(Utils.loadImage("/icon.gif"));
         frame.setResizable(false);
         Thread gameThread = new Thread(() -> {
            long cycleTime = System.currentTimeMillis();
            while(gameThreadIsRunning) {
                canvas.repaint();
                cycleTime = cycleTime + FRAME_DELAY_MILLIS;
                long difference = cycleTime - System.currentTimeMillis();
                try {
                    Thread.sleep(Math.max(0, difference));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
         });
         gameThread.setPriority(Thread.MIN_PRIORITY);
         gameThread.start();
         centralise(frame);
         frame.setVisible(true); // start AWT painting.
         frame.setTitle("Midokura Backgammon "+ CustomCanvas.VERSION);
         log("Backgammon visible.");
         windowX = frame.getLocationOnScreen().x;
         windowY = frame.getLocationOnScreen().y;
     }

     static void hideMousePointer(boolean hide) {
         if (hide && !isHidden) {
            isHidden = true;
            int[] pixels = new int[16 * 16];
            Image image = Toolkit.getDefaultToolkit().createImage( new MemoryImageSource(16, 16, pixels, 0, 16));
            Cursor transparentCursor =  Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
            frame.setCursor(transparentCursor);
         } else if (!hide && isHidden) {
             isHidden = false;
             frame.setCursor(null);
         }
     }

     private static void centralise(JFrame frame) {
         Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
         frame.setLocation(screenDim.width, (screenDim.height / 8) - WINDOWY_MINUS);
         frame.setLocation(screenDim.width / 2 - frame.getWidth() / 2,
             (screenDim.height / 2) - (frame.getHeight() / 2) - WINDOWY_MINUS);
     }

     private static void log(String s) {
         Utils.log("Main{}:" + s);
     }
}
