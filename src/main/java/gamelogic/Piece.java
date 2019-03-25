/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gamelogic;
import java.awt.Color;
import java.awt.Graphics;
import lowlevel.*;
/**
 *
 * @author Gaz
 */
public class Piece {

    //colour consts
    public static int WHITE_PIECE_COLOUR=0xe4e4d8;
    public static int BLACK_PIECE_COLOUR=0x612d00;
    public static int WHITE_PIECE_INNER_COLOUR=0xc0c0c0;
    public static int BLACK_PIECE_INNER_COLOUR=0x452402;
    public static Color white_piece_inner_color, black_piece_inner_color;
    public static Color white_piece_color, black_piece_color;

    boolean showCollisions;
    public static int PIECE_DIAMETER=0;
    
    // these variables (along with PIECE_DIAMETER) are used to work out if the player has clicked on the piece
    int collision_x;
    int collision_y;


    public Player father;
    int colour;
    Utils utils = new Utils();


    public Piece(Player father_)
    {
        father=father_;
        if (father==null)
        {
            Utils._E("Piece was made with a null father");
        }
        String colstr="";
        if (father.getColour()==Player.WHITE)
        {
            colstr="WHITE";
        }
        else if (father.getColour()==Player.BLACK)
        {
            colstr="BLACK";
        }
        else
        {
            colstr="**** NEITHER BLACK OR WHITE! (this isnt a michael jackson album)";
            Utils._E(colstr);
        }
        _("Piece made. father is "+colstr);
        colour=father.getColour();

        makeColourObjects(false);
    }

    public static void makeColourObjects(boolean forceRecreation)
    {
        if (white_piece_color==null || forceRecreation)
        {
            white_piece_color=new Color(WHITE_PIECE_COLOUR);
            white_piece_inner_color=new Color(WHITE_PIECE_INNER_COLOUR);
        }
        if (black_piece_color==null || forceRecreation)
        {
            black_piece_color=new Color(BLACK_PIECE_COLOUR);
            black_piece_inner_color=new Color(BLACK_PIECE_INNER_COLOUR);
        }
    }

    public int getColour()
    {
        return colour;
    }

    private void _(String s)
    {
        Utils.log("Piece{}:" + s);
    }

    public void drawPieceOnMouse(Graphics g, int x, int y)
    {
        paint( g,  x,  y);
    }

    public void paint(Graphics g, int x, int y)
    {

        PIECE_DIAMETER=Spike.TRIANGLE_HEIGHT/7;
        if (colour==Player.WHITE)
        {
            utils.setColor(g, white_piece_color);
        }
        else if (colour==Player.BLACK)
        {
            utils.setColor(g, black_piece_color);
            
        }
        else
        {
            Utils._E("Dave, Im a bit worried that this piece doesnt know what colour it is.");
        }

        if (stickToMouse)
        {
            x = Board.mouseHoverX;
            y = Board.mouseHoverY;
        }
//if (colour==Player.BLACK)
//utils.setColor(g, 0,0,0,CustomCanvas.TRANSPARENCY_LEVEL);
        utils.fillCircle(g,x, y,PIECE_DIAMETER, PIECE_DIAMETER);
           
        utils.setColor(g,Color.BLACK);
        utils.drawCircle(g,x, y,PIECE_DIAMETER, PIECE_DIAMETER);

        collision_x=x;
        collision_y=y;
        if (CustomCanvas.showCollisions)
        {

            utils.setColor(g,Color.RED);
            utils.drawRect(g,collision_x, collision_y,PIECE_DIAMETER, PIECE_DIAMETER);
        }

        //turned off inner circle for now, looks a bit weird
        /*
        ////draw inner circle////
        if (colour==Player.WHITE)
        {
            utils.setColor(g, white_piece_inner_color);
        }
        else if (colour==Player.BLACK)
        {
            utils.setColor(g, black_piece_inner_color);

        }
        int piece_diameter_over_2 = PIECE_DIAMETER/2;
        utils.drawCircle(g,x+piece_diameter_over_2/2, y+piece_diameter_over_2/2,piece_diameter_over_2, piece_diameter_over_2);
        */
        
       
    }

    boolean stickToMouse;
    //this is called to tell the piece to use the x, y vals from the mouse
    //instead of its usual ones as the user is placing it and it needs to stick to the
    //mouse point until they do place it.
    public void stickToMouse()
    {
        stickToMouse=true;
    }
    
    public void unstickFromMouse()
    {
        //log("unstickFromMouse");
        stickToMouse=false;
    }

    //returns true if the x,y passed in (from a mouse click) are within the
    //boundaries of this piece, ie user clicked on this piece
    public boolean userClickedOnThis(int mouseX, int mouseY)
    {
        if (mouseX>=collision_x && mouseX<=collision_x+PIECE_DIAMETER)
        {
            if (mouseY>=collision_y && mouseY<=collision_y+PIECE_DIAMETER)
            {
                _("Piece clicked on");
                return true;
            }
        }
        return false;
    }
}
