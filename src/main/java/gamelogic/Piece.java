package gamelogic;
import java.awt.Color;
import java.awt.Graphics;
import lowlevel.*;

public class Piece {

    // colour consts
    public static int WHITE_PIECE_COLOUR = 0xe4e4d8;
    public static int BLACK_PIECE_COLOUR = 0x612d00;
    public static int WHITE_PIECE_INNER_COLOUR = 0xc0c0c0;
    public static int BLACK_PIECE_INNER_COLOUR = 0x452402;
    private static Color white_piece_color, black_piece_color;

    public static int PIECE_DIAMETER = 0;
    
    // these variables (along with PIECE_DIAMETER) are used to work out if the player has clicked on the piece
    int collision_x;
    int collision_y;

    int colour;
    Utils utils = new Utils();
    private boolean stickToMouse;

    Piece(Player father) {
        if (father == null) {
            Utils._E("Piece was made with a null father");
        }
        String colstr = father.printColour();
        log("Piece made. father is "+colstr);
        colour = father.getColour();
        makeColourObjects(false);
    }

    public static void makeColourObjects(boolean forceRecreation) {
        if (white_piece_color==null || forceRecreation) {
            white_piece_color=new Color(WHITE_PIECE_COLOUR);
        }
        if (black_piece_color==null || forceRecreation) {
            black_piece_color=new Color(BLACK_PIECE_COLOUR);
        }
    }

    public int getColour()
    {
        return colour;
    }

    void drawPieceOnMouse(Graphics g, int x, int y)
    {
        paint( g,  x,  y);
    }

    public void paint(Graphics g, int x, int y) {
        PIECE_DIAMETER = Spike.TRIANGLE_HEIGHT/7;
        if (colour == Player.WHITE) {
            utils.setColor(g, white_piece_color);
        } else if (colour == Player.BLACK) {
            utils.setColor(g, black_piece_color);
        } else {
            Utils._E("Dave, Im a bit worried that this piece doesnt know what colour it is.");
        }

        if (stickToMouse) {
            x = Board.mouseHoverX;
            y = Board.mouseHoverY;
        }
        utils.fillCircle(g,x, y,PIECE_DIAMETER, PIECE_DIAMETER);
        utils.setColor(g,Color.BLACK);
        utils.drawCircle(g,x, y,PIECE_DIAMETER, PIECE_DIAMETER);

        collision_x = x;
        collision_y = y;
        if (CustomCanvas.showBoundaryBoxes) {
            utils.setColor(g,Color.RED);
            utils.drawRect(g,collision_x, collision_y,PIECE_DIAMETER, PIECE_DIAMETER);
        }
    }

    //this is called to tell the piece to use the x, y vals from the mouse
    //instead of its usual ones as the user is placing it and it needs to stick to the
    //mouse point until they do place it.
    public void stickToMouse()
    {
        stickToMouse=true;
    }
    
    public void unstickFromMouse() {
        stickToMouse=false;
    }

    //returns true if the x,y passed in (from a mouse click) are within the
    //boundaries of this piece, ie user clicked on this piece
    public boolean userClickedOnThis(int mouseX, int mouseY) {
       return (mouseX >= collision_x && mouseX <= collision_x + PIECE_DIAMETER)
        && (mouseY >= collision_y && mouseY <= collision_y + PIECE_DIAMETER);
    }

    private void log(String s) {
        Utils.log("Piece{}:" + s);
    }
}
