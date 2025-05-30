package cz.cvut.fel.pjv.logic;

import java.util.ArrayList;

/**
 * MoveRules Class to determine valid moves
 * Used each round in Player checkMove method
 * Created in Controller class
 */
public class MoveRules {

    /**
     * ArrayList of lists of moves that can be performed after first half of push move
     */
    private final ArrayList<ArrayList<Integer>> movesAfterPushArray;

    /**
     * Array of booleans if move was first half of push or not
     */
    private final boolean[] pushCheck;

    /**
     * Array of figure chars to determine figures turn before
     */
    private final char[] figuresTurnBefore;

    /**
     * Array of horizontal and vertical coords of tiles turn before
     */
    private final int[][] xyCoordsArray;

    /**
     * Needed to use Board class
     */
    private final Board board;

    /**
     * Constructor to initialize attributes and Board class to use it
     * @param board used for methods and attributes from Board class
     */
    public MoveRules(Board board) {
        this.movesAfterPushArray = new ArrayList<>(2);
        this.pushCheck = new boolean[4];
        this.figuresTurnBefore = new char[2];
        this.xyCoordsArray = new int[2][2];
        for (int i = 0; i < 2; i++) {
            figuresTurnBefore[i] = ' ';
            for (int j = 0; j < 2; j++) {
                xyCoordsArray[i][j] = -1;
            }
        }
        this.board = board;
    }

    /**
     * Constructor to initialize attributes from loaded values
     * @param movesAfterPushArray array of moves player needs to do after push
     * @param pushCheck array of booleans if push was made or not in a step
     * @param figuresTurnBefore array of char figures to determine figure
     * @param xyCoordsArray array of coords of last played figure
     * @param board used for methods and attributes from Board class
     */
    public MoveRules(ArrayList<ArrayList<Integer>> movesAfterPushArray, boolean[] pushCheck,
                     char[] figuresTurnBefore, int[][] xyCoordsArray, Board board) {
        this.movesAfterPushArray = movesAfterPushArray;
        this.pushCheck = pushCheck;
        this.figuresTurnBefore = figuresTurnBefore;
        this.xyCoordsArray = xyCoordsArray;
        this.board = board;
    }

    /**
     * Rules for standard moves used in Player class method checkMove
     * @param player needed to determine player
     * @param x horizontal position of the figure
     * @param y vertical position of the figure
     * @return all possible moves in arrayList of Integers
     */
    public ArrayList<Integer> moveRule(Player player, int x, int y) {
        ArrayList<Integer> moves = new ArrayList<>();
        // Check if player did first half of push and now must go with his figure to finish the whole push move
        if (player.getMoveCount() > 0 && pushCheck[player.getMoveCount() - 1]) {
            ArrayList<Integer> movesAfterPush = player.getMoveCount() < 3 ? movesAfterPushArray.getFirst() : movesAfterPushArray.getLast();
            int[] targetCoords = player.getMoveCount() < 3 ? xyCoordsArray[0] : xyCoordsArray[1];
            for (int i = 0; i < movesAfterPush.size(); i += 2) {
                if (movesAfterPush.get(i) == x && movesAfterPush.get(i + 1) == y) {
                    moves.add(targetCoords[0]);
                    moves.add(targetCoords[1]);
                    break;
                }
            }
            return moves;
        }
        // Check for possible moves based on figure type
        char figure = board.getFigure(x, y);
        int[][] offsets = switch (figure) {
            case 'R' -> new int[][]{{1, 0}, {0, -1}, {0, 1}};
            case 'r' -> new int[][]{{-1, 0}, {0, -1}, {0, 1}};
            default -> new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        };
        for (int[] offset : offsets) {
            int newX = x + offset[0];
            int newY = y + offset[1];
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8 && board.getFigure(newX, newY) == ' ') {
                moves.add(newX);
                moves.add(newY);
            }
        }
        if (!moves.isEmpty()) {
            setAttributes(player, x, y, figure, false);
        }
        return moves;
    }

    /**
     * Rules for special push move used in Player class method checkMove
     * @param player needed to determine player
     * @param x horizontal position of the figure
     * @param y vertical position of the figure
     * @return all possible moves in arrayList of Integers
     */
    public ArrayList<Integer> pushRule(Player player, int x, int y) {
        // Check if player did takeback one or two times or did not takeback
        // Two takebacks after two pushes
        if (movesAfterPushArray.size() == 2 && player.getMoveCount() == 0) {
            movesAfterPushArray.clear();
        // One takeback after two pushes
        } else if (movesAfterPushArray.size() == 2) {
            movesAfterPushArray.removeLast();
        // One takeback after one push as first move
        } else if (movesAfterPushArray.size() == 1 && player.getMoveCount() < 2) {
            movesAfterPushArray.removeLast();
        // One takeback after one push as second move
        } else if (movesAfterPushArray.size() == 1 && player.getMoveCount() == 2 && pushCheck[2]) {
            movesAfterPushArray.removeLast();
        }
        ArrayList<Integer> moves = new ArrayList<>();
        ArrayList<Integer> movesAfterPush = new ArrayList<>();
        char figure = board.getFigure(x, y);
        int figureStrength = Figure.getFigureStrength(figure);
        // Check for possible push moves
        int[][] offsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] offset : offsets) {
            int newX = x + offset[0];
            int newY = y + offset[1];
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                char nextFigure = board.getFigure(newX, newY);
                int nextFigureStrength = Figure.getFigureStrength(nextFigure);
                if (nextFigure == ' ') {
                    moves.add(newX);
                    moves.add(newY);
                } else if (!freeze(newX, newY) && figureStrength < nextFigureStrength &&
                        Character.isUpperCase(figure) != Character.isUpperCase(nextFigure)) {
                    movesAfterPush.add(newX);
                    movesAfterPush.add(newY);
                }
            }
        }
        // Empty moves ArrayList if player cannot do push move
        if (movesAfterPush.isEmpty()) {
            moves.clear();
        }
        if (!moves.isEmpty()) {
            setAttributes(player, x, y, board.getFigure(x, y), true);
            movesAfterPushArray.add(movesAfterPush);
        }
        return moves;
    }

    /**
     * Rules for special pull move used in Player class method checkMove
     * @param player needed to determine player
     * @param x horizontal position of the figure
     * @param y vertical position of the figure
     * @return all possible moves in arrayList of Integers
     */
    public ArrayList<Integer> pullRule(Player player, int x, int y) {
        ArrayList<Integer> moves = new ArrayList<>();
        int prevX, prevY;
        char figureTurnBefore;
        // Check if pull was done in the first to steps or not
        if (player.getMoveCount() < 3) {
            prevX = xyCoordsArray[0][0];
            prevY = xyCoordsArray[0][1];
            figureTurnBefore = figuresTurnBefore[0];
        } else {
            prevX = xyCoordsArray[1][0];
            prevY = xyCoordsArray[1][1];
            figureTurnBefore = figuresTurnBefore[1];
        }
        if (prevX == -1 || prevY == -1 || figureTurnBefore == ' ' || board.getFigure(prevX, prevY) != ' ') {
            return moves;
        }
        int offsetX = prevX - x;
        int offsetY = prevY - y;
        char figure = board.getFigure(x, y);
        // Check for possible pull moves
        if (Math.abs(offsetX) <= 1 && Math.abs(offsetY) <= 1 && (offsetX == 0 || offsetY == 0) &&
                Figure.getFigureStrength(figure) < Figure.getFigureStrength(figureTurnBefore)) {
            moves.add(prevX);
            moves.add(prevY);
        }
        return moves;
    }

    /**
     * Rules to determine if figure is frozen or not
     * Used in Player class method checkMove
     * @param x horizontal position of the figure
     * @param y vertical position of the figure
     * @return True, if figure is frozen, false otherwise
     */
    public boolean freeze(int x, int y) {
        char figure = board.getFigure(x, y);
        // Conditions to get figures next to the player figure
        char figureN = (y > 0) ? board.getFigure(x, y - 1) : ' ';
        char figureS = (y < 7) ? board.getFigure(x, y + 1) : ' ';
        char figureW = (x > 0) ? board.getFigure(x - 1, y) : ' ';
        char figureE = (x < 7) ? board.getFigure(x + 1, y) : ' ';
        int figureStrength = Figure.getFigureStrength(figure);
        int figureStrengthN = Figure.getFigureStrength(figureN);
        int figureStrengthS = Figure.getFigureStrength(figureS);
        int figureStrengthW = Figure.getFigureStrength(figureW);
        int figureStrengthE = Figure.getFigureStrength(figureE);
        // Conditions to determine if player figure is frozen or not
        if (Character.isUpperCase(figure) && (Character.isUpperCase(figureN) || Character.isUpperCase(figureS)
                || Character.isUpperCase(figureW) || Character.isUpperCase(figureE))) {
            return false;
        } else if (Character.isLowerCase(figure) && (Character.isLowerCase(figureN) || Character.isLowerCase(figureS)
                || Character.isLowerCase(figureW) || Character.isLowerCase(figureE))) {
            return false;
        } else {
            return figureStrength < figureStrengthN || figureStrength < figureStrengthS
                    || figureStrength < figureStrengthW || figureStrength < figureStrengthE;
        }
    }

    /**
     * Setter for all checking attributes in moveRule or pushRule if there is/are valid move(s)
     * @param player needed to determine player
     * @param x horizontal position of tile
     * @param y vertical position of tile
     * @param figure char figure to be set
     * @param check bool to set if push or not
     */
    public void setAttributes(Player player, int x, int y, char figure, boolean check) {
        if (player.getMoveCount() < 2) {
            this.xyCoordsArray[0][0] = x;
            this.xyCoordsArray[0][1] = y;
            this.figuresTurnBefore[0] = figure;
        } else {
            this.xyCoordsArray[1][0] = x;
            this.xyCoordsArray[1][1] = y;
            this.figuresTurnBefore[1] = figure;
        }
        this.pushCheck[player.getMoveCount()] = check;
    }

    /**
     * Resets all checking attributes needed for special moves after the end of each round
     */
    public void resetAttributes() {
        for (int i = 0; i < 2; i++) {
            this.figuresTurnBefore[i] = ' ';
            this.pushCheck[i] = false;
            this.pushCheck[i + 2] = false;
            for (int j = 0; j < 2; j++) {
                this.xyCoordsArray[i][j] = -1;
            }
        }
        this.movesAfterPushArray.clear();
    }

    /**
     * Check if player has to play specific figures to complete push move
     * @param player needed to determine player
     * @return True if player has to, false otherwise
     */
    public boolean isPushCheck(Player player) {
        return pushCheck[player.getMoveCount() - 1];
    }

    /**
     * Getter for movesAfterPushArray
     * @return movesAfterPushArray
     */
    public ArrayList<ArrayList<Integer>> getMovesAfterPushArray() {
        return movesAfterPushArray;
    }

    /**
     * Getter for pushCheck
     * @return pushCheck
     */
    public boolean[] getPushCheck() {
        return pushCheck;
    }

    /**
     * Getter for figuresTurnBefore
     * @return figuresTurnBefore
     */
    public char[] getFiguresTurnBefore() {
        return figuresTurnBefore;
    }

    /**
     * Getter for xyCoordsArray
     * @return xyCoordsArray
     */
    public int[][] getXyCoordsArray() {
        return xyCoordsArray;
    }

}
