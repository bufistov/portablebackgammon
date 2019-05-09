package graphics;


import java.awt.*;
import java.util.HashMap;


public class GameColour {

    private static int defaultms[] = {
        /*BACKGROUND_COLOUR*/  0x993300,
        /*PANEL_COLOUR*/       0x000000,
        /*ROLL_BUTTON_COLOUR*/ 0xffcc66,
        /*Board.BOARD_COLOUR*/ 0x000000,
        /*Board.BAR_COLOUR*/   0x993300,
        /*Spike.BLACK_SPIKE_COLOUR*/ 0x993802,
        /*Spike.WHITE_SPIKE_COLOUR*/ 0xffcc7e,
        /*Piece.WHITE_PIECE_COLOUR*/ 0xe4e4d8,
        /*Piece.BLACK_PIECE_COLOUR*/ 0x612d00,
        /*Die.DIE_COLOUR*/           0xFFFFFF,
        /*Die.DOT_COLOUR*/           0x000000
    };

    private static int metalic[] = {
        /*BACKGROUND_COLOUR*/               0xffffff,
        /*PANEL_COLOUR*/                    0x828284,
        /*ROLL_BUTTON_COLOUR*/              0xffffff,
        /*Board.BOARD_COLOUR*/              0x9b9b9b,
        /*Board.BAR_COLOUR*/                0x8b898c,
        /*Spike.BLACK_SPIKE_COLOUR*/        0xc7c8cd,
        /*Spike.WHITE_SPIKE_COLOUR*/        0xa3a4a8,
        /*Piece.WHITE_PIECE_COLOUR*/        0xedf0f5,
        /*Piece.BLACK_PIECE_COLOUR*/        0x1a1a22,
        /*Die.DIE_COLOUR*/                  0x807875,
        /*Die.DOT_COLOUR*/                  0xe5e0da
    };

    private static int classic[] = {
        /*BACKGROUND_COLOUR*/               0x2c632a,
        /*PANEL_COLOUR*/                    0x002001,
        /*ROLL_BUTTON_COLOUR*/              0xfe1e1c,
        /*Board.BOARD_COLOUR*/              0xf4ebca,
        /*Board.BAR_COLOUR*/                0x245223,
        /*Spike.BLACK_SPIKE_COLOUR*/        0x99643c,
        /*Spike.WHITE_SPIKE_COLOUR*/        0xed974c,
        /*Piece.WHITE_PIECE_COLOUR*/        0xfefbf2,
        /*Piece.BLACK_PIECE_COLOUR*/        0x363b3f,
        /*Die.DIE_COLOUR*/                  0xfe1e1c,
        /*Die.DOT_COLOUR*/                  0xfffdfe
    };


    private static int funnyman[] = {
        /*BACKGROUND_COLOUR*/               0x661913,
        /*PANEL_COLOUR*/                    0x210d0c,
        /*ROLL_BUTTON_COLOUR*/              0xffffff,
        /*Board.BOARD_COLOUR*/              0x9d581d,
        /*Board.BAR_COLOUR*/                0x490f0e,
        /*Spike.BLACK_SPIKE_COLOUR*/        0x290d0a,
        /*Spike.WHITE_SPIKE_COLOUR*/        0x6e1213,
        /*Piece.WHITE_PIECE_COLOUR*/        0x4e3113,
        /*Piece.BLACK_PIECE_COLOUR*/        0x841b25,
        /*Die.DIE_COLOUR*/                  0xffffff,
        /*Die.DOT_COLOUR*/                  0x791216
    };

    private static int bumblebee[] = {
        /*BACKGROUND_COLOUR*/               0x202427,
        /*PANEL_COLOUR*/                    0x3a3a3a,
        /*ROLL_BUTTON_COLOUR*/              0xe4ff00,
        /*Board.BOARD_COLOUR*/              0x50555b,
        /*Board.BAR_COLOUR*/                0x545454,
        /*Spike.BLACK_SPIKE_COLOUR*/        0x030504,
        /*Spike.WHITE_SPIKE_COLOUR*/        0xe4ff00,
        /*Piece.WHITE_PIECE_COLOUR*/        0xb1995d,
        /*Piece.BLACK_PIECE_COLOUR*/        0x404443,
        /*Die.DIE_COLOUR*/                  0x000000,
        /*Die.DOT_COLOUR*/                  0xe4ff00
    };

    private static HashMap<String, int[]> themes = new HashMap<>();
    static {
        themes.put("default", defaultms);
        themes.put("metalic", metalic);
        themes.put("classic", funnyman);
        themes.put("funnyman", funnyman);
        themes.put("bumblebee", bumblebee);
    }

    private Color background;
    private Color panel;
    private Color rollButton;
    private Color board;
    private Color bar;
    private Color blackSpike;
    private Color whiteSpike;
    private Color whitePiece;
    private Color blackPiece;
    private Color die;
    private Color dieDot;

    private Color flash;

    public GameColour() {
        applyTheme(defaultms);
        flash = new Color(255,225,0);
    }

    public void applyTheme(String name) {
        applyTheme(themes.getOrDefault(name, defaultms));
    }

    private void applyTheme(int[] theme) {
        background = new Color(theme[0]);
        panel = new Color(theme[1]);
        rollButton = new Color(theme[2]);
        board = new Color(theme[3]);
        bar = new Color(theme[4]);
        blackSpike = new Color(theme[5]);
        whiteSpike = new Color(theme[6]);
        whitePiece = new Color(theme[7]);
        blackPiece = new Color(theme[8]);
        die = new Color(theme[9]);
        dieDot = new Color(theme[10]);
    }

    public Color getBackground() {
        return background;
    }

    public Color getPanel() {
        return panel;
    }

    public Color getRollButton() {
        return rollButton;
    }

    public Color getBoard() {
        return board;
    }

    public Color getBar() {
        return bar;
    }

    public Color getBlackSpike() {
        return blackSpike;
    }

    public Color getWhiteSpike() {
        return whiteSpike;
    }

    public Color getWhitePiece() {
        return whitePiece;
    }

    public Color getBlackPiece() {
        return blackPiece;
    }

    public Color getDie() {
        return die;
    }

    public Color getDieDot() {
        return dieDot;
    }

    public Color flash() {
        return flash;
    }
}
