/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lowlevel;
import java.awt.*;
import javax.swing.*;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.image.*;

public class Main
{
     public static final boolean CENTRALISE_ON_SECOND_MONITOR=false;//for testing on my 2 monitr setup.
     public static Canvas canvas;
     public static JFrame frame;

     private static final int FRAME_DELAY_MILLIS = 50;//50;//20; // 20ms. implies 50fps (1000/20) = 50
     private static boolean gameThreadIsRunning = true;
     public static void main(String[] args)
     {
         log("Main called, Backgammon starting.");
         frame = new JFrame();

         log("insetY:" + insetY + ", insetX" + insetX);
         canvas = new CustomCanvas(frame); // create our canvas object that has custom rendering in it.
         frame.getContentPane().add(canvas);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
         frame.setSize(810, 500);
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

         Insets insets = frame.getInsets();
         insetY=insets.top;
         insetX=insets.left;

         windowX=frame.getLocationOnScreen().x;
         windowY=frame.getLocationOnScreen().y;
     }

     static boolean isHidden;
     public static void hideMousePointer(boolean hide)
     {
         if (hide && !isHidden)
         {
            isHidden=true;
            int[] pixels = new int[16 * 16];
            Image image = Toolkit.getDefaultToolkit().createImage( new MemoryImageSource(16, 16, pixels, 0, 16));
            Cursor transparentCursor =  Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
            frame.setCursor(transparentCursor);
         }
         else if (!hide && isHidden)
         {
             isHidden=false;
             frame.setCursor(null);
         }
     }

     public static int insetY;
     public static int insetX;

     public static final int WINDOWY_MINUS=50;

     private static void centralise(JFrame frame) {
        // get dimensions of the screen for centreing window later
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
	    // Centre the window
        if (CENTRALISE_ON_SECOND_MONITOR) {
            frame.setLocation((screenDim.width),(screenDim.height/8)-WINDOWY_MINUS);
        }
        else
        {
            frame.setLocation((screenDim.width/2)-frame.getWidth()/2,(screenDim.height/2)-(frame.getHeight()/2)-WINDOWY_MINUS);
        }
     }

     static int windowX;
     static int windowY;
     public static int getWindowXpos() {
         return windowX;
     }

     public static int getWindowYpos() {
          return windowY;
     }

     private static void log(String s) {
         Utils.log("Main{}:" + s);
     }
}
