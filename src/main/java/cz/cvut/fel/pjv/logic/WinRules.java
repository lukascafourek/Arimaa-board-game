package cz.cvut.fel.pjv.logic;

import java.util.ArrayList;

/**
 * WinRules class to determine winner
 * Used each round in Board chooseWinner method
 * Created in Board class
 */
public class WinRules {

    /**
     * Needed to use Board class
     */
    private final Board board;

    /**
     * Constructor to initialize attributes and Board class to use it
     * @param board used for methods and attributes from Board class
     */
    public WinRules(Board board) {
        this.board = board;
    }

    /**
     * Rule for win by having rabbit on the opponent`s last row
     * Used in Board class method chooseWinner
     * @param someone player at the end of their move
     * @return boolean value to choose if win
     */
    public boolean winRabbits(Player someone) {
        char[][] matrix = board.getBoard();
        if (someone.getColor().equals("Gold")) {
            for (int j = 0; j < 8; j++) {
                if (matrix[7][j] == 'R') {
                    return true;
                }
            }
        } else {
            for (int j = 0; j < 8; j++) {
                if (matrix[0][j] == 'r') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Rule for loss by having no rabbits
     * Used in Board class method chooseWinner
     * @param someone player at the end of their move
     * @return boolean value to choose if loss
     */
    public boolean lossNoRabbits(Player someone) {
        char[][] matrix = board.getBoard();
        if (someone.getColor().equals("Gold")) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 8; j++) {
                    if (matrix[i][j] == 'R') {
                        return false;
                    }
                }
            }
        } else {
            for (int i = 1; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (matrix[i][j] == 'r') {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Rule for loss by not being able to perform any move
     * Used in Board class method chooseWinner
     * @param someone player at the end of their move
     * @return boolean value to choose if loss
     */
    public boolean lossNoMoves(Player someone) {
        char[][] matrix = board.getBoard();
        ArrayList<Integer> move;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (matrix[i][j] != ' ') {
                    move = someone.checkMove(i, j);
                    if (!move.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Rule to determine winner if max amount of rounds is reached
     * Used in Board class method chooseWinner
     * @return boolean value to choose winner
     * True = Gold Player, False = Silver Player
     */
    public boolean maxRoundsReached() {
        int goldCounter = 0, silverCounter = 0;
        char[][] matrix = board.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Character.isUpperCase(matrix[i][j])) {
                    goldCounter++;
                } else if (Character.isLowerCase(matrix[i][j])) {
                    silverCounter++;
                }
            }
        }
        return goldCounter > silverCounter;
    }

}
