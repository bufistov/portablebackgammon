/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lowlevel;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;

/**
 *
 * @author Gaz
 */
class ThreadLoop implements Runnable {

   


      boolean isRunning = true;
      public void run() {
        long cycleTime = System.currentTimeMillis();
        while(isRunning) {
            Main.canvas.repaint();
            cycleTime = cycleTime + Main.FRAME_DELAY;
            long difference = cycleTime - System.currentTimeMillis();
            try {
             Thread.sleep(Math.max(0, difference));
            }
            catch(InterruptedException e) {
             e.printStackTrace();
            }
         }
      }
 }




