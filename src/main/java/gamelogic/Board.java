package gamelogic;
import java.awt.*;
import java.util.*;

import data.DieType;
import data.PlayerColor;
import graphics.GameColour;
import graphics.Geometry;
import lowlevel.*;

public class Board {

    private Color board_colour, bar_colour;
    private GameColour gameColour;
    private Geometry geometry;

    // game state variables
    private ArrayList<Spike> spikes;
    private Player whitePlayer, blackPlayer;
    private Player currentPlayer;
    public static Die die1, die2;

    private Utils utils = new Utils();
    private Sound sfxNoMove;

    private static final int LAST_SPIKE = 23;
    private static final int FIRST_SPIKE = 0;

    private static final int BOARD_NEW_GAME = 0;
    private static final int DEBUG_PIECES_IN_THEIR_HOME = 1;
    private static final int INIT_CONFIGURATION = BOARD_NEW_GAME;
    private static final int[] whiteInitPositions = {
        0, 0, 0, 0, 0, 5,
        0, 3, 0, 0, 0, 0,
        5, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 2
    };

    private static final int[] blackInitPositioin = new int[24];
    static {
        int blackIdx = 23;
        for (int i = 0; i < whiteInitPositions.length; ++i, --blackIdx) {
            blackInitPositioin[blackIdx] = whiteInitPositions[i];
        }
    }

    // Garbage
    public static boolean listBotsOptions;
    public static String botOptions = "<<NONE YET>>";
    public SpikePair SPtheMoveToMake; // stores the move they will make

    //these flags indicate if the player has took their go yet for that dice
    //if the dice are combined then they are both set to false in one go.
    public static boolean die1HasBeenUsed, die2HasBeenUsed;

    public static boolean NOT_A_BOT_BUT_A_NETWORKED_PLAYER = false;

    public Board(GameColour gameColour, Geometry geometry, GameConfig config) {
        this.gameColour = gameColour;
        this.geometry = geometry;
        sfxNoMove = new Sound("/nomove.wav");
        loadSounds(config.soundOn());
        whitePlayer = new Player(PlayerColor.WHITE,"Player1");
        blackPlayer = new Player(PlayerColor.BLACK,"Player2");
        currentPlayer = whitePlayer;
        spikes = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            spikes.add(new Spike(geometry, i));
        }
        die1 = new Die(geometry);
        die2 = new Die(geometry);
        makeColourObjects();
        initialiseBoard(INIT_CONFIGURATION);
        log("Board made");
    }

    public void loadSounds(boolean soundOn) {
        sfxNoMove.loadSound(soundOn);
    }

    public void makeColourObjects() {
        board_colour = new Color(gameColour.getBoardColor());
        bar_colour = new Color(gameColour.getBarColor());
        for (Spike spike: spikes) {
            spike.makeColourObjects();
        }
    }
    
    public void paint(Graphics g, int boardWidth, int boardHeight, boolean gameInProgress) {
        utils.setColor(g, Color.BLACK);
        int borderWidth = geometry.borderWidth();
        int barWidth = geometry.centralBarWidth();
        int widthMinusBorder = boardWidth - barWidth;

        //draw the board:
        // outline:
        utils.setColor(g, board_colour);
        utils.fillRect(g,borderWidth,borderWidth,widthMinusBorder,boardHeight-borderWidth*2);
        utils.setColor(g, Color.BLACK);
        utils.drawRect(g,borderWidth,borderWidth,widthMinusBorder,boardHeight-borderWidth*2);
        // bar between 2 halves
        utils.setColor(g, bar_colour);
        utils.fillRect(g,boardWidth / 2 - barWidth / 2, borderWidth, barWidth,boardHeight - borderWidth * 2);
        utils.setColor(g, Color.BLACK);
        utils.drawRect(g,boardWidth / 2 - barWidth / 2, borderWidth, barWidth,boardHeight - borderWidth * 2);

        for (Spike spike: spikes) {
           spike.paint(g);
        }

        paintDice(g,boardWidth,boardHeight);

        // draw the potential moves for whoevers go it is
        if (gameInProgress) {
            if (haveToMovePieceFromBar(currentPlayer.getColour())) {
                drawPotentialMovesFromBar();
            } else {
                if (CustomCanvas.pieceStuckToMouse == null) {
                    drawPotentialMoves();
                } else {
                    keepPotentialSpikesPulsing(CustomCanvas.pieceStuckToMouse.sourceSpikeId());
                }
            }
            if (die1.enabled() || die2.enabled()) {
                calculatePotentialMoves();
            }
        }
    }

    private void paintDice(Graphics g, int WIDTH, int HEIGHT) {
        if (!CustomCanvas.showRollButton) {
            int diex = (geometry.borderWidth() + ((WIDTH/4)*3)) + geometry.dieSize();
            int diey = (geometry.borderWidth() + (HEIGHT/2)) - geometry.dieSize();
            if (!die1HasBeenUsed) {
                die1.paint(g, diex, diey);
            } else {
                die1.disable();
            }

            diex += geometry.dieSize() + geometry.tinyGap();
            if (!die2HasBeenUsed) {
                die2.paint(g, diex, diey);
            } else {
                die2.disable();
            }
        }
    }

    // when piece is on the bar this should display the options for it
    private void drawPotentialMovesFromBar() {
        allowPieceToStickToMouse = false; // make this false right away since its decided in this method, but could still be true from last time.
        checkConsistent();
        if (CustomCanvas.showRollButton) {
            return;
        }

        ArrayList<Spike> spikesAllowedToMoveToFromBar = new ArrayList<>();
        boolean cantGetOfBarWithDie1 = !canWeGetOffTheBarWithThisDie(die1, DieType.DIE1, spikesAllowedToMoveToFromBar);
        if (!cantGetOfBarWithDie1) {
            getOffTheBarOptions(spikesAllowedToMoveToFromBar);
        } else {
            log("NO OPTIONS FOR GETTING OFF BAR WITH DIE1");
        }
        boolean cantGetOfBarWithDie2 = !canWeGetOffTheBarWithThisDie(die2, DieType.DIE2, spikesAllowedToMoveToFromBar);
        if (!cantGetOfBarWithDie2) {
            getOffTheBarOptions(spikesAllowedToMoveToFromBar);
        } else {
            log("NO OPTIONS FOR GETTING OFF BAR WITH DIE2");
        }

        if (cantGetOfBarWithDie1 && cantGetOfBarWithDie2) {
            log("NO OPTIONS FROM BAR NEXT TURN!!!!!!!!!!!!!!");
            die1HasBeenUsed = true;
            die2HasBeenUsed = true;
            die1.disable();
            die2.disable();
        }
    }

    private void getOffTheBarOptions(ArrayList<Spike> spikesAllowedToMoveToFromBar) {
        Spike destinationSpike = spikesAllowedToMoveToFromBar.get(0);
        Vector theBarPieces = whoseTurnIsIt() == PlayerColor.WHITE ? CustomCanvas.theBarWHITE : CustomCanvas.theBarBLACK;
        log("White pieces on bar potential pick ups: " + theBarPieces.size());
        if (CustomCanvas.pieceOnMouse) {
            log("Destination PLACE ON AVAIL SPIKE:" + destinationSpike.getSpikeNumber());
            Point spikeMiddle = destinationSpike.getMiddlePoint();
            setBotDestination(spikeMiddle.x, spikeMiddle.y, "PLACE FROM BAR ONTO AVAIL SPIKES");
        } else {
            log("DESTINATION FOR BOT, PIECE ON BAR......");
            Piece p = (Piece) theBarPieces.firstElement();
            setBotDestination(p.getCenterX(), p.getCenterY(),"DESTINATION FOR BOT, PIECE ON BAR......");
        }
    }

    // given the die this method will add spikes to spikesAllowedToMoveToFromBar
    // for current player so that the spikes available will flash and be ready to have a piece added to them
    private boolean canWeGetOffTheBarWithThisDie(Die die, DieType whichDie, ArrayList<Spike> spikesAllowedToMoveToFromBar) {
        int destinationSpikeId = whoseTurnIsIt() == PlayerColor.BLACK ? die.getValue() - 1 : 24 - die.getValue();
        if (destinationSpikeId >= 0 && destinationSpikeId < spikes.size()) {
            Spike destinationSpike = spikes.get(destinationSpikeId);
            if (destinationSpike.pieces.size() <= 1 ||
                doesThisSpikeBelongToPlayer(destinationSpike, whoseTurnIsIt())) {
                destinationSpike.flash(whichDie);
                if (spikesAllowedToMoveToFromBar != null && !spikesAllowedToMoveToFromBar.contains(destinationSpike)) {
                    destinationSpike.store_this_die(die);
                    spikesAllowedToMoveToFromBar.add(destinationSpike);
                }
                return true;
            }
        }
        return false;
    }

    public void RESET_ENTIRE_GAME_VARS(boolean soundOn) {
        loadSounds(soundOn);
        currentPlayer = whitePlayer;
        allowPieceToStickToMouse = false;
        die1HasBeenUsed = false;
        die2HasBeenUsed = false;

        listBotsOptions = false;
        botOptions = "<<NONE YET>>";
        SPtheMoveToMake = null;

        initialiseBoard(INIT_CONFIGURATION);
    }

    // pulses the spikes while piece is stuck to mouse
    private void keepPotentialSpikesPulsing(int sourceSpikeId) {
        if (sourceSpikeId == -1) {
            ArrayList<Spike> possibleSpikes = this.spikesToMoveToFromBar(whoseTurnIsIt());
            for (Spike spike: possibleSpikes) {
                int neededValue = currentPlayer.isWhite() ? spike.getPosition() : 24 - spike.getPosition();
                spike.flash(neededValue == die1.getValue() ? DieType.DIE1 : DieType.DIE2);
            }
        } else {
            ArrayList<Integer> possibleSpikes = reachableSpikes(spikes.get(sourceSpikeId), currentPlayer, die1, die2);
            for (Integer spikeId: possibleSpikes) {
                int neededValue = Math.abs(sourceSpikeId - spikeId);
                DieType dieType = DieType.DIE1;
                if (neededValue == die1.getValue()) {
                    dieType = DieType.DIE1;
                } else if (neededValue == die2.getValue()) {
                    dieType = DieType.DIE2;
                } else if (neededValue == die1.getValue() + die2.getValue()) {
                    dieType = DieType.DIE1AND2;
                }
                if (spikeId != currentPlayer.containerId()) {
                    spikes.get(spikeId).flash(dieType);
                }
            }
        }
    }

    // this is always the current x and y vals of the mouse pointer
    public static int mouseHoverX,mouseHoverY;

    // controls whether the piece should follow the mouse when clicked on by player
    // (based on if there are potential moves to be made)
    public static boolean allowPieceToStickToMouse = false;

    // we take copies of the "pulsating" spikes (ie those that are valid spikes that
    // the current player could move a piece to) - we take these copies so that
    // when the player picks up a piece (ie it becomes stuck to the mouse pointer)
    // and moves it around, we can still show the pulsating valid spikes that he
    // can drop this piece onto. Without these copies as soon as the pointer
    // stopped hovering over the current spike (like it would if player was placing
    // a piece) the pulsating indication of valid options would vanish.
    public static Spike copy_of_reachableFromDie1;
    public static Spike copy_of_reachableFromDie2;
    public static Spike copy_of_reachableFromBothDice;

    private void checkConsistent() {
        final int whitePieces = calculateAmountOfPiecesOnBoard(PlayerColor.WHITE) +
            CustomCanvas.whitePiecesSafelyInContainer.size() +
            CustomCanvas.theBarWHITE.size();
        if (whitePieces != 15) {
            throw new RuntimeException("PIECES NOT EQUAL TO 15 FOR WHITE its " + whitePieces);
        }
        final int blackPieces = calculateAmountOfPiecesOnBoard(PlayerColor.BLACK) +
            CustomCanvas.blackPiecesSafelyInContainer.size() +
            CustomCanvas.theBarBLACK.size();
        if (blackPieces != 15) {
            throw new RuntimeException("PIECES NOT EQUAL TO 15 FOR BLACK its " + blackPieces);
        }
    }

    /*
     * This method draws an indicator to show the player potential moves
     * for the piece they currently have the mouse hovered over
     * there can be up to 3 potential moves: die1, die2, die1+die2
     */
    private void drawPotentialMoves() {
        boolean debugmessages = false;
        allowPieceToStickToMouse = false; // make this false right away since its decided in this method, but could still be true from last time.
        checkConsistent(); // sets the right bools if pieces are home

        if (CustomCanvas.showRollButton) {
            return;
        }

        Spike currentSpikeHoveringOver = doesThisSpikeBelongToPlayer();
        if (currentSpikeHoveringOver == null) {
            return;
        }
        boolean die1AnOption = checkValidPotentialMove(currentSpikeHoveringOver, die1.getValue());

        //this boolean is used to keep track of whether we let a piece stick to mouse, so we need to keep a track on if die1
        //is an option (since if it is we want that piece to be able to be picked up) but we do further checks below
        //so this "stillAnOption" variable gets updated below and used at the bottom to update canWeStickPieceToMouse
        boolean die1StillAnOption = false;//set to false first so we know if its true its been updated below

        if (die1AnOption && !die1HasBeenUsed) {
            copy_of_reachableFromDie1 = null;
            int potentialSpikeIndex = currentPlayer.getDestinationSpike(currentSpikeHoveringOver, die1.getValue());
            boolean highlightPieceContainerAsOption = potentialSpikeIndex == currentPlayer.containerId()
                && !anybodyNotInHome(currentPlayer);
            if (highlightPieceContainerAsOption) {
                if (potentialSpikeIndex == FIRST_SPIKE - 1 && whoseTurnIsIt() == PlayerColor.WHITE) {
                    die1StillAnOption = true;
                    copy_of_reachableFromDie1 = null; // STOPS OLD SPIKES FLASHING IN PICE CONTAINER CIRCUMSTANCES
                } else if (potentialSpikeIndex == LAST_SPIKE+1 && whoseTurnIsIt() == PlayerColor.BLACK) {
                    log("yes " + potentialSpikeIndex + " is a valid option DIE1 TO GET ONTO PIECE BLACK CONTAINER");
                    die1StillAnOption = true;
                    copy_of_reachableFromDie1 = null; // STOPS OLD SPIKES FLASHING IN PICE CONTAINER CIRCUMSTANCES
                }
            }
           ///NO NEED FOR ELSE HERE EXPERIMENTAL 21JAN 1018AM else // normal situation. ie a spike is the option
            if (potentialSpikeIndex >= FIRST_SPIKE && potentialSpikeIndex <= LAST_SPIKE) {
                 Spike reachableFromDie1 = spikes.get(potentialSpikeIndex);
                 if (debugmessages) {
                    log("yes " + potentialSpikeIndex + " is a valid option DIE1");
                 }
                 pulsatePotentialSpike(reachableFromDie1, DieType.DIE1);
                 copy_of_reachableFromDie1 = reachableFromDie1;
                 if (debugmessages) {
                    log("copy_of_reachableFromDie1 set up.");
                 }
                 die1StillAnOption=true;
            }
        } else {
            //EXPERIMENTAL JAN 14TH 2010 [seems to work remove this line]
            //make this null if we know for certain its not a potential move or ot gets remembered
            copy_of_reachableFromDie1 = null;
        }

        boolean die2AnOption = checkValidPotentialMove(currentSpikeHoveringOver, die2.getValue());
        boolean die2StillAnOption = false;
        if (die2AnOption && !die2HasBeenUsed) {
            copy_of_reachableFromDie2 = null; //Set to null here so we know if its valid by the end it true is
            int potentialSpikeIndex = currentPlayer.getDestinationSpike(currentSpikeHoveringOver, die2.getValue());
            boolean highlightPieceContainerAsOption = potentialSpikeIndex == currentPlayer.containerId()
                && !anybodyNotInHome(currentPlayer);
            if (highlightPieceContainerAsOption) {
                if (potentialSpikeIndex == FIRST_SPIKE-1 && whoseTurnIsIt() == PlayerColor.WHITE) {
                     log("yes " + potentialSpikeIndex + " is a valid option DIE2 TO GET ONTO PIECE WHITE CONTAINER");
                    die2StillAnOption = true;
                    copy_of_reachableFromDie2 = null;//EXPERIMENT- YES IT WORKS
                } else if (potentialSpikeIndex == LAST_SPIKE + 1 && whoseTurnIsIt() == PlayerColor.BLACK) {
                     log("yes " + potentialSpikeIndex + " is a valid option DIE2 TO GET ONTO PIECE BLACK CONTAINER");
                     die2StillAnOption = true;
                     copy_of_reachableFromDie2 = null;//EXPERIMENT-YES IT WORKS
                }
            }
            if (potentialSpikeIndex >= FIRST_SPIKE && potentialSpikeIndex <= LAST_SPIKE) {
                 Spike reachableFromDie2 = spikes.get(potentialSpikeIndex);
                 if (debugmessages) {
                    log("yes " + potentialSpikeIndex + " is a valid option DIE2");
                 }
                 pulsatePotentialSpike(reachableFromDie2, DieType.DIE2);

                 copy_of_reachableFromDie2=reachableFromDie2;
                 if (debugmessages) {
                    log("copy_of_reachableFromDie2 set up.");
                 }
                 die2StillAnOption=true;
            }
        } else {
            //EXPERIMENTAL JAN 14TH 2010 [seems to work remove this line]
            //make this null if we know for certain its not a potential move or ot gets remembered
            copy_of_reachableFromDie2=null;
        }

        //and DIE1 + DIE2
        boolean bothDiceAnOption = checkValidPotentialMove(currentSpikeHoveringOver,die1.getValue()+die2.getValue());
        boolean bothDiceStillAnOption = false;


        //if die1+die2 would yield a potential valid spike to land on AND if the
        //player has not used die1 OR die2 this turn yet.
        //AND ***IMPORTANTLY IF die1 OR die2 are valid options, since the player needs to be able to take each
        //turn and not simply add them together (therefore making an invalid move by combining their rolls)
        //this is not how backgammon works.
        if (bothDiceAnOption && !die1HasBeenUsed && !die2HasBeenUsed && (die1AnOption || die2AnOption)) {
            copy_of_reachableFromBothDice = null; // Set to null here so we know if its valid by the end it true is
            int potentialSpikeIndex = currentPlayer.getDestinationSpike(currentSpikeHoveringOver, die1.getValue() + die2.getValue());
            boolean highlightPieceContainerAsOption = potentialSpikeIndex == currentPlayer.containerId() &&
                !anybodyNotInHome(currentPlayer);
            // only for the case when a piece can go into the piece container
            if (highlightPieceContainerAsOption) {//<-- this can get set by checkValidPotentialMove when the situation is right
                if (potentialSpikeIndex == FIRST_SPIKE - 1 && whoseTurnIsIt() == PlayerColor.WHITE) {
                    log("yes " + potentialSpikeIndex + " is a valid option DIE1+DIE2 TO GET ONTO PIECE WHITE CONTAINER");
                    bothDiceStillAnOption = true;
                } else if (potentialSpikeIndex == LAST_SPIKE + 1 && whoseTurnIsIt() == PlayerColor.BLACK) {
                    log("yes " + potentialSpikeIndex + " is a valid option DIE1+DIE2 TO GET ONTO PIECE BLACK CONTAINER");
                    bothDiceStillAnOption = true;
                }
            }
            ///NO NEED FOR ELSE HERE EXPERIMENTAL 21JAN 1018AM else // normal situation. ie a spike is the option
            if (potentialSpikeIndex >= FIRST_SPIKE && potentialSpikeIndex <= LAST_SPIKE) {
                 Spike reachableFromBothDice = spikes.get(potentialSpikeIndex);
                 if (debugmessages) {
                    log("yes " + potentialSpikeIndex + " is a valid option BOTH DICE");
                 }
                 reachableFromBothDice.flash(DieType.DIE1AND2);
                 copy_of_reachableFromBothDice = reachableFromBothDice;
                 if (debugmessages) {
                    log("copy_of_reachableFromBothDice set up.");
                 }
                 bothDiceStillAnOption=true;
            }
        } else {
            //EXPERIMENTAL JAN 14TH 2010 [seems to work remove this line]
            //make this null if we know for certain its not a potential move or ot gets remembered
            copy_of_reachableFromBothDice=null;
        }
        allowPieceToStickToMouse = die1StillAnOption || die2StillAnOption || bothDiceStillAnOption;
    }

    /**
     * Returns list of ids of reachable spikes from given spike for given player.
     * If current spike does not belongs to the player an empty list is returned.
     * @param currentSpike the spike to calculate reachable spikes from
     * @param player the player to check.
     * @param die1 die1
     * @param die2 die2
     * @return The list of spike ids (0 to 23 for normal spikes, -1 or 24 for container ids)
     */
    private ArrayList<Integer> reachableSpikes(Spike currentSpike, Player player, Die die1, Die die2) {
        ArrayList<Integer> result = new ArrayList<>();
        if (currentSpike.getAmountOfPieces(player.getColour()) > 0) {
            boolean die1AnOption = die1.enabled() && checkValidPotentialMove(currentSpike, die1.getValue());
            if (die1AnOption) {
                result.add(player.getDestinationSpikeId(currentSpike, die1.getValue()));
            }

            boolean die2AnOption = die2.enabled() && checkValidPotentialMove(currentSpike, die2.getValue());
            if (die2AnOption) {
                Integer die2Destination = player.getDestinationSpikeId(currentSpike, die2.getValue());
                if (die2Destination == player.containerId()) {
                    if (die2.getValue() == die1.getValue() || result.isEmpty()) {
                            result.add(die2Destination);
                    }
                } else {
                    result.add(die2Destination);
                }
            }

            boolean die1and2AnOption = die1.enabled() && die2.enabled() && (die1AnOption || die2AnOption)
                && !result.contains(player.containerId()) &&
                checkValidPotentialMove(currentSpike, die1.getValue() + die2.getValue());
            if (die1and2AnOption) {
                result.add(player.getDestinationSpikeId(currentSpike, die1.getValue() + die2.getValue()));
            }
        }
        return result;
    }

    private int calculateAmountOfPiecesOnBoard(PlayerColor player) {
        int piecesOnBoard = 0;
        for (Spike spike: spikes) {
            piecesOnBoard += spike.getAmountOfPieces(player);
        }
        return piecesOnBoard;
    }

    private int calculateAmountOfPiecesInHomeArea(Player player) {
        int piecesInHomeArea = 0;
        for (Spike spike: spikes) {
            if (!spike.pieces.isEmpty() &&
                spike.getSpikeNumber() >= player.homeSpikeStart() && spike.getSpikeNumber() <= player.homeSpikeEnd()) {
                Piece piece = (Piece) spike.pieces.firstElement();
                if (piece.getColour() == player.getColour()) {
                    piecesInHomeArea += spike.pieces.size();
                }
            }
        }
        return piecesInHomeArea;
    }

    private boolean anybodyNotInHome(Player player) {
        for (Spike spike: spikes) {
            if (spike.getAmountOfPieces(player.getColour()) > 0 &&
                (spike.getSpikeNumber() < player.homeSpikeStart() || spike.getSpikeNumber() > player.homeSpikeEnd()))
                return true;
        }
        return false;
    }

    //takes in a spike and a die value,
    //this is to indicate to player its a potential move they can make
    //whichDice tells the spike which die would allow this potential move, so it an be displayed to player
    private void pulsatePotentialSpike(Spike spike, DieType whichDice) {
        //this makes the spike colour pulse nicely to indicate its an option
        spike.flash(whichDice);
    }

    /**
     * Checks if current player can make a valid move from given spike using given die.
     * @param currentSpike Spike under consideration.
     * @param dieRoll value of the die.
     * @return true if the  move is possible, false otherwise
     */
    private boolean checkValidPotentialMove(Spike currentSpike, int dieRoll) {
        int potentialSpike = currentPlayer.getDestinationSpike(currentSpike, dieRoll);
        if (potentialSpike < whitePlayer.containerId() && lastNotEmptyWhiteSpike().getPosition() == currentSpike.getPosition()) {
            potentialSpike = whitePlayer.containerId();
        }
        if (potentialSpike > blackPlayer.containerId() && lastNotEmptyBlackSpike().getPosition() == currentSpike.getPosition()) {
            potentialSpike = blackPlayer.containerId();
        }
        if (potentialSpike == currentPlayer.containerId() && !anybodyNotInHome(currentPlayer)) {
            return true;
        }
        return (potentialSpike >= FIRST_SPIKE) && (potentialSpike <= LAST_SPIKE) &&
            (isThisSpikeEmpty(spikes.get(potentialSpike)) ||
                doesThisSpikeBelongToPlayer(spikes.get(potentialSpike), whoseTurnIsIt()));
    }

    private Spike doesThisSpikeBelongToPlayer() {
        Spike hoverSpike = grabSpikeHoveringOver();
        boolean containsOneOfTheirPieces = false;
        if (hoverSpike != null) {
           containsOneOfTheirPieces = doesThisSpikeBelongToPlayer(hoverSpike, whoseTurnIsIt());
        }
        if (containsOneOfTheirPieces) {
            return hoverSpike;
        } else {
            return null;
        }
    }

    private Spike grabSpikeHoveringOver() {
       for (Spike currentSpike: spikes) {
           if (currentSpike.userClickedOnThis(mouseHoverX, mouseHoverY)) {
               return currentSpike;
           }
        }
        return null;
    }

    // ALSO RETURNS TRUE IF THERE IS ONLY ONE ENEMY PIECE ON THE SPIKE
    private boolean isThisSpikeEmpty(Spike checkme) {
        return  checkme.pieces.size() <= 1;
    }

    //returns true if the spike passed in contains a piece belonging to
    //the player colour passed in. returns false if not.
    private boolean doesThisSpikeBelongToPlayer(Spike checkme, PlayerColor playerColour) {
         Enumeration piecesE = checkme.pieces.elements();
         while(piecesE.hasMoreElements()) {
             Piece aPiece = (Piece) piecesE.nextElement();
             if (aPiece.getColour() == playerColour) {
                return true;
             }
        }
        return false;
    }

    void initialiseBoard(int mode) {
        log("mode: BOARD_NEW_GAME");
        initialiseBoardForNewGame(whiteInitPositions, blackInitPositioin);
        if (mode == DEBUG_PIECES_IN_THEIR_HOME) {
            log("mode: DEBUG_BOARD_WHITE_PIECES_IN_THEIR_HOME");
            int[] whiteHome = {
                0, 5, 5, 5, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0
            };
            int[] blackHome = {
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 2, 11, 2, 0, 0
            };
            initialiseBoardForNewGame(whiteHome, blackHome);
        }
    }


    // puts the pieces where they need to be to initialise a new game of backgammon
    void initialiseBoardForNewGame(int[] whiteInitPositions, int[] blackInitPositions) {
        log("initialiseBoardForNewGame");
        for (Spike spike: spikes) {
            spike.pieces.clear();
        }
        int whitePiecesOnBoard = 0;
        for (int i = 0; i < whiteInitPositions.length; ++i) {
            whitePiecesOnBoard += whiteInitPositions[i];
            for (int j = 0; j < whiteInitPositions[i]; ++j) {
                Piece newPiece = new Piece(geometry, whitePlayer);
                spikes.get(i).addPiece(newPiece);
            }
        }
        int blackPiecesOnBoard = 0;
        for (int i = 0; i < blackInitPositions.length; ++i) {
            blackPiecesOnBoard += blackInitPositions[i];
            for (int j = 0; j < blackInitPositions[i]; ++j) {
                Piece newPiece = new Piece(geometry, blackPlayer);
                spikes.get(i).addPiece(newPiece);
            }
        }
        for (int j = whitePiecesOnBoard; j < 15; ++j) {
            CustomCanvas.theBarWHITE.add(new Piece(geometry, whitePlayer));
        }
        for (int j = blackPiecesOnBoard; j < 15; ++j) {
            CustomCanvas.theBarBLACK.add(new Piece(geometry, blackPlayer));
        }
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    private static void log(String s) {
        Utils.log(String.format("thread-%s Board{}:%s", Thread.currentThread().getName(), s));
    }

    void calculatePotentialMoves() {
        if (!CustomCanvas.showRollButton && !CustomCanvas.pieceOnMouse && SPtheMoveToMake == null) {
            log("_______________________RECALCULATE MOVES die1:" + die1HasBeenUsed + " die2:" + die2HasBeenUsed);
            Vector spikePairs = getValidOptions();
            log("Potential moves: " + spikePairs.size());
            if (spikePairs.size() > 0) {
                listBotsOptions = true;
                if (listBotsOptions) {
                    botOptions = "";
                    Enumeration ee = spikePairs.elements();
                    while (ee.hasMoreElements()) {
                        SpikePair sp = (SpikePair) ee.nextElement();
                        botOptions += "->" + sp.pickMyPiece.getName() + "->" + sp.dropPiecesOnMe.getName() + " ";
                    }
                    log("valid options: " + botOptions);
                }
                SPtheMoveToMake = (SpikePair) spikePairs.elementAt(Utils.getRand(0, spikePairs.size() - 1));
                if (SPtheMoveToMake.dropPiecesOnMe.isContainer()) {
                    log("SPECIAL CASE randomly chose to go to spike:" +
                        SPtheMoveToMake.pickMyPiece.getName() + " and drop off at CONTAINER");
                    CustomCanvas.tellRobot(true, "->" + SPtheMoveToMake.pickMyPiece.getName() + "->Container");
                    Spike takeMyPiece = SPtheMoveToMake.pickMyPiece;
                    Piece firstPiece = ((Piece) takeMyPiece.pieces.firstElement());
                    setBotDestination(firstPiece.getCenterX(), firstPiece.getCenterY(),
                        "TAKE A PIECE TO CONTAINER");
                } else {
                    log("-randomly chose to go to spike:" +
                        SPtheMoveToMake.pickMyPiece + " and drop off at spike:" +
                        SPtheMoveToMake.dropPiecesOnMe);
                    CustomCanvas.tellRobot(true, "->" + SPtheMoveToMake.pickMyPiece +
                        "->" + SPtheMoveToMake.dropPiecesOnMe);
                    Spike takeMyPiece = SPtheMoveToMake.pickMyPiece;
                    Piece firstPiece = ((Piece) takeMyPiece.pieces.firstElement());
                    int goToX = firstPiece.getCenterX();
                    int goToY = firstPiece.getCenterY();
                    setBotDestination(goToX, goToY, "RANDOMLY CHOOSE A PIECE");
                    log("***************PIECE IM LOOKING FOR IS AT: " + goToX + "," + goToY);
                }
            } else {
                log("NO OPTIONS!");
                if (!die1HasBeenUsed){
                    //SPECIAL CASE LARGE DIE ROLLS NEED TO BECOME VALID NOW. AS THEY NEED TO PUT PIECES AWAY
                    //so what we do is sneaky, reduce die value number (hiding it from players of course)
                    //which makes option become available in this case.
                    if (allPiecesAreHome()) {
                        log("LOWERING THEVALUE OF DIE 1");
                        die1.setValue(die1.getValue() - 1);
                    } else {
                        //ORDINARY CASE
                        //use this die up so it can move onto next one
                        die1HasBeenUsed = true;
                        log("DISABLED DIE 1x");
                        // canvas.tellPlayers("No option with Die 1 (" + die1.getValue() + ")");
                        sfxNoMove.playSound();
                    }
                } else if (!die2HasBeenUsed) {
                    if (allPiecesAreHome()) {
                        log("LOWERING THEVALUE OF DIE 2");
                        die2.setValue(die2.getValue() - 1);
                    } else {
                        //use this die up so it can move onto next go
                        die2HasBeenUsed = true;
                        log("DISABLED DIE 2x");
                        // canvas.tellPlayers("No options available with Die 2 (" + die2.getValue() + ")");
                        sfxNoMove.playSound();
                        //it should move onto next players go NOW...
                        if (CustomCanvas.someoneRolledADouble) {
                            //even cancel a double go if theres no options for die2
                            CustomCanvas.someoneRolledADouble = false;
                            CustomCanvas.doubleRollCounter = 3;
                            log("NO OPTIONS SO CANCELLED DOUBLE TURN!");
                        }
                    }
                }
            }
        } else if (CustomCanvas.pieceOnMouse) {
            // Point robot the final destination after it took the piece
            theyWantToPlaceAPiece();
        }
    }

    /**
     * Checks the currently available die and populates spikePairs vector with
     * valid (sourceSpike, destinationSpike) pairs.
     */
    private Vector getValidOptions() {
        log("Getting valid spike pairs");
        Vector spikePairs = new Vector(5);
        int diceRoll = -1;
        if (!die1HasBeenUsed) {
            diceRoll = die1.getValue();
            log("using DIE1 value " + diceRoll);
        } else if (!die2HasBeenUsed) {
            diceRoll = die2.getValue();
            log("using DIE2 value " + diceRoll);
        }
        if (diceRoll > 0) {
            for (Spike spike: spikes) {
                if (spike.getAmountOfPieces(currentPlayer.getColour()) > 0) {
                    int potentialSpike = currentPlayer.getDestinationSpike(spike, diceRoll);
                    if (allPiecesAreHome()) {
                        if (potentialSpike >= FIRST_SPIKE - 1 && potentialSpike <= LAST_SPIKE + 1) {
                            log("checkAbleToGetIntoPieceContainer is true - potentialSpike:" + potentialSpike);
                            if (potentialSpike == LAST_SPIKE + 1 || potentialSpike == FIRST_SPIKE - 1) {
                                log("PIECECONTAINER: MAKING A FAKE SPIKE, potentialspike is " + potentialSpike);
                                Spike destinationSpike = new Spike(geometry,-1);
                                log("yes " + destinationSpike + " IS A PIECE CONTAINER we can move to");
                                spikePairs.add(new SpikePair(spike, destinationSpike));
                            }
                        }
                    }
                    if (potentialSpike >= FIRST_SPIKE && potentialSpike <= LAST_SPIKE){
                        Spike destinationSpike = spikes.get(potentialSpike);
                        if (destinationSpike.pieces.size() <= 1 ||
                            doesThisSpikeBelongToPlayer(destinationSpike, whoseTurnIsIt())) {
                            spikePairs.add(new SpikePair(spike, destinationSpike));
                        }
                    }
                }
            }
        } else {
            log(" warnign dice roll was under 0 - this indicates no options for this player");
        }
        log("finished getValidOptions");
        return spikePairs;
    }

    public static void setBotDestination(int x, int y, String desc) {
        if (Bot.destX != x || Bot.destY != y) {
            log("NEW BOT DEST: " + x + "," + y + ":" + desc);
            Bot.destX = x;
            Bot.destY = y;
        }
    }

    private void theyWantToPlaceAPiece() {
        if (CustomCanvas.barPieceStuckOnMouse) {
            log("dont do anythign til we place this");
            return;
        }
        if (SPtheMoveToMake == null) {
            log("DOUBLE RECALC.>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            return;
        }
        if (!Bot.getFullAutoPlay() && whoseTurnIsIt() == PlayerColor.WHITE) {
            return;
        }
        Spike dropOnMe = SPtheMoveToMake.dropPiecesOnMe;
        if (dropOnMe != null) {
            if (dropOnMe.isContainer()) {
                int pieceContainerX = geometry.containerX();
                int pieceContainerY = (whoseTurnIsIt() == PlayerColor.WHITE) ? geometry.whiteContainerY() :
                    geometry.blackContainerY();
                setBotDestination(pieceContainerX + geometry.containerWidth() / 2,
                    pieceContainerY + geometry.containerHeight() / 2,"PIECE CONTAINER DESTINATION");
            } else {
                Point middlePoint = dropOnMe.getMiddlePoint();
                setBotDestination(middlePoint.x, middlePoint.y, "NORMAL CASE DROP ON SPIKE A");
            }
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerColor player) {
        if (player == PlayerColor.WHITE) {
            currentPlayer = whitePlayer;
        } else {
            currentPlayer = blackPlayer;
        }
    }

    public void nextTurn() {
        if (currentPlayer.getColour() == PlayerColor.WHITE) {
            currentPlayer = blackPlayer;
            log("BLACKS TURN");
        } else {
            currentPlayer = whitePlayer;
            log("WHITES TURN");
        }
        SPtheMoveToMake = null;
    }

    // returns the current pip count of the given player.
    public int calculatePips(PlayerColor player) {
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
        if (player == PlayerColor.WHITE) {
            for (int i = 0; i < 24; i++) {
                Spike spike = spikes.get(i);
                pips += (i + 1) * spike.getAmountOfPieces(PlayerColor.WHITE);
            }
        } else {
            int j = 0;
            for (int i = 23; i >= 0; i--, j++) {
                Spike spike = spikes.get(i);
                pips += (j + 1) * spike.getAmountOfPieces(PlayerColor.BLACK);
            }
        }
        return pips;
    }

    public ArrayList<Spike> getSpikes() {
        return spikes;
    }

    public void rollDies() {
        die1.roll();
        die2.roll();
        die1HasBeenUsed = die2HasBeenUsed = false;
    }

    public boolean rolledDouble() {
        return die1.enabled() && die2.enabled() && (die1.getValue() == die2.getValue());
    }
    
    public PlayerColor whoseTurnIsIt() {
        return currentPlayer.getColour();
    }

    public void drawBlackPieceContainer(Graphics g) {
        boolean allPiecesAreHome = calculateAmountOfPiecesInHomeArea(blackPlayer) +
            CustomCanvas.blackPiecesSafelyInContainer.size() == 15;
        boolean pulsateBlackContainer = allPiecesAreHome && pulsateContainer(currentPlayer, activeSpikeId());
        drawPieceContainer(g, geometry.blackContainerY(), pulsateBlackContainer, allPiecesAreHome,
            CustomCanvas.blackPiecesSafelyInContainer.size());
    }

    public void drawWhitePieceContainer(Graphics g) {
        boolean allPiecesAreHome = calculateAmountOfPiecesInHomeArea(whitePlayer) +
            CustomCanvas.whitePiecesSafelyInContainer.size() == 15;
        boolean pulsateWhiteContainer = allPiecesAreHome && pulsateContainer(currentPlayer, activeSpikeId());
        drawPieceContainer(g, geometry.whiteContainerY(), pulsateWhiteContainer, allPiecesAreHome,
            CustomCanvas.whitePiecesSafelyInContainer.size());
    }

    private void drawPieceContainer(Graphics g, int topY, boolean pulsateContainer, boolean allPiecesAreHome,
                                    int piecesOnContainer) {
        if (allPiecesAreHome) {
            utils.setColor(g, Color.GREEN);
            if (pulsateContainer) {
                utils.setColor(g, Color.YELLOW);
            }
        } else {
            utils.setColor(g, Color.WHITE);
        }
        final int myX = geometry.containerX();
        int myY = topY;
        for (int i = 0; i < 15; i++) {
            myY += geometry.containerSubSize();
            if (i < piecesOnContainer) {
                Color originalColor = utils.getColor();
                utils.setColor(g, Color.ORANGE);
                utils.fillRect(g, myX, myY, geometry.containerWidth(), geometry.containerSubSize());
                utils.setColor(g, originalColor);
            }
            utils.drawRect(g, myX, myY, geometry.containerWidth(), geometry.containerSubSize());
        }
    }

    private boolean allPiecesAreHome() {
        int inContainer = currentPlayer.isWhite() ?  CustomCanvas.whitePiecesSafelyInContainer.size() :
            CustomCanvas.blackPiecesSafelyInContainer.size();
        return calculateAmountOfPiecesInHomeArea(currentPlayer) +  inContainer == 15;
    }

    private Spike lastNotEmptyWhiteSpike() {
        for (int i = 23; i >= 0; --i) {
            if (spikes.get(i).getAmountOfPieces(PlayerColor.WHITE) > 0)
                return spikes.get(i);
        }
        return null;
    }

    private Spike lastNotEmptyBlackSpike() {
        for (Spike spike: spikes) {
            if (spike.getAmountOfPieces(PlayerColor.BLACK) > 0)
                return spike;
        }
        return null;
    }

    private Spike lastNotEmptySpike() {
        return currentPlayer.isWhite() ? lastNotEmptyWhiteSpike() : lastNotEmptyBlackSpike();
    }

    /**
     * Calculates which dies get current piece to container, assuming that such die combination exist
     * @param player player
     * @param sourceSpikeId source spike id from where piece was picked
     * @return Die type
     * @throws Exception if source spike does not belong to player of container cannot be reached from it.
     */
    public DieType whichDieGetsUsToPieceContainer(Player player, int sourceSpikeId) throws Exception {
        int exactValue = player.isWhite() ? sourceSpikeId - player.containerId() : player.containerId() - sourceSpikeId;
        int maxDieValue = Math.max(die1.getValue(), die2.getValue());
        Spike sourceSpike = spikes.get(sourceSpikeId);
        if (sourceSpike.getAmountOfPieces(player.getColour()) == 0) {
            throw new Exception(String.format("Source spike does not belongs to %s player", player.getColour()));
        }

        ArrayList<Integer> reachableFromSource = reachableSpikes(sourceSpike, player, die1, die2);
        if (!reachableFromSource.contains(player.containerId())) {
            throw new  Exception(String.format("Container cannot be reached from spike %s die1: %d, die2: %d",
                sourceSpike.getName(), die1.getValue(), die2.getValue()));
        }
        if (die1.getValue() == exactValue)
            return DieType.DIE1;
        if (die2.getValue() == exactValue)
            return DieType.DIE2;
        if (maxDieValue > exactValue) {
            return die1.getValue() >= die2.getValue() ? DieType.DIE1 : DieType.DIE2;
        }
        return DieType.DIE1AND2;
    }

    public ArrayList<Spike> spikesToMoveToFromBar(PlayerColor playerColor) {
        ArrayList<Spike> result = new ArrayList<>();
        canWeGetOffTheBarWithThisDie(die1, DieType.DIE1, result);
        canWeGetOffTheBarWithThisDie(die2, DieType.DIE2, result);
        return result;
    }

    public boolean haveToMovePieceFromBar(PlayerColor playerColor) {
        return (playerColor == PlayerColor.WHITE && CustomCanvas.theBarWHITE.size() > 0) ||
        (playerColor == PlayerColor.BLACK && CustomCanvas.theBarBLACK.size() > 0);
    }

    public boolean pulsateContainer(Player player, int sourceSpikeId) {
        return sourceSpikeId >= 0 &&
            reachableSpikes(spikes.get(sourceSpikeId), player, die1, die2).contains(currentPlayer.containerId());
    }

    /**
     * Returns id of currently active spike.
     * @return Either id of the spike the piece is dragged from or id of the spike the mouse points on.
     */
    private int activeSpikeId() {
        int spikeId = -1;
        if (CustomCanvas.pieceStuckToMouse != null)
            spikeId = CustomCanvas.pieceStuckToMouse.sourceSpikeId();
        else {
            Spike spike = grabSpikeHoveringOver();
            if (spike != null && spike.getAmountOfPieces(whoseTurnIsIt()) > 0) {
                spikeId = spike.getSpikeNumber();
            }
        }
        return spikeId;
    }
}
