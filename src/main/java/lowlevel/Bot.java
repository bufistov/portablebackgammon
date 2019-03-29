package lowlevel;
import gamelogic.Board;
import gamelogic.Player;
import java.awt.Robot;
import java.awt.event.InputEvent;


/** A robotic player who can move mouse etc, for demo and test automation and cpu player
 *
 */
public class Bot extends Thread {

    Robot robot;
    CustomCanvas canvas;

    public static  long DELAY_BETWEEN_CLICKS_MILLIS = 1000;
    public static  long ROBOT_DELAY_AFTER_CLICKS = 100;
    public static boolean dead = true;
    public static int x = 0;
    public static int y = 0;
    int addMeX;
    int addMeY;
    public static int destX;
    public static int destY;

    private static boolean JUMP_DIRECT_TO_DEST = false;
    private boolean READY2CLICK = true;
    private static boolean STOPCLICKING = false;
    private long clickedTime;
    private int lastX, lastY;
    private int sameDestCounter;

    private static boolean FULL_AUTO_PLAY = false;//Plays everything via the bot so you can watch it all
    private static boolean TAKES_OVER_MOUSE = false;
    private boolean isRunning = true;

    public Bot(CustomCanvas canvas_) {
        log("Bot born.");
        canvas = canvas_;
        try {
            robot = new Robot();
        }
        catch(Exception e) {
            Utils._E("ERROR making robot " + e.getMessage());
        }
    }

    public static boolean getFullAutoPlay() {
        return FULL_AUTO_PLAY;
    }

    public static void setFullAutoPlay(boolean value) {
        FULL_AUTO_PLAY = value;
    }

    private void tick() {
        if (dead || canvas.gameComplete() || (Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt == Player.WHITE)) {
            try {
                Thread.sleep(50);
                return;
            } catch (InterruptedException ie) {
                log("Interrupted while sleeping");
                Thread.currentThread().interrupt();
            }
        }

        addMeX = Main.getWindowXpos();
        addMeY = Main.getWindowYpos() + 20; // WORK OUT WHY IT NEEDS 15
        if (x == destX && y == destY && x != 0 && y != 0 ) {
            long difference = System.currentTimeMillis() - clickedTime;
            if (difference > DELAY_BETWEEN_CLICKS_MILLIS && !STOPCLICKING) {
                READY2CLICK=true;
                log("DEST REACHED. destX:" + destX + " destY:" + destY);
            }

            if (READY2CLICK) {
                if (lastX==destX && lastY==destY) {
                    sameDestCounter++;
                    if(sameDestCounter>3) {
                        log("SAME DEST FIXER~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        canvas.mouseClickedX(destX, destY, CustomCanvas.RIGHT_MOUSE_BUTTON);
                        clickedTime = System.currentTimeMillis();
                        READY2CLICK=false;
                    }
                } else {
                    sameDestCounter=0;
                }
                lastX=destX;
                lastY=destY;
                if (TAKES_OVER_MOUSE) {
                    robot.delay(100);
                    clickedTime = System.currentTimeMillis();
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                    robot.delay(100);
                } else {
                    try {
                        Thread.sleep(25);
                    } catch(Exception e) {
                        Utils._E("insomnia!");
                    }
                    canvas.mouseClickedX(destX, destY, CustomCanvas.LEFT_MOUSE_BUTTON);
                    clickedTime = System.currentTimeMillis();
                    READY2CLICK=false;
                }
            }
        }
        if (JUMP_DIRECT_TO_DEST) {
            x=destX;
            y=destY;
            if (TAKES_OVER_MOUSE) {
                robot.mouseMove(x+addMeX, y+addMeY);
            }
        } else {
            try {
                Thread.sleep(1);
            }
            catch(Exception e) {
                Utils._E("insomnia!");
            }
            if (x<destX) {
                x++;
                if (TAKES_OVER_MOUSE) {
                    robot.mouseMove(x+addMeX, y+addMeY);
                }
            }
            if (x>destX) {
                x--;
                if (TAKES_OVER_MOUSE) {
                    robot.mouseMove(x+addMeX, y+addMeY);
                }
            } if (y<destY) {
                y++;
                if (TAKES_OVER_MOUSE) {
                    robot.mouseMove(x+addMeX, y+addMeY);
                }
            }
            if (y>destY) {
                y--;
                if (TAKES_OVER_MOUSE) {
                    robot.mouseMove(x+addMeX, y+addMeY);
                }
            }
        }
    }

    @Override
    public void run() {
        while(isRunning) {
            tick();
        }
    }

    private void log(String s) {
        Utils.log("Bot{}:" + s);
    }
}
