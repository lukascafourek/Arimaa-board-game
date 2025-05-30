package cz.cvut.fel.pjv.logic;

import java.util.Arrays;

/**
 * Class used for the game board
 * HAS-A relation to the Notation class
 */
public class Board {

    /**
     * Needed to be able to know board index in boardArray
     */
    private int boardIndex;

    /**
     * Game board as 2D array
     */
    private final char[][] board;

    /**
     * List of game boards as 3D array
     */
    private final char[][][] boardArray;

    /**
     * Needed to use Notation methods
     */
    private final Notation notation;

    /**
     * Needed to use WinRules methods
     */
    private final WinRules winRules;

    /**
     * Constructor to initialize sizes of arrays and use notation
     * @param notation needed to use Notation methods and attributes
     */
    public Board(Notation notation) {
        this.boardIndex = 0;
        this.board = new char[8][8];
        this.boardArray = new char[400][8][8];
        this.notation = notation;
        this.winRules = new WinRules(this);
    }

    /**
     * Constructor to initialize attributes from loaded values
     * @param boardIndex index of current board
     * @param board current state of board
     * @param boardArray array of saved boards
     * @param notation Notation class to use it
     */
    public Board(int boardIndex, char[][] board, char[][][] boardArray, Notation notation) {
        this.boardIndex = boardIndex;
        this.board = board;
        this.boardArray = boardArray;
        this.notation = notation;
        this.winRules = new WinRules(this);
    }

    /**
     * Places figures on board before game starts
     * Used in MainGame createGridCells method
     * @param x horizontal position of figure
     * @param y vertical position of figure
     */
    public void initialize(int x, int y, String imagePath) {
        char figure;
        if (imagePath == null) {
            figure = ' ';
        } else {
            figure = imagePath.charAt(1);
        }
        board[x][y] = figure;
    }

    /**
     * Changes figure to desired one
     * @param x horizontal position of figure
     * @param y vertical position of figure
     * @param figure char that is to be placed
     */
    public void changeFigure(int x, int y, char figure) {
        board[x][y] = figure;
    }

    /**
     * Gets the char figure on the board
     * @param x horizontal position/index of the board
     * @param y vertical position/index of the board
     * @return element of the board as char figure
     */
    public char getFigure(int x, int y) {
        return board[x][y];
    }

    /**
     * Removes figure if it is on tiles c3, c6, f3 or f6 according to game moveRules
     * Used in Controller class after each move
     * @param x horizontal position of figure
     * @param y vertical position of figure
     */
    public void removeFigure(int x, int y) {
        board[x][y] = ' ';
    }

    /**
     * Chooses winner from the two players according to WinRules
     * Used at the end of each round, uses WinRules class
     * @param currentPlayer player who made the last move
     * @param nextPlayer player whose turn is next
     * @return name of the player who wins and how he wins or null, if nobody wins that round
     */
    public String chooseWinner(Player currentPlayer, Player nextPlayer) {
        if (winRules.winRabbits(currentPlayer)) {
            return currentPlayer.getName() + ": Their rabbit reached goal!";
        }
        if (winRules.winRabbits(nextPlayer)) {
            return nextPlayer.getName() + ": Their rabbit reached goal!";
        }
        if (winRules.lossNoRabbits(nextPlayer)) {
            return currentPlayer.getName() + ": Opponent has lost all rabbits!";
        }
        if (winRules.lossNoRabbits(currentPlayer)) {
            return nextPlayer.getName() + ": Opponent has lost all rabbits!";
        }
        if (winRules.lossNoMoves(nextPlayer)) {
            return currentPlayer.getName() + ": Opponent has no moves!";
        }
        if (notation.getRound() >= 50 && currentPlayer.getColor().equals("Silver")) {
            if (winRules.maxRoundsReached()) {
                return nextPlayer.getName() + ": The have more figures!";
            } else {
                return currentPlayer.getName() + ": They have same amount or more figures!";
            }
        }
        return null;
    }

    /**
     * Decrement boardIndex by 1
     */
    public void decBoardIndex() {
        boardIndex--;
    }

    /**
     * Removes last board saved in the boardArray
     * and deep copies the previous board to current board
     * Used in valid takeback
     */
    public void removeLastBoard() {
        boardArray[boardIndex] = null;
        for (int i = 0; i < 8; i++) {
            board[i] = Arrays.copyOf(boardArray[boardIndex - 1][i], 8);
        }
    }

    /**
     * Saves the board to the boardArray as deep copy
     * Increment boardIndex by 1
     * Used at the end of each move
     */
    public void saveMatrix() {
        char[][] tmpBoard = new char[8][8];
        for (int i = 0; i < 8; i++) {
            tmpBoard[i] = Arrays.copyOf(board[i], 8);
        }
        boardArray[boardIndex] = tmpBoard;
        boardIndex++;
    }

    /**
     * Loads the board from the boardArray
     * @param index used to get the element (board) of the boardArray
     * @return a whole one board is returned
     */
    public char[][] loadBoard(int index) {
        return boardArray[index];
    }

    /**
     * Getter for boardIndex
     * @return boardIndex as int
     */
    public int getBoardIndex() {
        return boardIndex;
    }

    /**
     * Getter for board matrix
     * @return board as matrix
     */
    public char[][] getBoard() {
        return board;
    }

    /**
     * Getter for boardArray
     * @return boardArray
     */
    public char[][][] getBoardArray() {
        return boardArray;
    }

    /**
     * Getter for WinRules class
     * @return WinRules class
     */
    public WinRules getWinRules() {
        return winRules;
    }

}
