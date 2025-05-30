package cz.cvut.fel.pjv.logic;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class for AI player plying random
 * Extends HumanPlayer class
 */
public class AiPlayer extends HumanPlayer {

    /**
     * Needed to play choose random figures and moves
     */
    private final Random random;

    public AiPlayer(String name, boolean playerType, String color, Notation notation, Board board, MoveRules moveRules) {
        super(name, playerType, color, notation, board, moveRules);
        this.random = new Random();
    }

    public AiPlayer(String name, boolean type, String color, int moveCount, Notation notation, Board board, MoveRules moveRules) {
        super(name, type, color, moveCount, notation, board, moveRules);
        this.random = new Random();
    }

    /**
     * Only for AI, add all positions of initialized figures at the start
     * to choose figures and change them to ArrayList
     * @return position of a figure in ArrayList
     */
    public ArrayList<Integer> chooseStartFigureChange() {
        ArrayList<Integer> figures = new ArrayList<>();
        if (getColor().equals("Gold")) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 8; j++) {
                    figures.add(i);
                    figures.add(j);
                }
            }
        } else {
            for (int i = 6; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    figures.add(i);
                    figures.add(j);
                }
            }
        }
        return randomChooseTile(figures);
    }

    /**
     * Random number for change of figures at the start
     * @return number to determine changes
     */
    public int randomNumberOfChanges() {
        return random.nextInt(9);
    }

    /**
     * Only for AI, check possible moves for all figures
     * Random choose one of them
     * Used at the start of every move
     * @return integer ArrayList of two integers,
     * first is horizontal position, second is vertical position of the chosen figure
     */
    public ArrayList<Integer> chooseFigure() {
        ArrayList<Integer> chosenFigure = new ArrayList<>();
        ArrayList<Integer> figures = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getFigure(i, j) != ' ') {
                    if (!checkMove(i, j).isEmpty()) {
                        figures.add(i);
                        figures.add(j);
                    }
                }
            }
        }
        if (!figures.isEmpty()) {
            chosenFigure = randomChooseTile(figures);
        }
        return chosenFigure;
    }

    /**
     * Helper method for chooseFigure and chooseMove
     * Random choose tile
     * @param tiles needed to get tiles size
     * @return x and y coords of chosen tile in ArrayList
     */
    private ArrayList<Integer> randomChooseTile(ArrayList<Integer> tiles) {
        ArrayList<Integer> chosenFigure = new ArrayList<>();
        int choose = random.nextInt(tiles.size());
        if (choose % 2 != 0) {
            choose--;
        }
        chosenFigure.add(tiles.get(choose));
        chosenFigure.add(tiles.get(choose + 1));
        return chosenFigure;
    }

    /**
     * Only for AI, choose move for the figure specified by x and y coords
     * @param x horizontal position of the figure
     * @param y vertical position of the figure
     * @return ArrayList of horizontal and vertical coords of the chosen tile
     */
    public ArrayList<Integer> chooseMove(int x, int y) {
        ArrayList<Integer> moves = checkMove(x, y);
        return randomChooseTile(moves);
    }

    /**
     * Chooses if the AI player will do next move or not
     * Used after each performed move in the round
     * @return True if chosen, then do next move, otherwise false
     */
    public boolean nextMove() {
        if (getMoveCount() == 0) {
            return true;
        } else if (moveRules.isPushCheck(this)) {
            return true;
        } else if (getMoveCount() >= 4) {
            return false;
        }
        return random.nextBoolean();
    }

}
