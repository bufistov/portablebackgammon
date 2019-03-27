package lowlevel;

import java.awt.*;
import java.awt.event.*;
import gamelogic.*;
import java.awt.image.BufferStrategy;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JFrame;

import static gamelogic.GuiState.*;

/** This class is used basically for calling the right paint methods
 *  based on state, these paint due to this class being a subclass of canvas.
 */
public class CustomCanvas extends Canvas implements MouseListener, MouseMotionListener, KeyListener {

    private final int maxSplashCounter = 50;
    private static final boolean drawMousePointer = true;
    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final String SPECIAL_END_SYMBOL = "::";// this signifiys to scroll bar the end is reached whislt being invisible to our customfont
    // breaks down wrapMe into a vector and prints each line after each other making sure that the text wraps
    // properly.

    private static boolean I_AM_CLIENT;
    public static boolean I_AM_SERVER;

    public static final String VERSION = "v0.0.1";
    private static final boolean RELEASE_BUILD = false;
    public static boolean SOUND_ON = true;

    // Debug
    public static boolean showBoundaryBoxes = false;
    private static boolean PAINT_STATE = false;
    private static final String DEBUG_HEADER = "Midokura Backgammon game (DEBUG MODE):";

    public static int TINY_GAP = 5; // when we need a tiny gap
    private int typeOfPlay = -1;
    private static final int NETWORK_PLAY = 1;
    private static final int LOCAL_PLAY = 2;

    // -- constants
    private static int PANEL_COLOUR = 0x000000;
    public static int BACKGROUND_COLOUR = 0x993300;
    private static int ROLL_BUTTON_COLOUR = 0xffcc66;
    private Color panel_colour, background_colour, roll_button_colour;

    private static final int PANEL_SIZE_FRACTION = 5; // adjust me to change ratio:
    //this simply means the panel will represent one x-th of the available screen,
    // ergo if PANEL_SIZE_FRACTION is 5, it uses 1/5 of the space avail and the game
    // uses the other 4/5

    private CustomFont fontwhite, fontblack;
    private boolean INFO = false;    // 'about box' toggle
    private Utils utils = new Utils();   // Hardware Abstraction Layer
    private GuiState state = GuiState.SPLASH_SCREEN;
    String stateString;
    int PANEL_WIDTH=0;
    public Bot bot = new Bot(this); // make a robotic player who can move mouse etc, for demo and test automation and cpu player

    /////j2se specific vars
    JFrame jFrame;
    // Acquiring the current Graphics Device and Graphics Configuration
    GraphicsEnvironment   graphEnv     = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice        graphDevice  = graphEnv.getDefaultScreenDevice();
    GraphicsConfiguration graphicConf  = graphDevice.getDefaultConfiguration();
    Graphics2D g;
    BufferStrategy bufferStrategy;

    public static boolean NETWORK_GAME_IN_PROCESS;
    public static Sound sfxmouseClick, sfxDiceRoll, sfxDoubleRolled,
        sfxError,sfxNoMove,sfxPutPieceInContainer, sfxGameOver, sfxKilled;
    public static Sound sfxdouble, sfxResign;

    // Garbage
    static String robotMoveDesc = "Bot loaded.";
    boolean flip;
    int paraYoffset=0;
    int OUTLINE_FOR_CHAT_BOXES=0;
    Vector playerPositions;
    boolean buttonPressed;
    //prefs button x,y width and height
    private static final int prefw = 20;
    private static final int prefh = 20;
    public static long FIFTY_SECONDS = 50000L;
    public static long TEN_SECONDS = 10000L;
    static long robotMessageSetTimeLong;
    private static int TRANSPARENCY_LEVEL = 100;

    private Board board;
    private int y;
    private int x;
    private int splashCounter;

    // for collisions.
    public static int whiteContainerX;
    public static int whiteContainerY;
    public static int whiteContainerWidth;
    public static int whiteContainerHeight;
    public static int blackContainerX;
    public static int blackContainerY;
    public static int blackContainerWidth;
    public static int blackContainerHeight;

    // use these to detect if the roll button was clicked.
    int rollButtonX;
    int rollButtonY;
    int rollButtonW;
    int rollButtonH;

    public static boolean showRollButton=true;//false when not needed

    public static int D1lastDieRoll_toSendOverNetwork;
    public static int D2lastDieRoll_toSendOverNetwork;
    public static boolean someoneRolledADouble=false;
    public static int doubleRollCounter=0;//this tracks how many rolls a player has had after rolling a double,
    //ie, we want them to have 4 rolls if thats the case and not 2

    //for glowy buttons
    public static final int GLOW_INCREMENTER = 15;
    private boolean glowA, glowB;

    int doubleX;
    int doubleY;
    int doubleWidth;
    int doubleHeight;
    int resignX;
    int resignY;
    int resignWidth;
    int resignHeight;

    public static boolean whiteResigned;
    public static boolean blackResigned;

    public static Vector theBarWHITE = new Vector(4);//the bar holds pieces that get killed
    public static Vector theBarBLACK = new Vector(4);//the bar holds pieces that get killed

    //these store the pieces that have been sent to the container, when all are in that player wins.
    public static Vector whitePiecesSafelyInContainer=new Vector(15);
    public static Vector blackPiecesSafelyInContainer=new Vector(15);

    private static boolean DEBUG_CONSOLE = false;
    private boolean PAUSED;
    NetworkChatClient chatClient;
    boolean ignoreRepaints=true;
    public static String chatText="";

    private GameNetworkClient client;
    public static final int LEFT_MOUSE_BUTTON = 0;
    public static final int RIGHT_MOUSE_BUTTON = 1;

    public static boolean pieceOnMouse = false; // is true when a piece is stuck to mouse
    public static Piece pieceStuckToMouse; // this is simply a copy of whatever piece (if any) is stuck to mouse

    boolean showChallengeWindow;
    String personToChallenge;

    // for screens with 2 buttons this is button 1
    int buttonxA, buttonyA;
    int buttonwA, buttonhA;
    //and button 2
    int buttonxB, buttonyB;
    int buttonwB, buttonhB;

    private int glowCounter = 125;

    private static boolean paintRobotMessages;
    private long playerMessageSetTimeLong;//thjis keeps the version on screen for a few secs at the start
    //sets the vars to allow a message to be shown to the player in bottom right for
    //a while

    private static final long SHOW_ME_LIMIT = 3000; // how long  show player message 1.5 sec
    static String message2Players=""+VERSION;

    int messageWidth, messageHeight;
    int messagex,messagey;

    //when the user clicks on a piece and it sticks to the mouse we hold it in
    //pieceStuckToMouse, this variable below is the spike that holds pieceStuckToMouse
    //we use it so we can add/remove the pieceStuckToMouse from this spike if its
    //placed onto a new one.
    private Spike originalSpikeForPieceSelected;
    public static boolean barPieceStuckOnMouse;

    //////////////////THEMES CODE/////////////////
    public static final int DEFAULT   = 0;
    public static final int METALIC   = 1;
    public static final int CLASSIC   = 2;
    public static final int FUNNYMAN  = 3;
    public static final int BUMBLEBEE = 4;
    public static final int MAX_THEMES = 4; // this should always equals the last one
    private int theme = DEFAULT;
    private String themeName;
    private boolean firstThemeSet = true; // so we dont tell players when the theme is set upon loading but we do othertimes

    CustomCanvas(JFrame jFrame_) {
        log("CustomCanvas made.");
        board = new Board(this);
        bot.start();
       
        // j2se specifics
        jFrame = jFrame_;
        addMouseListener(this);
        addMouseMotionListener( this );
        addKeyListener( this );

        // set icon in corner
        jFrame.setIconImage(utils.loadImage("/icon.gif"));
        jFrame.setResizable(false);

        setTheme(theme);
        makeColourObjects();
        loadCustomFonts();
        loadImages();

        log("Loading Sounds");
        sfxmouseClick = new Sound("/mouseclick.wav");
        sfxDiceRoll   = new Sound("/diceroll.wav");
        sfxDoubleRolled = new Sound("/whoosh.wav");
        sfxError=new Sound("/error.wav");
        sfxNoMove=new Sound("/nomove.wav");
        sfxPutPieceInContainer=new Sound("/pieceputaway.wav");
        sfxGameOver=new Sound("/gameover.wav");
        sfxKilled=new Sound("/killed.wav");
        sfxdouble=new Sound("/double.wav");
        sfxResign=new Sound("/resign.wav");
        log("Sounds loaded.");
        requestFocus();  // get focus for keys
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
          setIgnoreRepaint(true);
        }
    }

    @Override
    public void paint(Graphics g_) {
        handleMouse();
        doubleBuffering(1); // pass 1 in to start dbl buffering
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // whole game canvas:
        WIDTH = (getWidth() / PANEL_SIZE_FRACTION) * (PANEL_SIZE_FRACTION - 1);
        PANEL_WIDTH = (getWidth() / PANEL_SIZE_FRACTION) - Board.BORDER;
        HEIGHT = getHeight();
        paintSwitch(g);
        if (drawMousePointer) {
            if (NETWORK_GAME_IN_PROCESS) {
                utils.drawImage(g, pointer, pointerX, pointerY + 6, this); // this 6 lines it up
                Board.mouseHoverX = pointerX;
                Board.mouseHoverY = pointerY;
            } else {
                if (Bot.getFullAutoPlay()) {
                    utils.drawImage(g, pointer, Bot.x, Bot.y + 6, this); // this 6 lines it up
                    Board.mouseHoverX = Bot.x;
                    Board.mouseHoverY = Bot.y;
                } else {
                    if (Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt == Player.WHITE) {
                        Main.hideMousePointer(false);
                    } else if (Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt == Player.BLACK) {
                        Main.hideMousePointer(true);
                        Bot.dead = false;
                        utils.drawImage(g, pointer, Bot.x, Bot.y + 6, this); // this 6 lines it up
                        Board.mouseHoverX = Bot.x;
                        Board.mouseHoverY = Bot.y;
                    } else {
                        Main.hideMousePointer(false);
                    }
                }
            }
        }
        doubleBuffering(2); // pass 2 in to start dbl buffering
    }

    /*
    * The pointer is a wrapper over the real pointer, this is because at times we want the computer to control the mouse
    */
    private void handleMouse() {
    }

    // implements double buffering, phase is 1 or 2, 1 is called before
    // painting and 2 is called after. Any other phase is erroneous
    private void doubleBuffering(int phase) {
        if (phase==1) {
            //START DBL BUFFERING
            this.createBufferStrategy(2); // must be after we are visible!
            bufferStrategy = this.getBufferStrategy();
            g = (Graphics2D)bufferStrategy.getDrawGraphics();
        } else if (phase==2) {
            bufferStrategy.show();
        } else {
            Utils._E("doubleBuffering() phase was invalid "+phase);
        }
    }

    // Calls a different paint method based on the current state
    private void paintSwitch(Graphics g) {
        switch(state) {
            case SPLASH_SCREEN:
                paint_SPLASH_SCREEN(g);
                break;
            case OPTIONS_SCREEN_LOCAL_OR_NETWORK:
                glowButton(Board.mouseHoverX, Board.mouseHoverY);
                paint_OPTIONS_SCREEN_LOCAL_OR_NETWORK(g," Local Play ","Network Play","Please select");
                break;
            case OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN:
                glowButton(Board.mouseHoverX, Board.mouseHoverY);
                paint_OPTIONS_SCREEN_LOCAL_OR_NETWORK(g,"Computer"," Human  ","Play against");
                break;
            case GAME_IN_PROGRESS:
                paint_POST_SPLASH_SCREEN(g);
                break;
            case NETWORKING_ENTER_NAME:
                paint_NETWORKING_ENTER_NAME(g);
                break;
            case NETWORKING_LOBBY:
                paint_NETWORKING_LOBBY(g);
                break;
            default://///////////////////////////////////////////////
                Utils._E("Warning state in paint unrecognised!");
                break;
        }
        drawExtras(g);
    }

    //draws any of the extras:
    //debug info, about box, messages to players etc
    private void drawExtras(Graphics g) {
        if (PAINT_STATE) {
            paintState(g);
        }
        if (INFO) {
            paintAboutBox(g);
        }
        if (Utils.CANVAS_LOGGING) {
            paintStringsToCanvas(g);
        }
        //all of this in aid of a loop that lasts for x amount of seconds not a cpu dependent tick,
        //could be a bit over the top for what im doign (todo optimise?)
        long playerMessageTimePassedLong = System.currentTimeMillis() - playerMessageSetTimeLong;
        if (playerMessageTimePassedLong < SHOW_ME_LIMIT ) {
            paintMessageToPlayers(g);
        }
        if (Bot.dead == false) {
            if (paintRobotMessages) {
                  //all of this in aid of a loop that lasts for x amount of seconds not a cpu dependent tick,
                  //could be a bit over the top for what im doign (todo optimise?)
                long robotMessageTimePassedLong = System.currentTimeMillis()-robotMessageSetTimeLong;
                if (robotMessageTimePassedLong > FIFTY_SECONDS) {//so we dont have a mad long getting bigger and bigger
                    robotMessageTimePassedLong = TEN_SECONDS;//so it doesnt bring message back
                    robotMessageSetTimeLong = System.currentTimeMillis() - TEN_SECONDS;
                }
                if (robotMessageTimePassedLong < SHOW_ME_LIMIT) {
                    paintRobotMessage(g);
                }
            }
        }
        if (DEBUG_CONSOLE) {
            paintDebugBox(g);
        }
    }

    private void paintAboutBox(Graphics g) {
        infoCounter++;
        if (infoCounter > maxSplashCounter) {
            infoCounter = 0;
            INFO = false;
        }

        utils.setColor(g, 0,0,0,TRANSPARENCY_LEVEL);
        utils.fillRoundRect(g, WIDTH/4, HEIGHT/4, WIDTH/2, HEIGHT/2);
        utils.setColor(g, Color.white);
        utils.drawRoundRect(g, WIDTH/4, HEIGHT/4, WIDTH/2, HEIGHT/2);

        int xabout=WIDTH/2;
        int yabout=(HEIGHT/4)+TINY_GAP;

        //paint the about box
        String printme="Forumosa Backgammon ("+VERSION+")";
        fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);
        yabout += fontblack.getHeight();
        printme="- www.forumosa.com -";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme="Keys:    ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme="q = quit ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme="t = theme";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme="Developed by www.garethmurfin.co.uk";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    }

    //paints the about box
    private void paintDebugBox(Graphics g) {
        infoCounter++;
        if (infoCounter > maxSplashCounter) {
            infoCounter = 0;
            INFO = false;
        }

        utils.setColor(g, 0, 0, 0, 125);
        x = 10;
        y = 10;
        utils.fillRoundRect(g, x, y, WIDTH / 2, HEIGHT - 40);
        utils.setColor(g, Color.yellow);
        utils.drawRoundRect(g, x, y, WIDTH / 2, HEIGHT - 40);

        x += 5;
        y += TINY_GAP;
        //paint the about box
        String printme = "Backgammon (" + VERSION + ") DEBUG CONSOLE";
        fontwhite.drawString(g, printme, x, y, 0);
        y += fontblack.getHeight();

        printme = "DELAY_BETWEEN_CLICKS_MILLIS:" + Bot.DELAY_BETWEEN_CLICKS_MILLIS;
        //Highlighter to indicate if its on this option
        if (debugMenuPos == 0) {
            utils.drawRoundRect(g, x, y, fontwhite.stringWidth(printme + " "), fontwhite.getHeight());
        }
        //option itself,
        fontwhite.drawString(g, printme, x, y, 0);
        y += fontblack.getHeight();


        printme = "ROBOT_DELAY_AFTER_CLICKS:" + Bot.ROBOT_DELAY_AFTER_CLICKS;
        //Highlighter to indicate if its on this option
        if (debugMenuPos == 1) {
            utils.drawRoundRect(g, x, y, fontwhite.stringWidth(printme + " "), fontwhite.getHeight());
        }
        //option itself,
        fontwhite.drawString(g, printme, x, y, 0);
        y += fontblack.getHeight();

        printme = "paintRobotMessages:" + paintRobotMessages;
        //Highlighter to indicate if its on this option
        if (debugMenuPos == 2) {
            utils.drawRoundRect(g, x, y, fontwhite.stringWidth(printme + " "), fontwhite.getHeight());
        }
        //option itself,
        fontwhite.drawString(g, printme, x, y, 0);
        y += fontblack.getHeight();

        printme = "FULL_AUTO_PLAY:" + Bot.getFullAutoPlay();
        //Highlighter to indicate if its on this option
        if (debugMenuPos == 3) {
            utils.drawRoundRect(g, x, y, fontwhite.stringWidth(printme + " "), fontwhite.getHeight());
        }
        //option itself,
        fontwhite.drawString(g, printme, x, y, 0);
        y += fontblack.getHeight();

        y += 10;

        String[] helpMessages = {"Q = QUIT",
            "P = PAUSE (bot dead? " + Bot.dead + ")",
            "D = DEBUG CONSOLE (" + DEBUG_CONSOLE + ")",
            "T = THEME (" + themeName + ")", "C = COLLISIONS (" + showBoundaryBoxes + ")",
            "L = CANVAS LOGGING (" + Utils.CANVAS_LOGGING + ")", "S = SOUND (" + SOUND_ON + ")", "X = TEST SOUND",
            "J = JUMP TO DESTINATION: unknown",
            "F = FULL_AUTO_PLAY: unknwon"};
        for (String message : helpMessages) {
            fontwhite.drawString(g, message, x, y, 0);
            y += fontblack.getHeight();
        }
        y += 5;
        printme = Board.ROBOT_DESTINATION_MESSAGE;

        y = drawMeWrapped(g, x, y, printme, fontwhite, false, false, true, WIDTH / 2, false);
        if (robotMoveDesc.length() < 20)//avoid printing textual things, just moves.
        {
            printme = "Bot is thinking:" + robotMoveDesc;
            fontwhite.drawString(g, printme, x, y, 0);
            y += fontblack.getHeight();
        }
        if (Board.listBotsOptions && Board.botOptions.length() > 4) {//avoid printing textual things, just moves.
            printme = "Alternatives:";
            fontwhite.drawString(g, printme, x, y, 0);/////y+=fontblack.getHeight();
            //Graphics g,int y, String wrapMe, CustomFont font,String newLineChar,boolean backdrop,boolean scrollbar,boolean outline,boolean justifyleft)
            printme = Board.botOptions;
            y = drawMeWrapped(g, x, y, printme, fontwhite, false, false, true, WIDTH / 2, false);
        }
        printme = "BAR:W(" + theBarWHITE.size() + "),B(" + theBarBLACK.size() + ")";
        fontwhite.drawString(g, printme, x, y, 0);
        y += fontblack.getHeight();
        printme = "DIE Used?:(" + Board.die1HasBeenUsed + "),(" + Board.die2HasBeenUsed + ")";
        fontwhite.drawString(g, printme, x, y, 0);
        y += fontblack.getHeight();
    }

    int debugMenuPos=0;
    public static final int DEBUG_OPTION_TIME_DELAY_BETWEEN_CLICKS=0;
    public static final int DEBUG_OPTION_ROBOT_DELAY_AFTER_CLICKS=1;
    public static final int DEBUG_OPTION_paintRobotMessages=2;
    public static final int DEBUG_OPTION_FULL_AUTO_PLAY=3;
    public static final int LAST_DEBUG_OPTION=3;

    public static final int DEBUGLEFT=1;
    public static final int DEBUGRIGHT=2;

    private void debugOptionChanged(int direction) {
        switch (debugMenuPos) {
            //DELAY_BETWEEN_CLICKS_MILLIS
            case DEBUG_OPTION_TIME_DELAY_BETWEEN_CLICKS:
                if (direction == DEBUGLEFT) {
                    Bot.DELAY_BETWEEN_CLICKS_MILLIS -= 10;
                    log("DELAY_BETWEEN_CLICKS_MILLIS:" + Bot.DELAY_BETWEEN_CLICKS_MILLIS);
                }
                if (direction == DEBUGRIGHT) {
                    Bot.DELAY_BETWEEN_CLICKS_MILLIS += 10;
                    log("DELAY_BETWEEN_CLICKS_MILLIS:" + Bot.DELAY_BETWEEN_CLICKS_MILLIS);
                }
                break;
            //DEBUG_OPTION_ROBOT_DELAY_AFTER_CLICKS
            case DEBUG_OPTION_ROBOT_DELAY_AFTER_CLICKS:
                if (direction == DEBUGLEFT) {
                    Bot.ROBOT_DELAY_AFTER_CLICKS -= 10;
                    log("ROBOT_DELAY_AFTER_CLICKS:" + Bot.ROBOT_DELAY_AFTER_CLICKS);
                }
                if (direction == DEBUGRIGHT) {
                    Bot.ROBOT_DELAY_AFTER_CLICKS += 10;
                    log("ROBOT_DELAY_AFTER_CLICKS:" + Bot.ROBOT_DELAY_AFTER_CLICKS);
                }
                break;
            //DEBUG_OPTION_paintRobotMessages
            case DEBUG_OPTION_paintRobotMessages:
                if (direction == DEBUGLEFT) {
                    paintRobotMessages = !paintRobotMessages;
                    log("paintRobotMessages:" + paintRobotMessages);
                }
                if (direction == DEBUGRIGHT) {
                    paintRobotMessages = !paintRobotMessages;
                    log("paintRobotMessages:" + paintRobotMessages);
                }
                break;
            //DELAY_BETWEEN_CLICKS_MILLIS
            case DEBUG_OPTION_FULL_AUTO_PLAY:
                if (direction == DEBUGLEFT) {
                    Bot.setFullAutoPlay(!Bot.getFullAutoPlay());
                    log("FULL_AUTO_PLAY:" + Bot.getFullAutoPlay());
                }
                if (direction == DEBUGRIGHT) {
                    Bot.setFullAutoPlay(!Bot.getFullAutoPlay());
                    log("FULL_AUTO_PLAY:" + Bot.getFullAutoPlay());
                }
                break;
            default:
                Utils._E("UNKNOWN DEBUG OPTION CHANGED:" + debugMenuPos);
                break;
        }
    }

    //paints the state - for debugging.
    private void paintState(Graphics g) {
        fontblack.drawString(g, state.name(),20,20,0);
    }

    int infoCounter=0;
    Image splashScreenLogo,splashScreenLogoSmall;
    Image op,admin;
    public static Image pointer;
    public static int WIDTH;
    public static int HEIGHT;

    public static int pointerX;
    public static int pointerY;

    //loads all images needed
    private void loadImages() {
        log("Attempting to loadImages()");
        if (splashScreenLogo == null) {
            splashScreenLogo      = utils.loadImage("/midokura-logo.png");
             splashScreenLogoSmall = utils.loadImage("/midokura-logo-small.png");
             pointer               = utils.loadImage("/pointer.png");
             op                    = utils.loadImage("/op.png");
             admin                 =  utils.loadImage("/admin.png");
        }
        else {
            log("Images already pre-cached...");
        }
    }

    ///////// ALL PAINT STATE METHODS //////////////////////
    private void paint_SPLASH_SCREEN(Graphics g) {
        utils.backGround(g, Color.WHITE, getWidth(), getHeight());
        utils.drawImage(g, splashScreenLogo, getWidth()/2, getHeight()/2,this);
        utils.setColor(g, Color.BLACK);
        if (showBoundaryBoxes) {
             int ydebug = 10;
             int xdebug = 10;
             fontblack.drawString(g, DEBUG_HEADER, xdebug, ydebug,0);
             ydebug += fontblack.getHeight();
             fontblack.drawString(g, VERSION, xdebug, ydebug,0);
             ydebug += fontblack.getHeight();
             fontblack.drawString(g, splashCounter + "/" + maxSplashCounter, xdebug, ydebug,0);
        }
        if (splashCounter++ > maxSplashCounter) {
            log("Splash done.");
            state = OPTIONS_SCREEN_LOCAL_OR_NETWORK;
        }
    }

    private void paint_POST_SPLASH_SCREEN(Graphics g) {
        utils.backGround(g, background_colour, getWidth(), getHeight()); // paint entire background
        utils.setColor(g, Color.WHITE);

        //paint board and its containing parts
        int boardWidth = (getWidth() / PANEL_SIZE_FRACTION) * (PANEL_SIZE_FRACTION - 1);
        int boardHeight = getHeight();
        board.paint(g, boardWidth, boardHeight);
        //paint the message panel to the right with players name etc
        utils.setColor(g, panel_colour);
        utils.fillRect(g, boardWidth, Board.BORDER, PANEL_WIDTH, boardHeight - (Board.BORDER * 2));

        //draw the preferences button
        final int prefx = preferencesButtonX();
        final int prefy = preferencesButtonY();

        //draw a circle with an 'i' inside.
        utils.setColor(g, Color.blue);
        utils.fillCircle(g, prefx, prefy, prefw, prefh);
        utils.setColor(g, Color.white);
        utils.drawCircle(g, prefx, prefy, prefw, prefh);
        fontwhite.drawString(g, "i", prefx + 4, prefy + 2, 0);

        if (showBoundaryBoxes) {
            utils.setColor(g, Color.RED);
            utils.drawRect(g, prefx, prefy, prefw, prefh);
        }

        // draw panel text:
        int xpos = boardWidth + TINY_GAP;

        // draw the piece container
        int heightOf3LinesOfText = (fontwhite.getHeight() * 3) + (Board.BORDER * 2) + TINY_GAP;
        int containerSubSize = boardHeight / 70;
        int containerWidth = PANEL_WIDTH / 3;
        int topOfPieceContainer = boardHeight - ((containerSubSize * 15) + heightOf3LinesOfText);

        if (Board.allBlackPiecesAreHome) {
            utils.setColor(g, Color.GREEN);
            if (Board.pulsateBlackContainer) {
                utils.setColor(g, Color.YELLOW);//dra piece container yellow when its an option
            }
        } else {
            utils.setColor(g, Color.WHITE);
        }
        //draw black players piece container
        drawPieceContainer(g, xpos, topOfPieceContainer, containerWidth,
            containerSubSize, heightOf3LinesOfText, Player.BLACK);

        heightOf3LinesOfText = (fontwhite.getHeight() * 3) + Board.BORDER + TINY_GAP;
        topOfPieceContainer = heightOf3LinesOfText;

        if (Board.allWhitePiecesAreHome) {
            utils.setColor(g, Color.GREEN);
            if (Board.pulsateWhiteContainer) {
                utils.setColor(g, Color.YELLOW);//dra piece container yellow when its an option
            }
        } else {
            utils.setColor(g, Color.WHITE);
        }
        // draw white players piece container
        drawPieceContainer(g, xpos, topOfPieceContainer, containerWidth,
            containerSubSize, heightOf3LinesOfText, Player.WHITE);

        int pieceOnBarY = (HEIGHT / 2) - Piece.PIECE_DIAMETER;
        //Draw pieces on the bar//////////////
        Enumeration eW = theBarWHITE.elements();
        while (eW.hasMoreElements()) {
            Piece p = (Piece) eW.nextElement();
            p.paint(g, (WIDTH / 2) - Piece.PIECE_DIAMETER / 2, pieceOnBarY -= Piece.PIECE_DIAMETER);
        }
        pieceOnBarY = (HEIGHT / 2);
        Enumeration eB = theBarBLACK.elements();
        while (eB.hasMoreElements()) {
            Piece p = (Piece) eB.nextElement();
            p.paint(g, (WIDTH / 2) - Piece.PIECE_DIAMETER / 2, pieceOnBarY += Piece.PIECE_DIAMETER);
        }
        drawHUDtext(xpos);
    }

    //draws the little holder where the pieces go
    private void drawPieceContainer(Graphics g, int xpos, int topOfPieceContainer,
            int containerWidth, int containerSubSize, int heightOf3LinesOfText,
            int player) {

        int piecesOnContainer = 0;
        if (player == Player.WHITE) {
            piecesOnContainer = whitePiecesSafelyInContainer.size();
        } else if (player == Player.BLACK) {
            piecesOnContainer = blackPiecesSafelyInContainer.size();
        }
        int myX = WIDTH + ((PANEL_WIDTH / 4) - (containerWidth / 2));
        int myY = topOfPieceContainer;
        for (int i = 0; i < 15; i++) {
            //simply draws the containers green if players have all their pieces in the home section
            //and therefore the piece containers are 'live' and ready for action
            myY = myY + containerSubSize;
            if (i < piecesOnContainer) {
                Color originalColor = utils.getColor();
                utils.setColor(g, Color.ORANGE);
                utils.fillRect(g, myX, myY, containerWidth, containerSubSize);
                utils.setColor(g, originalColor);
            }
            utils.drawRect(g, myX, myY, containerWidth, containerSubSize);
        }
        //update collision data
        if (player == Player.WHITE) {
            whiteContainerX = myX;
            whiteContainerY = myY - (containerSubSize * 14);
            whiteContainerWidth = containerWidth;
            whiteContainerHeight = containerSubSize * 15;
            if (showBoundaryBoxes) {
                utils.setColor(g, Color.RED);
                utils.drawRect(g, whiteContainerX, whiteContainerY, whiteContainerWidth, whiteContainerHeight);
            }
        } else if (player == Player.BLACK) {
            blackContainerX = myX;
            blackContainerY = myY - (containerSubSize * 14);
            blackContainerWidth = containerWidth;
            blackContainerHeight = containerSubSize * 15;
            if (showBoundaryBoxes) {
                utils.setColor(g, Color.RED);
                utils.drawRect(g, blackContainerX, blackContainerY, blackContainerWidth, blackContainerHeight);
            }
        } else {
            Utils._E("drawPieceContainer has been given incorrect player number!");
        }
    }

    //draw all of the text on the panel
    private void drawHUDtext(int xpos) {
        int ypos = Board.BORDER + TINY_GAP;
        //draw black players score at top
        String printme = "White (" + board.getBlackPlayer().name + ")";
        if (board.whoseTurnIsIt == Player.WHITE) {
            printme += "*";
        }
        fontwhite.drawString(g, printme, xpos, ypos, 0);
        ypos += fontwhite.getHeight();

        printme = "Pips: " + calculatePips(Player.WHITE);
        fontwhite.drawString(g, printme, xpos, ypos, 0);
        ypos += fontwhite.getHeight();
        printme = "Score: " + board.getBlackPlayer().score;
        fontwhite.drawString(g, printme, xpos, ypos, 0);

        //draw white players score at bot
        ypos = HEIGHT - 9 - (Board.BORDER * 2) - (fontwhite.getHeight() * 2);
        printme = "Brown (" + board.getWhitePlayer().name + ")";
        if (board.whoseTurnIsIt == Player.BLACK) {
            printme += "*";
        }
        fontwhite.drawString(g, printme, xpos, ypos, 0);
        ypos += fontwhite.getHeight();
        printme = "Pips: " + calculatePips(Player.BLACK);/*board.getWhitePlayer().pips;*/
        fontwhite.drawString(g, printme, xpos, ypos, 0);
        ypos += fontwhite.getHeight();
        printme = "Score: " + board.getWhitePlayer().score;
        fontwhite.drawString(g, printme, xpos, ypos, 0);

        int xposTmp = -1;
        ypos = (HEIGHT / 2) - ((fontwhite.getHeight() * 4) / 2);
        printme = "Match Points: " + board.matchPoints;
        int widthOfPrintMe = (fontwhite.stringWidth(printme));
        xposTmp = (WIDTH + PANEL_WIDTH / 2) - ((widthOfPrintMe / 2) + TINY_GAP);
        fontwhite.drawString(g, printme, xposTmp, ypos, 0);
        ypos += fontwhite.getHeight();
        utils.setColor(g, roll_button_colour);

        //---- draw buttons
        ///////// double button
        printme = "Double";
        widthOfPrintMe = (fontwhite.stringWidth(printme));
        xposTmp = (WIDTH + PANEL_WIDTH / 2) - ((widthOfPrintMe / 2) + TINY_GAP);
        utils.setColor(g, roll_button_colour);
        ypos += 10;
        utils.drawRoundRect(g, xposTmp - 10, ypos, widthOfPrintMe + 20, (fontwhite.getHeight()));
        fontwhite.drawString(g, printme, xposTmp, ypos + 1, 0);
        ////
        doubleX = xposTmp - 10;
        doubleY = ypos;
        doubleWidth = widthOfPrintMe + 20;
        doubleHeight = (fontwhite.getHeight());
        if (showBoundaryBoxes) {
            utils.setColor(g, Color.red);
            utils.drawRect(g, doubleX, doubleY, doubleWidth, doubleHeight);
        }

        //draw the 'Roll' button
        ///////// roll button (on board itself (could be either side)
        printme = "" + Die.rollString; //either says roll or 'roll to see who goes first' ..
        widthOfPrintMe = (fontwhite.stringWidth(printme));

        //only show roll button when required
        if (CustomCanvas.showRollButton) {
            //draw in centre:
            xposTmp = ((WIDTH / 2)) - widthOfPrintMe / 2;

            utils.setColor(g, roll_button_colour);
            utils.fillRoundRect(g, xposTmp - 10, ypos, widthOfPrintMe + 20, (fontwhite.getHeight()));

            if (Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt == Player.BLACK || Bot.getFullAutoPlay()) {
                if (Board.NOT_A_BOT_BUT_A_NETWORKED_PLAYER && !RemotePlayer.clickRoll) {
                    log("WAITING FOR USER TO CLICK ROLL DICE REMOTELY");
                } else {
                    Board.setBotDestination((xposTmp - 10) + (widthOfPrintMe + 20) / 2,
                        ypos + (fontwhite.getHeight()) / 2, "PRESS ROLL BUTTON");
                }
            }

            /////for collisions
            rollButtonX = xposTmp - 10;
            rollButtonY = ypos;
            rollButtonW = widthOfPrintMe + 20;
            rollButtonH = (fontwhite.getHeight());
            //////////
            if (showBoundaryBoxes) {
                utils.setColor(g, Color.RED);
                utils.drawRect(g, rollButtonX, rollButtonY, rollButtonW, rollButtonH);
            }
            fontblack.drawString(g, printme, xposTmp, ypos + 1, 0);
            ypos += fontwhite.getHeight();

        } else {
            //still knock y down so other buttons draw inline.
            ypos += fontwhite.getHeight();
        }

        ///////// resign button
        printme = "Resign";
        widthOfPrintMe = (fontwhite.stringWidth(printme));
        xposTmp = (WIDTH + PANEL_WIDTH / 2) - ((widthOfPrintMe / 2) + TINY_GAP);
        utils.setColor(g, roll_button_colour);
        ypos += 10;
        utils.drawRoundRect(g, xposTmp - 10, ypos, widthOfPrintMe + 20, (fontwhite.getHeight()));
        fontwhite.drawString(g, printme, xposTmp, ypos + 1, 0);

        resignX = xposTmp - 10;
        resignY = ypos;
        resignWidth = widthOfPrintMe + 20;
        resignHeight = (fontwhite.getHeight());

        if (showBoundaryBoxes) {
            utils.setColor(g, Color.red);
            utils.drawRect(g, resignX, resignY, resignWidth, resignHeight);
        }
    }

    //returns the current pip count doe the player passed in.
    private int calculatePips(int player) {
        int pips = 0;

        /*pips is the amount of dots on the die it would take to get off the board, so to count them you go through the spikes
         * counting the number of pieces of that colour on the spike, then multiply that by the amount of spikes it is away from the
         * end of the board (INCLUDING the one to get onto the pice container), add these all up . as an example the starting pip count is 167 because:
         * 2 pieces on spike 0 (23 steps from end) * 2 = 48
         * 5 pieces on spike 11 (13 steps from end)*5=65
         * 3 pieces on spike 16 (8 steps from end) *3 = 24
         * 6 pieces on spike 18 (5 steps from the end) *6 =30
         * total is 167
         */
        Enumeration e = board.spikes.elements();
        if (player == Player.WHITE) {
            //works for white logic is simple
            for (int i = 0; i < 24; i++) {
                Spike spike = (Spike) board.spikes.elementAt(i);
                if (spike.getAmountOfPieces(player) > 0) {
                    pips += (i + 1) * spike.getAmountOfPieces(player);
                }
            }
        } else {
            //logic for black takes some thinking about!
            int j = 0;
            for (int i = 23; i >= 0; i--) {
                Spike spike = (Spike) board.spikes.elementAt(i);
                if (spike.getAmountOfPieces(player) > 0) {
                    pips += (j + 1) * spike.getAmountOfPieces(player);
                }
                j++;
            }
        }
        return pips;
    }

    // simply sets glow to true if the mouse is over the button
    // glow is a boolean used to make the button glow when pointer is over it.
    private void glowButton(int x, int y) {
        if (x >= buttonxA && x <= buttonxA + buttonwA) {
            if (y >= buttonyA && y <= buttonyA + buttonhA) {
                glowA = true;
            }
        }
        if (x >= buttonxB && x <= buttonxB + buttonwB) {
            if (y >= buttonyB && y <= buttonyB + buttonhB) {
                glowB = true;
            }
        }

        if (glowA || glowB) {
            glowCounter += GLOW_INCREMENTER;
            glowCounter = Math.min(glowCounter, 355);
        }
    }

    // this deals with touching the 'virtual' buttons
    // a mouse event is passed in to grab the x,y values from
    private boolean touchedButton(int x, int y) {
        buttonPressed = false;
        switch (state) {
            ///////////////////////////////////////////
            case OPTIONS_SCREEN_LOCAL_OR_NETWORK:
                //check buttons (local player, network play)
                checkAndDealWithTopButtonPressed_localplay(x, y);
                checkAndDealWithBotButtonPressed_networkplay(x, y);
                break;
            //////////////////////////////////////
            case OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN:
                //check buttons (computer player, human player)
                checkAndDealWithTopButtonPressed_computerPlayer(x, y);
                checkAndDealWithTopButtonPressed_humanPlayer(x, y);
                break;
            //////////////////////////////////////
            case GAME_IN_PROGRESS:
                //CHECK IF ROLL DICE BUTTON WAS PRESSED AND DEAL WITH IT////////
                if (showRollButton) {
                    checkAndDealWithRollDiceButton(x, y);
                } else {
                    //ROBOT LOOK FOR RELEVANT SPIKES..
                }
                //Other in game buttons go here, like double up, resign etc.
                break;
        }
        return buttonPressed;
    }

    //works out if the bottom button is pressed (in this state the 'computer player' button)
    //and deals with it
    private void checkAndDealWithTopButtonPressed_computerPlayer(int x, int y) {
        if (x >= buttonxA && x <= buttonxA + buttonwA) {
            if (y >= buttonyA && y <= buttonyA + buttonhA) {
                log("Selected COMPUTER on OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN");
                buttonPressed = true;
                Board.HUMAN_VS_COMPUTER = true;
                Bot.dead = false;
                log("CPU OPPONENT PRIMED.");
                startGame();
                if (typeOfPlay == LOCAL_PLAY) {
                    log("Selected LOCAL play against CPU");
                } else {
                    log("Selected NETWORK play against CPU");
                }
            }
        }
    }

    //works out if the bottom button is pressed (in this state the 'human player' button)
    //and deals with it
    private void checkAndDealWithTopButtonPressed_humanPlayer(int x,int y) {
        //check if bottom button is pressed (human player)
        if (x >= buttonxB && x <= buttonxB + buttonwB) {
            if (y >= buttonyB && y <= buttonyB + buttonhB) {
                log("Selected HUMAN on OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN");
                buttonPressed = true;
                startGame();
                Board.HUMAN_VS_COMPUTER = false;
                log("THE WEAKLING WOULD RATHER FACE A HUMAN.");
                if (typeOfPlay == LOCAL_PLAY) {
                    log("Selected LOCAL play against HUMAN");
                } else {
                    log("Selected NETWORK play against HUMAN");
                }
            }
        }
    }

     //works out if the bottom button is pressed (in this state the 'network play' button)
     //and deals with it
    private void checkAndDealWithBotButtonPressed_networkplay(int x, int y) {
        //check if bottom button is pressed (NETWORK)
        if (x >= buttonxB && x <= buttonxB + buttonwB) {
            if (y >= buttonyB && y <= buttonyB + buttonhB) {
                log("Selected NETWORK PLAY on OPTIONS_SCREEN_LOCAL_OR_NETWORK");
                typeOfPlay = NETWORK_PLAY;
                buttonPressed = true;
                state = NETWORKING_ENTER_NAME;
            }
        }
    }

    //works out if the top button is pressed (in this state the 'local play' button)
    //and deals with it
    private void checkAndDealWithTopButtonPressed_localplay(int x, int y) {
        if (x >= buttonxA && x <= buttonxA + buttonwA) {
            if (y >= buttonyA && y <= buttonyA + buttonhA) {
                log("Selected LOCAL PLAY on OPTIONS_SCREEN_LOCAL_OR_NETWORK");
                typeOfPlay = LOCAL_PLAY;
                buttonPressed = true;
                state = OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN;
            }
        }
    }
  
    //detects if the roll dice button has been pressed, and if so, reacts
    //accordingly
    private void checkAndDealWithRollDiceButton(int x, int y) {
        if (x >= rollButtonX && x <= rollButtonX + rollButtonW) {
            if (y >= rollButtonY && y <= rollButtonY + rollButtonH) {
                log("Roll Dice button clicked.");
                Board.die1HasBeenUsed = false;
                Board.die2HasBeenUsed = false;
                showDice = true; // show the die now theyve clicked roll.
                sfxDiceRoll.playSound();
                dealWithOrdinaryRolls();
                buttonPressed = true; // just to print out to us it was pressed.
            }
        }
    }
 
    //deals with an ordinary roll, that is sets the 2 die values to new random ones
    private void dealWithOrdinaryRolls() {
        log("----------- dealWithOrdinaryRolls -----------");
        if (Board.whoseTurnIsIt == Player.WHITE) {
            log("white will roll both die now.");
            // note we pass in null in here which tells it to roll both die for us directly
            playerRolls(Player.WHITE);
        } else if (Board.whoseTurnIsIt == Player.BLACK) {
            log("black will roll both die now.");
            // note we pass in null in here which tells it to roll both die for us directly
            playerRolls(Player.BLACK);
        } else {
            Utils.log("dealWithOrdinaryRolls does not know whoseTurnIsIt!");
        }
    }

     // deals with a player rolling a dice, accepts an int representing either
     // BLACK or WHITE, die is the die which the player should roll.
     // note that:
     // if Die is null it means that its an ordinary roll (not an opening roll) and we simply do 2 rolls for that player
     // accessing the dice objects directly, since we really want them to roll simulatenously so to speak
    private void playerRolls(int player) {
        String playerStr = player == Player.WHITE ? "White" : "Black";
        int val = board.die1.roll();
        D1lastDieRoll_toSendOverNetwork = val;
        GameNetworkClient.SENDCLICK_AND_DIEVALUE1 = true; // tells it to send a click over network
        int val2 = board.die2.roll();
        D2lastDieRoll_toSendOverNetwork = val2;
        GameNetworkClient.SENDCLICK_AND_DIEVALUE2 = true; // tells it to send a click over network
        log(String.format("####################################%s rolled:%d, %d", playerStr, val, val2));
        tellPlayers(String.format("%s rolled:%d-%d", playerStr, val, val2));

        if (val == val2) {
            log(String.format("%s Double!", playerStr));
            tellPlayers(String.format("%s rolled:%d-%d (Double)", playerStr, val, val2));
            someoneRolledADouble = true;
            doubleRollCounter = 0;
            sfxDoubleRolled.playSound();
        }
        showRollButton = false; // dont show it now theyve just rolled.
        board.calculatePotentialNumberOfMoves = true;
    }

    //clears the potential spikes used for highlighting possible moves,
    //once cleared they are recreated as needed.
    private static void clearPotentialSpikes() {
        log("clearPotentialSpikes");
        //Clear all the copy spikes so the valid options vanish
        Board.copy_of_reachableFromDie1=null;
        Board.copy_of_reachableFromDie2=null;
        Board.copy_of_reachableFromBothDice=null;
    }

    public static boolean showDice;
    //this needs to be called when swapping turns form one player to another
    //to ensure things behave correctly.
    public static void resetVarsTurn() {
        log("resetVarsTurn");

        //so it doesnt think dice have been used anymore
        Board.die1HasBeenUsed = false;
        Board.die2HasBeenUsed = false;

        clearPotentialSpikes();

        //make sure roll button gets redrawn
        showRollButton = true;
        showDice = false; // dont draw til the next player clicks roll.

        Board.calculatePotentialNumberOfMoves = true; // so they get calc'd at start of each go.
        someoneRolledADouble = false;
        doubleRollCounter = 0;
    }

    private void paint_OPTIONS_SCREEN_LOCAL_OR_NETWORK(Graphics g, String buttonAstr, String buttonBstr, String question) {
        String printme = question;
        int widthOfPrintMe;
        int xposTmp;
        int ypos = (getHeight() / 2) - fontblack.getHeight() * 5;

        widthOfPrintMe = (fontblack.stringWidth(printme));
        xposTmp = (getWidth() / 2) - ((widthOfPrintMe / 2));
        fontblack.drawString(g, printme, xposTmp, ypos + 1, 0);
        ypos += fontblack.getHeight() * 2;
        printme = buttonAstr;

        widthOfPrintMe = fontblack.stringWidth(printme);
        xposTmp = (getWidth() / 2) - ((widthOfPrintMe / 2));

        //make button glow if pointer is over it
        if (glowA) {
            if (glowCounter < 255) {
                utils.setColor(g, new Color(glowCounter, 0, 0));
            } else {
                utils.setColor(g, new Color(255, 0, 0));
            }
            utils.fillRoundRect(g, xposTmp - 10, ypos, widthOfPrintMe + 20, (fontblack.getHeight()));
            glowA = false;
        }
        utils.setColor(g, Color.black);
        utils.drawRoundRect(g, xposTmp - 10, ypos, widthOfPrintMe + 20, (fontblack.getHeight()));

        robotMoveDesc = "Bot Loaded. Answer: (" + question + ") ";
        Board.setBotDestination((xposTmp - 10) + (widthOfPrintMe + 20) / 2, ypos + (fontblack.getHeight() / 2),
            "Bot Loaded. Answer: (" + question + ") ");
        tellRobot(false, robotMoveDesc);

        /////for collision of button
        buttonxA = xposTmp - 10;
        buttonyA = ypos;
        buttonwA = widthOfPrintMe + 20;
        buttonhA = (fontblack.getHeight());
        if (showBoundaryBoxes) {
            utils.setColor(g, Color.red);
            utils.drawRect(g, buttonxA, buttonyA, buttonwA, buttonhA);
        }
        fontblack.drawString(g, printme, xposTmp, ypos + 1, 0);
        printme = "or";
        ypos += (fontblack.getHeight() * 2);
        widthOfPrintMe = (fontblack.stringWidth(printme));
        xposTmp = (getWidth() / 2) - ((widthOfPrintMe / 2));
        fontblack.drawString(g, printme, xposTmp, ypos + 1, 0);

        ///////// 'network' button
        printme = buttonBstr; // "Network Play";
        widthOfPrintMe = (fontblack.stringWidth(printme));
        xposTmp = (getWidth() / 2) - ((widthOfPrintMe / 2));
        utils.setColor(g, Color.BLACK);
        ypos += fontblack.getHeight() * 2;

        //make button glow if pointer is over it
        if (glowB) {
            if (glowCounter < 255) {
                utils.setColor(g, new Color(0, 0, glowCounter));
            } else {
                utils.setColor(g, new Color(0, 0, 255));
            }

            utils.fillRoundRect(g, xposTmp - 10, ypos, widthOfPrintMe + 20, (fontblack.getHeight()));
            glowB = false;
        }
        utils.setColor(g, Color.black);
        utils.drawRoundRect(g, xposTmp - 10, ypos, widthOfPrintMe + 20, (fontblack.getHeight()));
        fontblack.drawString(g, printme, xposTmp, ypos + 1, 0);

        /////for collision of button
        buttonxB = xposTmp - 10;
        buttonyB = ypos;
        buttonwB = widthOfPrintMe + 20;
        buttonhB = (fontblack.getHeight());
        if (showBoundaryBoxes) {
            utils.setColor(g, Color.red);
            utils.drawRect(g, buttonxB, buttonyB, buttonwB, buttonhB);
        }
        //draw a little version of the logo in the bottom right
        utils.drawImage(g, splashScreenLogoSmall, getWidth() - ((splashScreenLogoSmall.getWidth(this) / 2) + 20),
            getHeight() - splashScreenLogoSmall.getHeight(this), this);
    }

    private void paint_NETWORKING_ENTER_NAME(Graphics g) {
        String printme="Enter your name:";
        int xposTmp=0;
        int ypos =(getHeight()/2)-fontblack.getHeight()*5;

        int widthOfPrintMe = fontblack.stringWidth(printme);
        xposTmp=(getWidth()/2)-((widthOfPrintMe/2));
        fontblack.drawString(g, printme, xposTmp , ypos+1, 0);
        ypos+=fontblack.getHeight()*2;


        printme="Enter your name:";
        xposTmp=(getWidth()/2)-((widthOfPrintMe/2));
        utils.drawRect(g, xposTmp, ypos, fontblack.stringWidth(printme), fontblack.getHeight());

        printme=NetworkChatClient.nick;
        widthOfPrintMe=(fontblack.stringWidth(printme));
        xposTmp=(getWidth()/2)-((widthOfPrintMe/2));
        fontblack.drawString(g, printme, xposTmp , ypos+1, 0);
    }

    private void paint_NETWORKING_LOBBY(Graphics g) {
        TRANSPARENCY_LEVEL=255;
        utils.setColor(g, 0,0,0,TRANSPARENCY_LEVEL);
        utils.fillRect(g,0,0,getWidth(),getHeight());
        int SMALLGAP=5;

        x=SMALLGAP;
        y=SMALLGAP+fontblack.getHeight()*2;

        int BORDER=10;
        int WIDTH_OF_MESSAGE_TEXT  = ((getWidth()-getWidth()/6)-BORDER);
        int HEIGHT_OF_MESSAGE_TEXT = ((getHeight()-getHeight()/8)-(BORDER+y+SMALLGAP))+2;
        int WIDTH_OF_USERLIST  = getWidth()-(WIDTH_OF_MESSAGE_TEXT+BORDER+SMALLGAP);
        int HEIGHT_OF_USERLIST = HEIGHT_OF_MESSAGE_TEXT+1;
        int WIDTH_OF_ENTERTEXT_BOX=WIDTH_OF_MESSAGE_TEXT;
        int HEIGHT_OF_ENTERTEXT_BOX=getHeight()-(HEIGHT_OF_MESSAGE_TEXT+BORDER+y+SMALLGAP);

        int HEIGHT_OF_TOPIC_AND_NEWS_BOX=getHeight()-(HEIGHT_OF_MESSAGE_TEXT+HEIGHT_OF_ENTERTEXT_BOX+SMALLGAP*5);
        y=SMALLGAP;

        ///////////////////////////////////////////

        x=x+WIDTH_OF_MESSAGE_TEXT+SMALLGAP;
        //info box top right (amount of users and ops)
        utils.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
        utils.fillRoundRect(g, x, y, WIDTH_OF_USERLIST, HEIGHT_OF_TOPIC_AND_NEWS_BOX);
        utils.setColor(g, OUTLINE_FOR_CHAT_BOXES);
        utils.drawRoundRect(g, x, y, WIDTH_OF_USERLIST, HEIGHT_OF_TOPIC_AND_NEWS_BOX);
        fontblack.drawString(g, ""+NetworkChatClient.userList.size()+" users", x+SMALLGAP , y+(SMALLGAP*2)-2, 0);
        ////////////////////////////////////////////

        x=SMALLGAP;
        y=SMALLGAP+fontblack.getHeight()*2;

        Shape s = g.getClip();
        g.setClip(x-2, y+3, WIDTH_OF_MESSAGE_TEXT+5, HEIGHT_OF_MESSAGE_TEXT);

        //message text
        utils.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
        utils.fillRoundRect(g, x, y, WIDTH_OF_MESSAGE_TEXT, HEIGHT_OF_MESSAGE_TEXT);
        utils.setColor(g, OUTLINE_FOR_CHAT_BOXES);
        utils.drawRoundRect(g, x, y, WIDTH_OF_MESSAGE_TEXT, HEIGHT_OF_MESSAGE_TEXT);

        int listY=y+SMALLGAP;
        int topofChatBox=listY;

        Enumeration e = null;
        if (NetworkChatClient.messageText!=null) {
         y=SMALLGAP+fontblack.getHeight()*2;
         y+=paraYoffset;//scrolls it
         e = NetworkChatClient.messageText.elements();
         flip=false;
         while (e.hasMoreElements()) {
             if (y>(getHeight()-topofChatBox-HEIGHT_OF_ENTERTEXT_BOX)+fontblack.getHeight()) {
                 paraYoffset--;//smooth scrolling
             }
             int ydiff=y;
             int yorig=y;
             String message = (String) e.nextElement();
             y=drawMeWrapped(g,x,y,message,fontblack,false,false,true,WIDTH_OF_ENTERTEXT_BOX-15,false);
             flip=!flip;
             if (flip)
             {
                 ydiff=y-ydiff;
                 //utils.setColor(g,0xFFFFFF);
                 utils.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
                 utils.fillRoundRect(g,x+1,yorig-1,WIDTH_OF_ENTERTEXT_BOX-1,ydiff);
                 y=yorig;
                y=drawMeWrapped(g,x,y,message,fontblack,false,false,true,WIDTH_OF_ENTERTEXT_BOX-15,false);
             }
             else
             {

                 ydiff=y-ydiff;
               //  utils.setColor(g,0xd5d5d5);
                 utils.setColor(g, 100,100,100,TRANSPARENCY_LEVEL);
                 utils.fillRoundRect(g,x+1,yorig-1,WIDTH_OF_ENTERTEXT_BOX-1,ydiff);
                 y=yorig;
                y=drawMeWrapped(g,x,y,message,fontblack,false,false,true,WIDTH_OF_ENTERTEXT_BOX-15,false);

             }
         }
        }
        g.setClip(s);

        x = SMALLGAP;
        y = SMALLGAP;

        // header for topic and live news/////////draw here so it covers over scrolled text from messages
        utils.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
        utils.fillRoundRect(g, x, y, WIDTH_OF_MESSAGE_TEXT, HEIGHT_OF_TOPIC_AND_NEWS_BOX);
        utils.setColor(g, OUTLINE_FOR_CHAT_BOXES);
        utils.drawRoundRect(g, x, y, WIDTH_OF_MESSAGE_TEXT, HEIGHT_OF_TOPIC_AND_NEWS_BOX);

        fontblack.drawString(g, NetworkChatClient.topic, x+SMALLGAP , y+(SMALLGAP*2)-2, 0);

        x += WIDTH_OF_MESSAGE_TEXT+SMALLGAP;
        y = 2+SMALLGAP+fontblack.getHeight()*2;

        utils.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
        utils.fillRoundRect(g, x, y, WIDTH_OF_USERLIST, HEIGHT_OF_USERLIST);
        utils.setColor(g, OUTLINE_FOR_CHAT_BOXES);
        utils.drawRoundRect(g, x, y, WIDTH_OF_USERLIST, HEIGHT_OF_USERLIST);

        listY = y + SMALLGAP;
        e = NetworkChatClient.userList.elements();
        playerPositions = new Vector();
        while (e.hasMoreElements()) {
         String user = (String) e.nextElement();
         int xval=0;
         if (user.equals("ChanServ")) {
             utils.drawImage(g, op,x+SMALLGAP+3 , listY+fontblack.getHeight()/2, this);
             xval=x+3+SMALLGAP*2;
             fontblack.drawString(g, user,  xval, listY, 0); listY+=fontblack.getHeight();
         } else if (user.equals("Admin")) {
             utils.drawImage(g, admin,x+SMALLGAP+3 , listY+fontblack.getHeight()/2, this);
             xval=x+3+SMALLGAP*2;
             fontblack.drawString(g, user, xval , listY, 0); listY+=fontblack.getHeight();
         } else {
             xval=x+SMALLGAP ;
            fontblack.drawString(g, user, xval, listY, 0); listY+=fontblack.getHeight();
         }
         //so we know if we clicked on one
         playerPositions.addElement(new PlayerPos(xval,listY-fontblack.getHeight(),fontblack.stringWidth(user),fontblack.getHeight(),user));
        }

        x = SMALLGAP;
        y += HEIGHT_OF_USERLIST + SMALLGAP + 2;

        //enter text box
        utils.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
        utils.fillRoundRect(g, x, y, WIDTH_OF_ENTERTEXT_BOX, HEIGHT_OF_ENTERTEXT_BOX);
        utils.setColor(g, OUTLINE_FOR_CHAT_BOXES);
        utils.drawRoundRect(g, x, y, WIDTH_OF_ENTERTEXT_BOX, HEIGHT_OF_ENTERTEXT_BOX);

        if (chatText != null) {
            drawMeWrapped(g,x,y,chatText,fontblack,false,false,true,WIDTH_OF_ENTERTEXT_BOX,false);
        }

        //// 2 buttons in bottom right
        x += WIDTH_OF_MESSAGE_TEXT + SMALLGAP;
        //button 1
        //utils.setColor(g, 0xFFFFFF);
        utils.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
        utils.fillRoundRect(g, x, y, WIDTH_OF_USERLIST, (HEIGHT_OF_ENTERTEXT_BOX-SMALLGAP)/2);
        utils.setColor(g, OUTLINE_FOR_CHAT_BOXES);
        utils.drawRoundRect(g, x, y, WIDTH_OF_USERLIST, (HEIGHT_OF_ENTERTEXT_BOX-SMALLGAP)/2);
        String printme="Options";
        fontblack.drawString(g, printme, (x)+(fontblack.stringWidth(printme)/2) , y+(SMALLGAP*2)-3, 0);

        y+=(HEIGHT_OF_ENTERTEXT_BOX+SMALLGAP)/2;
        //button 2
        //  utils.setColor(g, 0xFFFFFF);
        utils.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
        utils.fillRoundRect(g, x, y, WIDTH_OF_USERLIST, (HEIGHT_OF_ENTERTEXT_BOX-SMALLGAP)/2);
        utils.setColor(g, OUTLINE_FOR_CHAT_BOXES);
        utils.drawRoundRect(g, x, y, WIDTH_OF_USERLIST, (HEIGHT_OF_ENTERTEXT_BOX-SMALLGAP)/2);
        printme="Leave";
        fontblack.drawString(g, printme, (x)+15+(fontblack.stringWidth(printme)/2) , y+(SMALLGAP*2)-3, 0);
        /////////////////////////

        if (showChallengeWindow) {
            utils.setColor(g, 0, 0, 0, TRANSPARENCY_LEVEL);
            utils.fillRoundRect(g, WIDTH / 4, HEIGHT / 4, WIDTH / 2, HEIGHT / 2);
            utils.setColor(g, Color.white);
            utils.drawRoundRect(g, WIDTH / 4, HEIGHT / 4, WIDTH / 2, HEIGHT / 2);

            int xabout = WIDTH / 2;
            int yabout = (HEIGHT / 4) + TINY_GAP;

            //paint the about box
            printme = "Challenge this player?";
            fontwhite.drawString(g, printme, xabout - fontwhite.stringWidth(printme) / 2, yabout, 0);
            yabout += fontblack.getHeight();
            printme = personToChallenge;
            fontwhite.drawString(g, printme, xabout - fontwhite.stringWidth(printme) / 2, yabout, 0);
            yabout += fontblack.getHeight();
            printme = "IP:";
            fontwhite.drawString(g, printme, xabout - fontwhite.stringWidth(printme) / 2, yabout, 0);
        }
    }

    //paint helpers///////////////
    private void bg(Color col, Graphics g) {
        utils.backGround(g,col,WIDTH,HEIGHT);
    }

    // gets called each frame to repaint
    public void update(Graphics g) {
        paint(g);
    }

    // wrapper around system out to console
    private static void log(String s) {
        Utils.log("CustomCanvas{}:" + s);
    }

    /**
    * left click is used for everything, apart from cancelling which is done with
    * right button, for instance once a piece is stuck to the pointer right click
    * will return it to its original position (cancel the move)
    */
    //////// INPUT METHODS ////////////////
    @Override
    public void mouseClicked(MouseEvent e) {
        if (state == NETWORKING_ENTER_NAME) {
            if (e.getY() > getHeight() - 50) {
                NetworkChatClient.theURL = NetworkChatClient.localURL;
                System.out.println("swapped to local url");
            }
            return;
        }
        if (state == NETWORKING_LOBBY) {
            //check who they clicked on
            Enumeration ee = playerPositions.elements();
            while (ee.hasMoreElements()) {
                PlayerPos pos = (PlayerPos) ee.nextElement();
                if (e.getX() >= pos.x && e.getX() <= pos.x + pos.width &&
                    e.getY() >= pos.y && e.getY() <= pos.y + pos.height
                    ) {
                    log("Clicked on:" + pos.name);
                    showChallengeWindow = true;
                    personToChallenge = pos.name;
                    String theirIP = personToChallenge.substring(personToChallenge.indexOf("@") + 1, personToChallenge.length());
                    if (theirIP.isEmpty()) {
                        theirIP = SERVER_IP_ADDRESS;
                    }
                    log("IP TO CONNECT TO:" + theirIP);
                    client = new GameNetworkClient(this, theirIP);
                    Thread t = new Thread(client);
                    t.start();
                }
            }
            return;
        }

        //so our mouse doesnt influence anything
        if (Bot.getFullAutoPlay() || (!Bot.dead && Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.BLACK) ) {
        } else {
            log("mouseClicked "+e.getX()+","+e.getY());
            int buttonPressed=-1;
            if (e.getButton()==e.BUTTON1) {
                buttonPressed = LEFT_MOUSE_BUTTON;
            }
            if (e.getButton() == e.BUTTON3) {
                buttonPressed=RIGHT_MOUSE_BUTTON;
            }
            mouseClickedX(e.getX(), e.getY(), buttonPressed);
        }
    }

    public void mouseClickedX(int x, int y, int buttonPressed) {
        splashCounter = maxSplashCounter + 1; // turn off splash if its on
        if (buttonPressed == LEFT_MOUSE_BUTTON) {
            if (Board.gameComplete) {
                board.RESET_ENTIRE_GAME_VARS();
                RESET_ENTIRE_GAME_VARS();
                state=SPLASH_SCREEN;
            }
        }
        if (buttonPressed == RIGHT_MOUSE_BUTTON) {
            log("RIGHT BUTTON PRESSED");
            unstickPieceFromMouse();
            if (board != null) {
                board.calculatePotentialNumberOfMoves = true;
                board.thereAreOptions = false;
            }
            return; //do nothing else with right click
        }

        switch(state) {
            case OPTIONS_SCREEN_LOCAL_OR_NETWORK:
                touchedButton(x, y);
                return;
            case OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN:
                touchedButton(x, y);
                return;
            case GAME_IN_PROGRESS:
                touchedButton(x, y);

                if (showRollButton) {
                    log("respond to no clicks as the roll button is up");
                    //only thign that will respond is the about window
                    //brings up the about window.
                    checkIfPrefsButtonClickedOn(x, y);
                    return;
                }
                //Making a move//////////
                //to move a piece the player simply once clicks
                //on a piece they wish to move - then if it has valid moves -
                //it attaches to the mouse pointer and can be placed either
                //on one of the valid potential spikes- or returned to where it
                //was initially by right clicking (and no move has been used)
                checkIfPieceClickedOn(x, y);//detects what piece (if any was clicked on)

                //once a piece is stuck to the pointer, we place it on a spike
                //IFF that spike is one of its valid moves.

                checkIfSpikeClickedOn(x, y);//detects what spike (if any was clicked on)
                checkIfPieceContainerClickedOn(x, y);
                checkIfDoubleClickedOn(x, y);
                checkIfResignClickedOn(x, y);
                //if both dice used move to next turn
                if (Board.die1HasBeenUsed && Board.die2HasBeenUsed) {
                    log("GO TO NEW TURN AA");
                    turnOver();
                }
                return;
        }
    }

    private void RESET_ENTIRE_GAME_VARS() {
        Board.whoseTurnIsIt=Player.WHITE;
        someoneRolledADouble=false;
        doubleRollCounter=0;//this tracks how many rolls a player has had after rolling a double,
        showRollButton=true;//false when not needed
        resetVarsTurn();
        theBarWHITE = new Vector(4);//the bar holds pieces that get killed
        theBarBLACK = new Vector(4);//the bar holds pieces that get killed

        //these store the pieces that have been sent to the container, when all are in that player wins.
        whitePiecesSafelyInContainer=new Vector(15);
        blackPiecesSafelyInContainer=new Vector(15);

        originalSpikeForPieceSelected=null;
        barPieceStuckOnMouse = false;
        pieceOnMouse = false;//is true when a piece is stuck to mouse
        pieceStuckToMouse=null;//this is simply a copy of whatever piece (if any) is stuck to mouse

        message2Players = VERSION;
        board = null;
        Board.gameComplete = false;
        whiteResigned = false;
        blackResigned = false;

        //so it doesnt continuing playin on its own
        Board.HUMAN_VS_COMPUTER = false;
        Bot.dead = true;
        splashCounter = 0;
    }

    public void turnOver() {
        log("---- THIS TURN IS OVER ----");
        if (Board.whoseTurnIsIt == Player.WHITE) {
            Board.whoseTurnIsIt = Player.BLACK;
            log("BLACKS TURN");
            tellPlayers("Black's turn to roll.");
        } else {
            Board.whoseTurnIsIt = Player.WHITE;
            log("WHITES TURN");
            tellPlayers("White's turn to roll.");
        }
        resetVarsTurn(); // so a turn can start a fresh.
    }

    //this method will "unstick" a piece from the mouse by flagging the piece itself
    //as no longer stuck, it is used when right clicking to cancel a move
    //but also once a piece has actually been moved.
    private void unstickPieceFromMouse() {
        if (pieceStuckToMouse!=null) {
            pieceStuckToMouse.unstickFromMouse();
        }
        pieceOnMouse = false;
        barPieceStuckOnMouse=false;

        if (board == null) {
            log("board null");
            return;
        }
        board.SPtheMoveToMake=null; // reset the move to make once a move is made or right click
        board.calculatePotentialNumberOfMoves = true; // so they get calc'd at start of each go.
        Board.spikesAllowedToMoveToFromBar = new Vector(4); // RESET THIS HERE?
        pieceStuckToMouse = null;/////////////////////<-will this stop it stickign ot pointer?pieceStuckToMouse
    }

    //checks if the preferences button is pressed and deals with it if so
    private void checkIfPrefsButtonClickedOn(int x, int y) {
        int prefx = preferencesButtonX();
        int prefy = preferencesButtonY();
        if (x >= prefx && x <= prefx + prefw) {
            if (y >= prefy && y <= prefy + prefh) {
                log("Prefs button clicked.");
                INFO = !INFO;
                if (INFO) {
                    tellPlayers("About");
                }
            }
        }
    }

    private void checkIfDoubleClickedOn(int x,int y) {
        int myX = doubleX;
        int myY = doubleY;
        int myWidth = doubleWidth;
        int myHeight = doubleHeight;
        if (x >= myX && x < (myX + myWidth)) {
            if (y > myY && y < (myY + myHeight)) {
                log("DOUBLE CLICKED ON!");
                sfxdouble.playSound();
                if (Board.whoseTurnIsIt == Player.WHITE) {
                    tellPlayers("White just doubled.");
                } else {
                    tellPlayers("Black just doubled.");
                }
            }
        }
    }

    private void checkIfResignClickedOn(int x, int y) {
        int myX = resignX;
        int myY = resignY;
        int myWidth = resignWidth;
        int myHeight = resignHeight;
        if (x >= myX && x < (myX+myWidth)) {
            if (y>myY && y<(myY+myHeight)) {
                log("RESIGN CLICKED ON!");
                sfxResign.playSound();
                if (Board.whoseTurnIsIt==Player.WHITE) {
                    Board.gameComplete = true;
                    whiteResigned = true;
                    Board.gameCompleteString="White has resigned. Black has won!";
                } else {
                    Board.gameComplete=true;
                    blackResigned=true;
                    Board.gameCompleteString="Black has resigned. White has won!";
                }
            }
        }
    }

    private void checkIfPieceContainerClickedOn(int x,int y) {
        int myX = 0;
        int myY = 0;
        int myWidth = 0;
        int myHeight = 0;
        String clickedOnText = "NONE";
        if (Board.whoseTurnIsIt == Player.WHITE) {
            myX = whiteContainerX;
            myY = whiteContainerY;
            myWidth = whiteContainerWidth;
            myHeight = whiteContainerHeight;
            clickedOnText = "WHITE CONTAINER CLICKED ON";
        } else if (Board.whoseTurnIsIt == Player.BLACK) {
            myX = blackContainerX;
            myY = blackContainerY;
            myWidth = blackContainerWidth;
            myHeight = blackContainerHeight;
            clickedOnText = "BLACK CONTAINER CLICKED ON";
        } else {
            Utils._E("checkIfPieceContainerClickedOn knows not whom click upon it!");
        }

        if (x >= myX && x < (myX + myWidth)) {
            if (y > myY && y < (myY + myHeight)) {
                log("" + clickedOnText);
                //if container is yellow, ie it knows its a potential option, AND we have a piece on the mouse
                //then we simply let it go into the piece container.
                if (Board.pulsateWhiteContainer && pieceOnMouse && Board.whoseTurnIsIt == Player.WHITE) {
                    log("WHITE put in container");
                    //remove from spike
                    //add to container vector
                    int correctDie = Board.whichDieGetsUsToPieceContainer;
                    boolean pieceWillGoToContainer = true;
                    placePieceRemoveOldOneAndSetDieToUsed(correctDie, pieceWillGoToContainer);
                    //continue
                } else
                    //if container is yellow, ie it knows its a potential option, AND we have a piece on the mouse
                    //then we simply let it go into the piece container.
                    if (Board.pulsateBlackContainer && pieceOnMouse && Board.whoseTurnIsIt == Player.BLACK) {
                        log("BLACK put in container");
                        //remove from spike
                        //add to container vector
                        int correctDie = Board.whichDieGetsUsToPieceContainer;
                        boolean pieceWillGoToContainer = true;
                        placePieceRemoveOldOneAndSetDieToUsed(correctDie, pieceWillGoToContainer);
                        //continue
                    }
            }
        }
    }

    //indicates if this mouse click has been on a spike
    private void checkIfSpikeClickedOn(int x, int y) {
        // grab the spikes, loop thru them checking to
        // see if the user clicked on that spike
        if (board == null) {
            log("game not ready. (splash still up)");
            return;
        }

        Enumeration spikes_e = board.spikes.elements();
        while (spikes_e.hasMoreElements()) {
            Spike spike = (Spike) spikes_e.nextElement();
            if (spike.userClickedOnThis(x, y)) {
                log("Spike was clicked on (" + spike.getSpikeNumber() + ")");

                /* REMOVING A PIECE FROM ONE SPIKE AND ADDING IT TO ANOTHER.
                 * When the player has a piece stuck to their mouse pointer
                 * and the valid potential spikes are pulsating, we have copies
                 * of them valid spikes stored as
                 * board.copy_of_reachableFromDie1, board.copy_of_reachableFromDie2,
                 * and board.copy_of_reachableFromBothDice.
                 * So we compare the number of the spike they just clicked on to the
                 * potential spikes the piece can go to, if they match then we know the
                 * player has placed a piece from one spike to another spike. So we remove it
                 * from the initial spike and add it to the new one, as shown below:
                 */

                // find out if this is a valid spiek to go to from bar
                if (barPieceStuckOnMouse) {
                    log("barPieceStuckOnouse spikesAllowedToMoveToFromBar.size()" + Board.spikesAllowedToMoveToFromBar.size());
                    Enumeration e = Board.spikesAllowedToMoveToFromBar.elements();
                    while (e.hasMoreElements()) {

                        Spike sp = (Spike) e.nextElement();
                        log("checkign spike:" + sp.getSpikeNumber());
                        if (spike.getSpikeNumber() == sp.getSpikeNumber()) {
                            log("YES WE CAN DROP OFF AT THIS SPIKE " + sp.getSpikeNumber());
                            //remove piece from bar
                            if (Board.whoseTurnIsIt == Player.WHITE) {
                                log("WHITE PIECE REMOVED FROM BAR");
                                theBarWHITE.remove(pieceStuckToMouse);
                                //IF this spike contains an enemy piece Kill it
                                if (sp.getAmountOfPieces(Player.BLACK) == 1) {
                                    log("WHITE KILLED A BLACK WHILE GETTING OFF BAR");
                                    Piece piece = (Piece) sp.pieces.firstElement();
                                    theBarBLACK.add(piece);///add this piece to the bar
                                    sp.removePiece(piece); //and remove from spike
                                    sfxKilled.playSound();

                                }
                            }
                            if (Board.whoseTurnIsIt == Player.BLACK) {
                                log("BLACK PIECE REMOVED FROM BAR");
                                theBarBLACK.remove(pieceStuckToMouse);

                                //IF this spike contains an enemy piece Kill it
                                if (sp.getAmountOfPieces(Player.WHITE) == 1) {
                                    Piece piece = (Piece) sp.pieces.firstElement();
                                    theBarWHITE.add(piece);///add this piece to the bar
                                    log("BLACK KILLED A WHITE WHILE GETTING OFF BAR");
                                    sp.removePiece((Piece) sp.pieces.firstElement());
                                    sfxKilled.playSound();
                                }
                            }
                            log("PLACED ON SPIKE");
                            //add it to the spike clicked on
                            sp.addPiece(pieceStuckToMouse);
                            //and make sure nothing is stuck to mouse by finalising move like this
                            log("UNSTUCK");
                            unstickPieceFromMouse();
                            // USE UP THE CORRECT DIE
                            Die theDieThatGotUsHere = sp.get_stored_die();

                            // Here we check if both dice have been used so we can move onto next players turn:
                            //UNLESS someone rolled a double
                            if (someoneRolledADouble && doubleRollCounter <= 3) {
                                log("Player is still enjoying his double round so dont move on. y");
                                board.calculatePotentialNumberOfMoves = true;//so they get calc'd at start of each go.
                                log("DONT USE UP DICE SINCE ITS A DOUBLE XXX");
                            } else {
                                if (theDieThatGotUsHere.getValue() == Board.die1.getValue()) {
                                    log("DIE1 USED GETTING OFF BAR " + Board.die1.getValue());
                                    Board.die1HasBeenUsed = true;

                                } else {
                                    log("DIE2 USED GETTING OFF BAR " + Board.die2.getValue());
                                    Board.die2HasBeenUsed = true;
                                }
                                log("CORRECT DIE USED UP.");
                            }
                            //done getting off bar

                            if (someoneRolledADouble) {
                                log("doubleRollCounter incremented!");
                                doubleRollCounter++;//increment this here to keep a track fi thi was a dbl
                            }
                        } else {
                            //log("NO WE CANT DROP OFF AT THIS SPIKE "+sp.getSpikeNumber());
                        }
                    }
                }

                //DIE1 MOVE
                if (pieceStuckToMouse != null && board.copy_of_reachableFromDie1 != null && spike.getSpikeNumber() == board.copy_of_reachableFromDie1.getSpikeNumber()) {
                    log("clicked on valid potential spike (die1)");
                    placePieceRemoveOldOneAndSetDieToUsed(1, false);
                    return;//EXPERMINETAL so it doesnt do any more checks since we are using this die
                }

                //DIE2 MOVE
                if (pieceStuckToMouse != null && board.copy_of_reachableFromDie2 != null && spike.getSpikeNumber() == board.copy_of_reachableFromDie2.getSpikeNumber()) {
                    log("clicked on valid potential spike (die2)");
                    placePieceRemoveOldOneAndSetDieToUsed(2, false);
                    return;//EXPERMINETAL so it doesnt do any more checks since we are using this die
                }

                //DIE1 + DIE2 MOVE
                if (pieceStuckToMouse != null && board.copy_of_reachableFromBothDice != null && spike.getSpikeNumber() == board.copy_of_reachableFromBothDice.getSpikeNumber()) {
                    log("clicked on valid potential spike (die1+die2)");
                    placePieceRemoveOldOneAndSetDieToUsed(3, false);
                }
            }
        }
    }

    // removes piece from the spike it came from, adds it to the new one just clicked on, and sets the die that did this to used
    // dieToSetUnused requires 1 or 2 (representing die 1 or die 2), OR 3 (3 IS BOTH DICE)
    //pieceWillGoToContainer is used ONLY when we are removig a piece from a spike and then adding it to the PIECE CONTAINER, in all other
    //situations its simply removing from one spike and adding to another
    private void placePieceRemoveOldOneAndSetDieToUsed(int dieToSetUnused, boolean pieceWillGoToContainer) {
        log("placePieceRemoveOldOneAndSetDieToUsed dieToSetUnused:" + dieToSetUnused);
        if (pieceStuckToMouse == null) {
            Utils._E("pieceStuckToMouse was null somehow");
        }
        //remove piece from its current spike
        originalSpikeForPieceSelected.removePiece(pieceStuckToMouse);
        if (dieToSetUnused == 1) {
            if (pieceWillGoToContainer) {
                if (Board.whoseTurnIsIt == Player.WHITE) {
                    whitePiecesSafelyInContainer.add(pieceStuckToMouse);
                    log("blackPiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:" + whitePiecesSafelyInContainer.size());
                    sfxPutPieceInContainer.playSound();
                } else if (Board.whoseTurnIsIt == Player.BLACK) {
                    blackPiecesSafelyInContainer.add(pieceStuckToMouse);
                    log("blackPiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:" + whitePiecesSafelyInContainer.size());
                    sfxPutPieceInContainer.playSound();
                } else {
                    Utils._E("whoseTurnIsIt is invalid here.");
                }
            } else {
                //// SPECIAL CONDITION - WAS A PIECE KILLED?////////////////////
                if (Board.whoseTurnIsIt == Player.WHITE && board.copy_of_reachableFromDie1.getAmountOfPieces(Player.BLACK) > 0) {
                    log("WHITE KILLED A BLACK");
                    Piece firstPiece = (Piece) board.copy_of_reachableFromDie1.pieces.firstElement();
                    board.copy_of_reachableFromDie1.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromDie1.addPiece(pieceStuckToMouse);
                    theBarBLACK.add(firstPiece); // add it to the BAR
                    sfxKilled.playSound();
                } else if (Board.whoseTurnIsIt == Player.BLACK && board.copy_of_reachableFromDie1.getAmountOfPieces(Player.WHITE) > 0) {
                    log("BLACK KILLED A WHITE");
                    Piece firstPiece = (Piece) board.copy_of_reachableFromDie1.pieces.firstElement();
                    board.copy_of_reachableFromDie1.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromDie1.addPiece(pieceStuckToMouse);
                    theBarWHITE.add(firstPiece); // add it to the BAR
                    sfxKilled.playSound();
                } else {
                    //NORMAL CONDITION
                    //add it to the spike user just clicked on
                    board.copy_of_reachableFromDie1.addPiece(pieceStuckToMouse);
                }
            }
            //so player cant use die one again
            //(and it wont come up as a potential valid option)
            Board.die1HasBeenUsed = true;
            log("die1HasBeenUsed A.");
        } else if (dieToSetUnused == 2) {
            if (pieceWillGoToContainer) {
                if (Board.whoseTurnIsIt == Player.WHITE) {
                    whitePiecesSafelyInContainer.add(pieceStuckToMouse);
                    log("whitePiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:" + whitePiecesSafelyInContainer.size());
                    sfxPutPieceInContainer.playSound();
                } else if (Board.whoseTurnIsIt == Player.BLACK) {
                    blackPiecesSafelyInContainer.add(pieceStuckToMouse);
                    log("blackPiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:" + whitePiecesSafelyInContainer.size());
                    sfxPutPieceInContainer.playSound();
                } else {
                    Utils._E("whoseTurnIsIt is invalid here.");
                }
            } else {
                //// SPECIAL CONDITION - WAS A PIECE KILLED?////////////////////
                if (Board.whoseTurnIsIt == Player.WHITE && board.copy_of_reachableFromDie2.getAmountOfPieces(Player.BLACK) > 0) {

                    log("WHITE KILLED A BLACK");
                    Piece firstPiece = (Piece) board.copy_of_reachableFromDie2.pieces.firstElement();
                    board.copy_of_reachableFromDie2.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromDie2.addPiece(pieceStuckToMouse);
                    theBarBLACK.add(firstPiece); // add it to the BAR
                    sfxKilled.playSound();

                } else if (Board.whoseTurnIsIt == Player.BLACK && board.copy_of_reachableFromDie2.getAmountOfPieces(Player.WHITE) > 0) {
                    log("BLACK KILLED A WHITE");
                    Piece firstPiece = (Piece) board.copy_of_reachableFromDie2.pieces.firstElement();
                    board.copy_of_reachableFromDie2.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromDie2.addPiece(pieceStuckToMouse);
                    theBarWHITE.add(firstPiece); // add it to the BAR
                    sfxKilled.playSound();

                } else {
                    //NORMAL CONDITION
                    //add it to the spike user just clicked on
                    board.copy_of_reachableFromDie2.addPiece(pieceStuckToMouse);
                }
            }
            //so player cant use die one again
            //(and it wont come up as a potential valid option)
            Board.die2HasBeenUsed = true;
            log("die2HasBeenUsed AA.");
        } else if (dieToSetUnused == 3) {
            if (pieceWillGoToContainer) {
                if (Board.whoseTurnIsIt == Player.WHITE) {
                    whitePiecesSafelyInContainer.add(pieceStuckToMouse);
                    log("blackPiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:" + whitePiecesSafelyInContainer.size());
                    sfxPutPieceInContainer.playSound();
                } else if (Board.whoseTurnIsIt == Player.BLACK) {
                    blackPiecesSafelyInContainer.add(pieceStuckToMouse);
                    log("blackPiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:" + whitePiecesSafelyInContainer.size());
                    sfxPutPieceInContainer.playSound();
                } else {
                    Utils._E("whoseTurnIsIt is invalid here.");
                }
            } else {
                //// SPECIAL CONDITION - WAS A PIECE KILLED?////////////////////
                if (Board.whoseTurnIsIt == Player.WHITE && board.copy_of_reachableFromBothDice.getAmountOfPieces(Player.BLACK) > 0) {
                    log("WHITE KILLED A BLACK");
                    Piece firstPiece = (Piece) board.copy_of_reachableFromBothDice.pieces.firstElement();
                    board.copy_of_reachableFromBothDice.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromBothDice.addPiece(pieceStuckToMouse);
                    theBarBLACK.add(firstPiece); // add it to the BAR
                    sfxKilled.playSound();

                } else if (Board.whoseTurnIsIt == Player.BLACK && board.copy_of_reachableFromBothDice.getAmountOfPieces(Player.WHITE) > 0) {
                    log("BLACK KILLED A WHITE");
                    Piece firstPiece = (Piece) board.copy_of_reachableFromBothDice.pieces.firstElement();
                    board.copy_of_reachableFromBothDice.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromBothDice.addPiece(pieceStuckToMouse);
                    theBarWHITE.add(firstPiece); // add it to the BAR
                    sfxKilled.playSound();

                } else {
                    //NORMAL CONDITION
                    //add it to the spike user just clicked on
                    board.copy_of_reachableFromBothDice.addPiece(pieceStuckToMouse);
                }
            }
            //so player cant use die one OR die two again
            //(and it wont come up as a potential valid option)
            Board.die1HasBeenUsed = true;
            Board.die2HasBeenUsed = true;

            log("die1HasBeenUsed B.");
            log("die2HasBeenUsed B.");
        } else {
            Utils._E("ERROR CANT TELL WHICH DICE TO SET AS UNUSED. dieToSetUnused:" + dieToSetUnused);
        }
        //and make sure nothing is stuck to mouse by finalising move like this
        unstickPieceFromMouse();
        if (someoneRolledADouble) {
            // this logic was hard to understand when mixed so i duplicated it here due to the subtle diffs
            switch (dieToSetUnused) {
                case 1:
                    doubleRollCounter++;
                    log("someoneRolledADouble DIE 1 doubleRollCounter:" + doubleRollCounter);
                    if (doubleRollCounter <= 1) {
                        log("dont hide die yet as it was a double");
                        Board.die1HasBeenUsed = false;// so it doesnt vanish
                    }
                    if (doubleRollCounter >= 4) {
                        log("double round done.1");
                        //ADDED TO FIX DOUBLES ISSUE 243PM JAN 21
                        Board.die1HasBeenUsed = true;//so they dont vanish
                        Board.die2HasBeenUsed = true;
                        someoneRolledADouble = false;
                    }
                    break;
                case 2:
                    doubleRollCounter++;
                    log("someoneRolledADouble DIE2 doubleRollCounter:" + doubleRollCounter);
                    if (doubleRollCounter <= 3) {
                        log("dont hide die yet as it was a double");
                        Board.die2HasBeenUsed = false;//so it doesnt vanish
                    }
                    if (doubleRollCounter >= 4) {
                        log("double round done.2");
                        //ADDED TO FIX DOUBLES ISSUE 243PM JAN 21
                        Board.die1HasBeenUsed = true;//so they dont vanish
                        Board.die2HasBeenUsed = true;
                        someoneRolledADouble = false;
                    }
                    break;
                case 3:
                    doubleRollCounter++;
                    doubleRollCounter++;//2 dice used in a roll like this
                    log("someoneRolledADouble BOTH DIE doubleRollCounter:" + doubleRollCounter);
                    log("dont hide die yet as it was a double");
                    Board.die1HasBeenUsed = false;//so they dont vanish
                    Board.die2HasBeenUsed = false;
                    if (doubleRollCounter >= 4) {
                        log("double round done.3");
                        Board.die1HasBeenUsed = true;//so they do vanish
                        log("die1HasBeenUsed C.");
                        Board.die2HasBeenUsed = true;
                        someoneRolledADouble = false;
                    }
                    break;
                default:
                    Utils._E("placePieceRemoveOldOneAndSetDieToUsed error in die number");
                    break;
            }
        }
    }

    //indicates if this mouse click has been on a piece
    private void checkIfPieceClickedOn(int x,int y) {
        if (pieceOnMouse) {
            //special case, if the player already has a piece stuck on the mouse dont let another
            //one go on, this causes an error in the game, best way is to simply leap out of
            //this method here if this is true
            log("pieceOnMouse special case ignore this piece click");
            return;
        }

        //check pieces on bar
        if (Board.pickingPieceUpFromBar) {
            Vector piecesOnTheBar = (Board.whoseTurnIsIt == Player.WHITE) ? theBarWHITE : theBarBLACK;
            Enumeration e = piecesOnTheBar.elements();
            while (e.hasMoreElements()) {
                Piece p = (Piece) e.nextElement();
                if (p.userClickedOnThis(x, y)) {
                    log("PIECE ON THE BAR CLICKED ON.");
                    p.stickToMouse();
                    pieceOnMouse = true;
                    barPieceStuckOnMouse = true;
                    pieceStuckToMouse = p;
                }
            }
        }

        //grab the spikes, loop thru them checking every single
        //piece to see if the user clicked on that piece
        Enumeration spikes_e = board.spikes.elements();
        while(spikes_e.hasMoreElements()) {
            Spike spike = (Spike) spikes_e.nextElement();
            Enumeration pieces_e = spike.pieces.elements();
            while(pieces_e.hasMoreElements()) {
                Piece piece = (Piece) pieces_e.nextElement();
                if (piece.userClickedOnThis(x, y)) {
                    // only allow picking up of OUR OWN pieces
                    // AND check if we already have a piece or not.
                    //this was a bug so hopefully fixed.
                    if (board.allowPieceToStickToMouse && piece.getColour()==Board.whoseTurnIsIt && !pieceOnMouse) {
                    //And it has potential moves (i.e. not pointless to pick up)
                        log("PICKED UP PIECE: "+Board.playerStr(piece.getColour()));
                        //if this piece has options then we allow it to stick to
                        //mouse, ie we allow player to pick it up..
                        piece.stickToMouse();
                        pieceOnMouse = true;
                        pieceStuckToMouse=piece;
                        originalSpikeForPieceSelected=spike;//keep a copy of this piece's original Spike (for removing the piece later if need be)
                    }
                    log("Piece was clicked on (" + piece + ") board.allowPieceToStickToMouse:" +
                        board.allowPieceToStickToMouse + " board.whoseTurnIsIt:" + board.whoseTurnIsIt);
                }
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        log("mousedragged");
    }

    public void mouseMoved(MouseEvent e) {
         //so our mouse doesnt influence anything
        if (Bot.getFullAutoPlay() || (!Bot.dead && Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.BLACK) ) {
            //log("mouse wont respond");
        } else {
            if (NETWORK_GAME_IN_PROCESS) {
                if ((I_AM_CLIENT && Board.whoseTurnIsIt == Player.WHITE) ||
                    (I_AM_SERVER && Board.whoseTurnIsIt == Player.BLACK)) {
                    pointerX = e.getX();
                    pointerY = e.getY();
                }
            } else {
                //stick these bck in as local play was broke without--
                pointerX = e.getX();
                pointerY = e.getY();
                Board.mouseHoverX = pointerX;
                Board.mouseHoverY = pointerY;
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        log("keyPressed");

        //TEXT ENTRY IN LOBBY
        if (state == NETWORKING_LOBBY) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                chatClient.send();
                chatText = "";
            }
            String letter = "" + e.getKeyChar();
            chatText += letter;
            return;
        }

        //////////////NAME ENTRY////////
        if (state == NETWORKING_ENTER_NAME) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                state = NETWORKING_LOBBY;
                chatClient = new NetworkChatClient(this);
            }
            if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_CAPS_LOCK)
                && NetworkChatClient.nick.length() > 0) {
                NetworkChatClient.nick = NetworkChatClient.nick.substring(0, NetworkChatClient.nick.length() - 1);
            } else {
                String letter = "" + e.getKeyChar();
                if (letter.equals(" ") || NetworkChatClient.nick.length() > 10) {
                } else {
                    NetworkChatClient.nick += letter;
                }
            }
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_F1) {
            ignoreRepaints = !ignoreRepaints;
            setIgnoreRepaint(ignoreRepaints);
            jFrame.setResizable(!ignoreRepaints);
            tellPlayers("F1 Pressed, ignoreRepaints is now " + ignoreRepaints);
            log("F1 Pressed, ignoreRepaints is now " + ignoreRepaints);
        }

        if (e.getKeyChar() == 'q' || e.getKeyChar() == 'Q') {//QUIT
            System.exit(0);
        }
        if (e.getKeyChar() == 'f' || e.getKeyChar() == 'F') {//QUIT
            Bot.setFullAutoPlay(!Bot.getFullAutoPlay());
            Board.HUMAN_VS_COMPUTER = !Board.HUMAN_VS_COMPUTER;
            Bot.dead = !Bot.getFullAutoPlay();
            log("Bot.dead:" + Bot.dead);
            paintRobotMessages = Bot.getFullAutoPlay();
            log("FULL_AUTO_PLAY:" + Bot.getFullAutoPlay());
            if (Bot.getFullAutoPlay()) {
                tellRobot(true, "Bot turned on.");
            } else {
                tellRobot(true, "Bot turned off.");
            }
        }

        if (e.getKeyChar() == 'p' || e.getKeyChar() == 'P') {//QUIT
            //PAUSE
            PAUSED = !PAUSED;
            Bot.dead = PAUSED;
            log("PAUSED:" + PAUSED);
        }

        if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {//QUIT
            //PAUSE
            SOUND_ON = !SOUND_ON;
            log("SOUND_ON:" + SOUND_ON);
        }

        if (DEBUG_CONSOLE && e.getKeyChar() == 'x' || e.getKeyChar() == 'X') {
            sfxError.playSound();
        }

        if (!RELEASE_BUILD && e.getKeyChar() == 'c' || e.getKeyChar() == 'C') {
            showBoundaryBoxes = !showBoundaryBoxes;
        }
        if (!RELEASE_BUILD && e.getKeyChar() == 'l' || e.getKeyChar() == 'L') {
            Utils.CANVAS_LOGGING = !Utils.CANVAS_LOGGING;
        }

        if (!RELEASE_BUILD && e.getKeyChar() == 'd' || e.getKeyChar() == 'D') {
            DEBUG_CONSOLE = !DEBUG_CONSOLE;
        }

        if (e.getKeyChar() == 't' || e.getKeyChar() == 'T') {
            theme++;
            setTheme(theme);
        }
        if (DEBUG_CONSOLE) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                log("UP");
                debugMenuPos--;
                if (debugMenuPos < 0)
                    debugMenuPos = 0;

            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                log("DOWN");
                debugMenuPos++;
                if (debugMenuPos > LAST_DEBUG_OPTION)
                    debugMenuPos = LAST_DEBUG_OPTION;

            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                log("LEFT");
                debugOptionChanged(DEBUGLEFT);
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                log("RIGHT");
                debugOptionChanged(DEBUGRIGHT);
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    private void setTheme(int theme_) {
        theme = theme_;
        log("theme:" + theme);
        if (theme > MAX_THEMES) {
            theme = DEFAULT;
        }

        switch (theme) {
            case DEFAULT:
                log("THEME SET TO DEFAULT");
                themeName = "default";
                if (!firstThemeSet)
                    tellPlayers("Theme set to " + themeName);
                themecolours = defaultms;
                break;
            case METALIC:
                log("THEME SET TO METALIC");
                themeName = "metalic";
                if (!firstThemeSet)
                    tellPlayers("Theme set to " + themeName);
                themecolours = metalic;
                break;
            case CLASSIC:
                log("THEME SET TO CLASSIC");
                themeName = "classic";
                if (!firstThemeSet)
                    tellPlayers("Theme set to " + themeName);
                themecolours = classic;
                break;
            case FUNNYMAN:
                log("THEME SET TO FUNNYMAN");
                themeName = "funnyman";
                if (!firstThemeSet)
                    tellPlayers("Theme set to " + themeName);
                themecolours = funnyman;
                break;
            case BUMBLEBEE:
                log("THEME SET TO BUMBLEBEE");
                themeName = "bumblebee";
                if (!firstThemeSet)
                    tellPlayers("Theme set to " + themeName);
                themecolours = bumblebee;
                break;
            default:
                Utils._E("theme is out of range!");
        }
        firstThemeSet = false;
        //assigns each colour from the one specified
        for (int i = 0; i < themecolours.length; i++) {
            switch (i) {
                case 0:
                    BACKGROUND_COLOUR = themecolours[i];
                    break;
                case 1:
                    PANEL_COLOUR = themecolours[i];
                    break;
                case 2:
                    ROLL_BUTTON_COLOUR = themecolours[i];
                    break;
                case 3:
                    Board.BOARD_COLOUR = themecolours[i];
                    break;
                case 4:
                    Board.BAR_COLOUR = themecolours[i];
                    break;
                case 5:
                    Spike.BLACK_SPIKE_COLOUR = themecolours[i];
                    break;
                case 6:
                    Spike.WHITE_SPIKE_COLOUR = themecolours[i];
                    break;
                case 7:
                    Piece.WHITE_PIECE_COLOUR = themecolours[i];
                    break;
                case 8:
                    Piece.BLACK_PIECE_COLOUR = themecolours[i];
                    break;
                case 9:
                    Piece.WHITE_PIECE_INNER_COLOUR = themecolours[i];
                    break;
                case 10:
                    Piece.BLACK_PIECE_INNER_COLOUR = themecolours[i];
                    break;
                case 11:
                    Die.DIE_COLOUR = themecolours[i];
                    break;
                case 12:
                    Die.DOT_COLOUR = themecolours[i];
                    break;
                default:
                    Utils._E("theme state error, should not exceed 12!");
            }
        }
        //force recreation of colour objects
        //we pass true into makeColourObjects to force them to remake themselves
        //with the new colour values and thus repaint with new theme
        makeColourObjects();
        Board.makeColourObjects(true);
        Spike.makeColourObjects(true);
        Piece.makeColourObjects(true);
        Die.makeColourObjects(true);
        log("Theme is loaded now and working.");
    }

    private void makeColourObjects() {
        panel_colour = new Color(PANEL_COLOUR);
        background_colour = new Color(BACKGROUND_COLOUR);
        roll_button_colour = new Color(ROLL_BUTTON_COLOUR);
    }

    // prepare the customfont
    private void loadCustomFonts() {
        if (fontwhite == null) {
            boolean land = false;
            int gap = 10;
            String path = "/";
            int GAP = 3;//REAL GAP VAL, REMOVE REDUNDANT ONES (TODO)- (lower value the bigger gap)
            try {
                utils.log("loading fonts:");
                fontwhite = CustomFont.getFont(utils.loadImage(path + "whitefont.png"),
                    CustomFont.SIZE_SMALL,
                    CustomFont.STYLE_PLAIN, land, 32, 93, GAP, gap, true, this);
                if (fontwhite == null) {
                    Utils._E("-- fontwhite image is null");
                }
                fontblack = CustomFont.getFont(utils.loadImage(path + "blackfont.png"),
                    CustomFont.SIZE_SMALL,
                    CustomFont.STYLE_PLAIN, land, 32, 93, GAP, gap, true, this);
                if (fontblack == null) {
                    Utils._E("-- fontblack image is null");
                }
            } catch (Exception e) {
                Utils._E("== error loading fonts " + e.getMessage());
            }
        } else {
            log("Fonts already pre-cached...");
        }
    }

    //for debugging, paints sytem.out to screen
    public void paintStringsToCanvas(Graphics g) {
        Enumeration e = Utils.systemOuts.elements();
        int x=3;
        int y=3;
        while (e.hasMoreElements()) {
             String printthis = (String)e.nextElement();
             fontwhite.drawString(g, printthis, x, y, 0);y+=fontwhite.getHeight();
        }
    }

    public void tellPlayers(String s) {
        playerMessageSetTimeLong = System.currentTimeMillis();
        message2Players = s;
    }

    public static void robotExplain(String s) {
         robotMessageSetTimeLong = System.currentTimeMillis();
         robotMoveDesc = s;
    }

    public void paintMessageToPlayers(Graphics g) {
        utils.setColor(g, 0, 0, 0, TRANSPARENCY_LEVEL);
        if (Board.gameComplete) {
            messageWidth = fontwhite.stringWidth(message2Players + "  ");
            messagex = (WIDTH / 2) - messageWidth / 2;
            messageHeight = fontwhite.getHeight();
            messagey = (HEIGHT / 2) - messageHeight / 2;

            utils.fillRoundRect(g, messagex, messagey, messageWidth, messageHeight);
            utils.setColor(g, Color.WHITE);
            utils.drawRoundRect(g, messagex, messagey, messageWidth, messageHeight);
            //draw message in middle of screen
            fontwhite.drawString(g, message2Players, messagex + 7, messagey + 1, 0);
        } else {
            messageWidth = fontwhite.stringWidth(message2Players + "  ");
            messagex = 10;
            messagey = HEIGHT - (fontwhite.getHeight() + TINY_GAP);
            messageHeight = fontwhite.getHeight();
            utils.fillRoundRect(g, messagex, messagey, messageWidth, messageHeight);
            utils.setColor(g, Color.WHITE);
            utils.drawRoundRect(g, messagex, messagey, messageWidth, messageHeight);
            //draw message in bottom left.
            fontwhite.drawString(g, message2Players, messagex + 7, messagey + 1, 0);
        }
    }

    public void paintRobotMessage(Graphics g) {
        utils.setColor(g, 0, 0, 0, TRANSPARENCY_LEVEL);
        messageWidth = fontwhite.stringWidth(robotMoveDesc + "  ");
        messagex = WIDTH - (messageWidth + 10);
        messagey = 10;//(fontwhite.getHeight()+TINY_GAP);
        messageHeight = fontwhite.getHeight();

        utils.fillRoundRect(g, messagex, messagey, messageWidth, messageHeight);
        utils.setColor(g, Color.RED);
        utils.drawRoundRect(g, messagex, messagey, messageWidth, messageHeight);
        fontwhite.drawString(g, robotMoveDesc, messagex + 7, messagey + 1, 0);
    }

    /////////////////////ADJUST COLOURS HERE ////////////////////////////
    //themes: specify colours for colour themes
    private static int themecolours[]; // this gets assigned in constructor

    // DEFAULT VALUES (ms xp backgammon colours)
    public static int defaultms[] = {
        BACKGROUND_COLOUR,
        PANEL_COLOUR,
        ROLL_BUTTON_COLOUR,
        Board.BOARD_COLOUR,
        Board.BAR_COLOUR,
        Spike.BLACK_SPIKE_COLOUR,
        Spike.WHITE_SPIKE_COLOUR,
        Piece.WHITE_PIECE_COLOUR,
        Piece.BLACK_PIECE_COLOUR,
        Piece.WHITE_PIECE_INNER_COLOUR,
        Piece.BLACK_PIECE_INNER_COLOUR,
        Die.DIE_COLOUR,
        Die.DOT_COLOUR
    };

    public static int metalic[] = {
        /*BACKGROUND_COLOUR*/               0xffffff,
        /*PANEL_COLOUR*/                    0x828284,
        /*ROLL_BUTTON_COLOUR*/              0xffffff,
        /*Board.BOARD_COLOUR*/              0x9b9b9b,
        /*Board.BAR_COLOUR*/                0x8b898c,
        /*Spike.BLACK_SPIKE_COLOUR*/        0xc7c8cd,
        /*Spike.WHITE_SPIKE_COLOUR*/        0xa3a4a8,
        /*Piece.WHITE_PIECE_COLOUR*/        0xedf0f5,
        /*Piece.BLACK_PIECE_COLOUR*/        0x1a1a22,
        /*Piece.WHITE_PIECE_INNER_COLOUR*/  0xffffff,
        /*Piece.BLACK_PIECE_INNER_COLOUR*/  0xffffff,
        /*Die.DIE_COLOUR*/                  0x807875,
        /*Die.DOT_COLOUR*/                  0xe5e0da
    };

    public static int classic[] = {
        /*BACKGROUND_COLOUR*/               0x2c632a,
        /*PANEL_COLOUR*/                    0x002001,
        /*ROLL_BUTTON_COLOUR*/              0xfe1e1c,
        /*Board.BOARD_COLOUR*/              0xf4ebca,
        /*Board.BAR_COLOUR*/                0x245223,
        /*Spike.BLACK_SPIKE_COLOUR*/        0x99643c,
        /*Spike.WHITE_SPIKE_COLOUR*/        0xed974c,
        /*Piece.WHITE_PIECE_COLOUR*/        0xfefbf2,
        /*Piece.BLACK_PIECE_COLOUR*/        0x363b3f,
        /*Piece.WHITE_PIECE_INNER_COLOUR*/  0xffffff,
        /*Piece.BLACK_PIECE_INNER_COLOUR*/  0xffffff,
        /*Die.DIE_COLOUR*/                  0xfe1e1c,
        /*Die.DOT_COLOUR*/                  0xfffdfe
    };


    public static int funnyman[] = {
        /*BACKGROUND_COLOUR*/               0x661913,
        /*PANEL_COLOUR*/                    0x210d0c,
        /*ROLL_BUTTON_COLOUR*/              0xffffff,
        /*Board.BOARD_COLOUR*/              0x9d581d,
        /*Board.BAR_COLOUR*/                0x490f0e,
        /*Spike.BLACK_SPIKE_COLOUR*/        0x290d0a,
        /*Spike.WHITE_SPIKE_COLOUR*/        0x6e1213,
        /*Piece.WHITE_PIECE_COLOUR*/        0x4e3113,
        /*Piece.BLACK_PIECE_COLOUR*/        0x841b25,
        /*Piece.WHITE_PIECE_INNER_COLOUR*/  0xffffff,
        /*Piece.BLACK_PIECE_INNER_COLOUR*/  0xffffff,
        /*Die.DIE_COLOUR*/                  0xffffff,
        /*Die.DOT_COLOUR*/                  0x791216
    };

    public static int bumblebee[] = {
        /*BACKGROUND_COLOUR*/               0x202427,
        /*PANEL_COLOUR*/                    0x3a3a3a,
        /*ROLL_BUTTON_COLOUR*/              0xe4ff00,
        /*Board.BOARD_COLOUR*/              0x50555b,
        /*Board.BAR_COLOUR*/                0x545454,
        /*Spike.BLACK_SPIKE_COLOUR*/        0x030504,
        /*Spike.WHITE_SPIKE_COLOUR*/        0xe4ff00,
        /*Piece.WHITE_PIECE_COLOUR*/        0xb1995d,
        /*Piece.BLACK_PIECE_COLOUR*/        0x404443,
        /*Piece.WHITE_PIECE_INNER_COLOUR*/  0xffffff,
        /*Piece.BLACK_PIECE_INNER_COLOUR*/  0xffffff,
        /*Die.DIE_COLOUR*/                  0x000000,
        /*Die.DOT_COLOUR*/                  0xe4ff00
    };

    ///ROBOT STUFF
    public static void tellRobot(boolean b, String s) {
        if (s != null) {
            robotExplain(s);
        }
    }

    private int drawMeWrapped(Graphics g,int x, int y, String wrapMe, CustomFont font, boolean backdrop,
                             boolean outline, boolean justifyleft, int width, boolean justifyRight) {
        if (wrapMe == null) {
            log("drawMeWrapped received a null string");
        }
        //////these texts need to be wrapped as they could be long
        Vector textLinesForWrappingTMP = separateTextNEW(wrapMe, width, getHeight(), font);

        int stringHeight = y;
        int Xtmp=x;
        for (int i = 0; i < textLinesForWrappingTMP.size(); i++) {
            if (stringHeight >= 0) {
                String printme = (String)textLinesForWrappingTMP.elementAt(i);
                if (justifyleft) {
                    Xtmp=x;
                } else if (justifyRight) {
                } else {
                    Xtmp=(WIDTH/2)-(font.stringWidth(printme)/2);
                }
                // this is a bit of a hack but a legacy form the custom font days
                // check if the end of the text is reached and control users ability to scroll with bools.
                // so we dont let them keep scrolling
                boolean allowScrollingDOWN = printme.indexOf(SPECIAL_END_SYMBOL) == -1;
                if (!allowScrollingDOWN) {
                    log("DONT ALLOW ASCROLL SINCE SPECIAL END SYMBOL DETECTED allowScrollingDOWN:" + allowScrollingDOWN);
                    //ok now remove the special end sybol so it doesnt print
                    printme=printme.substring(0,printme.indexOf(SPECIAL_END_SYMBOL));
                }
                fontblack.drawString( g,printme,Xtmp,stringHeight,0);
            }
            stringHeight+=(font.getHeight());//-5);
        }
        y = stringHeight;
        return y;
    }

    private static Vector separateTextNEW(String string, int width, int height, CustomFont font) {
        Vector lines = new Vector();
        String aline = "";
        StringTokenizer st = new StringTokenizer(string, " ");
        String s;
        while (st.hasMoreElements()) {
            s = st.nextToken(); // if its not null s failed to get used last time
            s = s.trim();
            if (s.equals("[p]") || s.equals("[br]") || s.equals("[br2]") || s.equals("[br][br]")) {
                //<p>"))
                // [p] [br] [br][br] ALL WORK LIKE HTMLS <P>
                // [br2] works like HTMLS <BR>
                lines.addElement(aline);
                if (!s.equals("[br2]")) {//dont add a empty line if its a br2
                    lines.addElement(" ");
                }
                aline = "";
            } else {
                if (font.stringWidth(aline + " " + s) < width) {
                    aline += s + " ";
                    s = null;
                } else {
                    lines.addElement(aline);
                    aline = "";
                }
                if (!st.hasMoreElements()) {
                    //if its finished just add last bit now to new line
                    if (s != null && (!s.equals("[p]") || s.equals("[br2]") || s.equals("[br]") || s.equals("[br][br]"))) {
                        lines.addElement(aline + "" + s);
                    } else {
                        lines.addElement(aline.trim());
                    }
                } else {
                    if (s != null) {
                        if (s.equals("[p]") || s.equals("[br]") || s.equals("[br2]") || s.equals("[br][br]")) {
                            lines.addElement(aline);
                            if (!s.equals("[br2]")) {//dont add a empty line if its a br2
                                lines.addElement(" ");
                            }
                            aline = "";
                        } else {
                            aline += "" + s + " ";
                        }
                    }
                }
            }
        }
        return lines;
    }

    private int preferencesButtonX() {
        return getWidth() - (Board.BORDER + prefw + TINY_GAP / 2);
    }

    private int preferencesButtonY() {
        return Board.BORDER;
    }

    void onHumanPlayerConnectedToServer() {
        log("Human player connected to server");
        Bot.dead = true;
        startGame();
        NETWORK_GAME_IN_PROCESS = true;
        I_AM_CLIENT = true;
        NetworkChatClient.KEEP_LOBBY_GOING = false;
        this.jFrame.setTitle(Main.frame.getTitle() + " Online game in progress. (Connected as client)");
    }

    void startGame() {
        int val = Utils.getRand(0, 999_999);
        String playerStr = "White";
        board.whoseTurnIsIt = Player.WHITE;
        if (val >= 500_000) {
            board.whoseTurnIsIt = Player.BLACK;
            playerStr = "Black";
        }
        tellPlayers(String.format("%s won the roll off", playerStr));
        state = GAME_IN_PROGRESS;
    }
}
