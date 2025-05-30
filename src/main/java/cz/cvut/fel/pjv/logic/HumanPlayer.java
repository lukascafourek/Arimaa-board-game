package cz.cvut.fel.pjv.logic;

import java.util.ArrayList;

/**
 * Class for playing human player
 * Extends Player abstract class
 * Predecessor of AiPlayer class
 */
public class HumanPlayer extends Player {

    public HumanPlayer(String name, boolean playerType, String color, Notation notation, Board board, MoveRules moveRules) {
        super(name, playerType, color, notation, board, moveRules);
    }

    public HumanPlayer(String name, boolean type, String color, int moveCount, Notation notation, Board board, MoveRules moveRules) {
        super(name, type, color, moveCount, notation, board, moveRules);
    }

    @Override
    public ArrayList<Integer> checkMove(int x1, int y1) {
        ArrayList<Integer> moves = new ArrayList<>();
        char[][] matrix = board.getBoard();
        // If player clicks on their figure
        if ((getColor().equals("Gold") && Character.isUpperCase(matrix[x1][y1])) ||
                (getColor().equals("Silver") && Character.isLowerCase(matrix[x1][y1]))) {
            boolean freeze = moveRules.freeze(x1, y1);
            if (!freeze) {
                moves = moveRules.moveRule(this, x1, y1);
            }
        // else if player clicks on opponent figure
        } else if (moveCount == 0 || !moveRules.isPushCheck(this)) {
            moves = moveRules.pullRule(this, x1, y1);
            if (moveCount < 3) {
               moves.addAll(moveRules.pushRule(this, x1, y1));
            }
        }
        return moves;
    }

}
