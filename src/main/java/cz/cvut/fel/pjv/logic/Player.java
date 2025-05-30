package cz.cvut.fel.pjv.logic;

import java.util.ArrayList;

/**
 * Abstract class for players with checkMove, getters and setters
 * HAS-A relations to Board and Notation classes
 * Predecessor of the HumanPlayer
 */
public abstract class Player {

    /**
     * Name of the player
     */
    protected String name;

    /**
     * Type of the player
     */
    protected boolean type;

    /**
     * Color of the player
     */
    protected String color;

    /**
     * Counter of moves in the current round, max 4
     */
    protected int moveCount;

    /**
     * Needed to use Notation class methods and attributes
     */
    protected Notation notation;

    /**
     * Needed to use Board class methods and attributes
     */
    protected Board board;

    /**
     * Needed to use MoveRules class methods
     */
    protected MoveRules moveRules;

    /**
     * Constructor to get the name of player, his type (human or AI) and his color (gold or silver),
     * also it initializes size of possible moves array, move counter and ret value.
     * Needs Notation and Board to use their methods and attributes
     * @param name given name of the player
     * @param type whether player clicked on AI checkbox or not
     * @param color first player is gold, second player is silver
     * @param notation needed to use Notation functions and attributes
     * @param board needed to use Board methods and attributes
     * @param moveRules needed to use MoveRules methods
     */
    public Player(String name, boolean type, String color, Notation notation, Board board, MoveRules moveRules) {
        this.name = name;
        this.type = type;
        this.color = color;
        this.notation = notation;
        this.board = board;
        this.moveCount = 0;
        this.moveRules = moveRules;
    }

    /**
     * Constructor to initialize all attributes from loaded values
     * when player wants to continue playing saved game
     * @param name given name of the player
     * @param type whether player clicked on AI checkbox or not
     * @param color first player is gold, second player is silver
     * @param moveCount player move count in a round
     * @param notation needed to use Notation functions and attributes
     * @param board needed to use Board methods and attributes
     * @param moveRules needed to use MoveRules methods
     */
    public Player(String name, boolean type, String color, int moveCount, Notation notation, Board board, MoveRules moveRules) {
        this.name = name;
        this.type = type;
        this.color = color;
        this.moveCount = moveCount;
        this.notation = notation;
        this.board = board;
        this.moveRules = moveRules;
    }

    /**
     * Checks whether it is possible to move with selected figure,
     * also uses MoveRules class method moveRule, pushRule, pullRule and freeze
     * Used whenever player chooses a figure on the board after the game started
     * @param x1 horizontal position of the selected figure
     * @param y1 vertical position of the selected figure
     * @return all possible moves as ArrayList of integers
     */
    public abstract ArrayList<Integer> checkMove(int x1, int y1);

    /**
     * Resets moveCount value
     * Used at the end of each round
     */
    public void resetMoveCount() {
        this.moveCount = 0;
    }

    /**
     * Getter for the player name
     * Used in Board, Controller and MainGame classes
     * @return player`s name as String
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the player type (human or AI)
     * Used in Controller and MainGame classes
     * @return playerType as boolean, True for AI, false for human
     */
    public boolean getType() {
        return type;
    }

    /**
     * Getter for the player color (gold or silver)
     * Used in Board, Controller, HumanPlayer, AiPlayer and WinRules class methods
     * @return player`s color as String
     */
    public String getColor() {
        return color;
    }

    /**
     * Getter for move count
     * Used at the end of each turn
     * @return moveCount as Integer
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * Increment player moveCount by 1
     */
    public void incMoveCount() {
        moveCount++;
    }

    /**
     * Decrement player moveCount by 1
     */
    public void decMoveCount() {
        moveCount--;
    }

    /**
     * Getter for MoveRules class, used in Controller class
     * @return MoveRules class
     */
    public MoveRules getRules() {
        return moveRules;
    }

}
