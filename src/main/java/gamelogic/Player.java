package gamelogic;
import lowlevel.*;

public class Player {

    public String name;
    public int score;
    private int colour;

    Player(int colour_, String name_) {
        colour = colour_;
        name = name_;
        log("Player made: "+name+" :: "+printColour());
    }
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    
    public int getColour()
    {
        return colour;
    }

    String printColour() {
        if (getColour() == WHITE)
            return "WHITE";
        else if(getColour()==BLACK)
            return "BLACK";
        else {
            Utils._E("PRINT COLOUR NEITHER B OR W");
            return "ERROR NO COLOUR";
        }
    }

    private void log(String s)
    {
        Utils.log("Player{}:" + s);
    }

    int homeSpikeStart() {
        return colour == WHITE ? 0 : 18;
    }

    int homeSpikeEnd() {
        return colour == WHITE ? 5 : 23;
    }

    int containerId() {
        return colour == WHITE ? -1 : 24;
    }

    int getDestinationSpike(Spike source, int roll) {
        return colour == WHITE ? source.getSpikeNumber() - roll :
            source.getSpikeNumber() + roll;
    }
}
