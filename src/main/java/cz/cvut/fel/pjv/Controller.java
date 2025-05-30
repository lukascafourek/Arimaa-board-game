package cz.cvut.fel.pjv;

import cz.cvut.fel.pjv.gui.MainGame;
import cz.cvut.fel.pjv.logic.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Class for combining MainGame gui with logic
 * Used in Start or Load class
 */
public class Controller {

    /**
     * Needed to get the right board and notation
     */
    private int index;

    /**
     * Needed to store possible moves of player figure
     */
    private ArrayList<Integer> possibleMoves;

    /**
     * Needed to use MainGame class
     */
    private MainGame mainGame;

    /**
     * Needed to potentially use AiPlayer
     */
    private AiPlayer aiPlayer = null;

    /**
     * Needed to potentially use Gold HumanPlayer
     */
    private HumanPlayer goldHumanPlayer = null;

    /**
     * Needed to potentially use Silver HumanPlayer
     */
    private HumanPlayer silverHumanPlayer = null;

    /**
     * Needed to use Notation class
     */
    private final Notation notation;

    /**
     * Needed to use Board class
     */
    private final Board board;

    /**
     * Needed to use Time class
     */
    private final Time time;

    /**
     * Needed to use MoveRules class
     */
    private final MoveRules moveRules;

    /**
     * Needed to potentially load saved game
     */
    private GameData gameData = null;

    /**
     * Initialization of logic classes if playing new game
     * @param playerOne Gold player name
     * @param playerTwo Silver player name
     * @param aiOne Gold AiPlayer yes or no
     * @param aiTwo Silver AiPlayer yes or no
     */
    public Controller(String playerOne, String playerTwo, boolean aiOne, boolean aiTwo) {
        possibleMoves = new ArrayList<>();
        this.index = 0;
        time = new Time();
        notation = new Notation();
        board = new Board(notation);
        moveRules = new MoveRules(board);
        if (aiOne) {
            aiPlayer = new AiPlayer(playerOne, true, "Gold", notation, board, moveRules);
            silverHumanPlayer = new HumanPlayer(playerTwo, false, "Silver", notation, board, moveRules);
        } else if (aiTwo) {
            goldHumanPlayer = new HumanPlayer(playerOne, false, "Gold", notation, board, moveRules);
            aiPlayer = new AiPlayer(playerTwo, true, "Silver", notation, board, moveRules);
        } else {
            goldHumanPlayer = new HumanPlayer(playerOne, false, "Gold", notation, board, moveRules);
            silverHumanPlayer = new HumanPlayer(playerTwo, false, "Silver", notation, board, moveRules);
        }
    }

    /**
     * Initialization of logic classes if playing saved game
     * @param gameData class to initialize class attributes from
     */
    public Controller(GameData gameData) {
        this.gameData = gameData;
        possibleMoves = new ArrayList<>();
        this.index = gameData.controllerIndex;
        time = new Time(gameData.timeSeconds, gameData.timeMinutes);
        notation = new Notation(gameData.notationListOfNotations, gameData.notationRound);
        board = new Board(gameData.boardBoardIndex, gameData.boardBoard, gameData.boardBoardArray, notation);
        moveRules = new MoveRules(gameData.goldRulesMovesAfterPushArray, gameData.goldRulesCheckPush,
                gameData.goldRulesFiguresTurnBefore, gameData.goldRulesXyArray, board);
        if (gameData.goldPlayerType) {
            aiPlayer = new AiPlayer(gameData.goldPlayerName, true, gameData.goldPlayerColor,
                    gameData.goldPlayerMoveCount, notation, board, moveRules);
            silverHumanPlayer = new HumanPlayer(gameData.silverPlayerName, false, gameData.silverPlayerColor,
                    gameData.silverPlayerMoveCount, notation, board, moveRules);
        } else if (gameData.silverPlayerType) {
            goldHumanPlayer = new HumanPlayer(gameData.goldPlayerName, false, gameData.goldPlayerColor,
                    gameData.goldPlayerMoveCount, notation, board, moveRules);
            aiPlayer = new AiPlayer(gameData.silverPlayerName, true, gameData.silverPlayerColor,
                    gameData.silverPlayerMoveCount, notation, board, moveRules);
        } else {
            goldHumanPlayer = new HumanPlayer(gameData.goldPlayerName, false, gameData.goldPlayerColor,
                    gameData.goldPlayerMoveCount, notation, board, moveRules);
            silverHumanPlayer = new HumanPlayer(gameData.silverPlayerName, false, gameData.silverPlayerColor,
                    gameData.silverPlayerMoveCount, notation, board, moveRules);
        }
    }

    /**
     * Combine gui with logic when changing figures at the start in the first round
     * @param turn needed to determine player
     */
    public void startGame(boolean turn) {
        // Adding player figure placements to notation
        if (turn) {
            notation.addMove(notation.getRound() + "g");
        } else {
            notation.addMove("\n" + notation.getRound() + "s");
        }
        char[][] matrix = board.getBoard();
        if (turn) {
            for (int i = 0; i < 2; i++) {
                innerCycle(matrix, i);
            }
        } else {
            for (int i = 6; i < 8; i++) {
                innerCycle(matrix, i);
            }
        }
        String joinedNotation = String.join("", notation.getNotation());
        mainGame.updateNotation(joinedNotation);
        if (!turn) {
            notation.addRound();
        }
        board.saveMatrix();
        notation.saveNotation();
        index++;
    }

    /**
     * helper method for startGame to add position to notation
     * @param matrix needed for cycle
     * @param i needed to determine row
     */
    private void innerCycle(char[][] matrix, int i) {
        for (int j = 0; j < 8; j++) {
            char v = (char) ('1' + i);
            char h = (char) ('a' + j);
            String position = " " + matrix[i][j] + h + v;
            notation.addMove(position);
        }
    }

    /**
     * Needed to determine if HumanPlayer chose a valid tile to play
     * @param x horizontal position of chosen tile
     * @param y vertical position of chosen tile
     * @param turn needed to determine which player is on the move
     * @return true if there is at least one possible move, false otherwise
     */
    public boolean checkMoveControl(int x, int y, boolean turn) {
        char figure = board.getFigure(x, y);
        if (figure == ' ') {
            return false;
        }
        if (turn) {
            possibleMoves = goldHumanPlayer.checkMove(x, y);
        } else {
            possibleMoves = silverHumanPlayer.checkMove(x, y);
        }
        if (possibleMoves.isEmpty()) {
            mainGame.showNoMovesWarningDialog();
        }
        mainGame.changeTileBackground(possibleMoves, true);
        return !possibleMoves.isEmpty();
    }

    /**
     * Adding move to notation after each move
     * @param x1y1 horizontal and vertical position of the first figure
     * @param x2 horizontal position of the second figure
     * @param y2 vertical position of the second figure
     * @return move as a String to be added to notation
     */
    public String updateNotationControl(int[] x1y1, int x2, int y2) {
        char figure = board.getFigure(x1y1[0], x1y1[1]);
        char v = (char) ('1' + x1y1[0]);
        char h = (char) ('a' + x1y1[1]);
        String position = "";
        if (x1y1[0] > x2) {
            position = " " + figure + h + v + "n";
        } else if (x1y1[0] < x2) {
            position = " " + figure + h + v + "s";
        } else if (x1y1[1] > y2) {
            position = " " + figure + h + v + "w";
        } else if (x1y1[1] < y2) {
            position = " " + figure + h + v + "e";
        }
        return position;
    }

    /**
     * Combine gui with logic to control AiPlayer start figure changes
     * Used at the start of the game if AiPlayer is playing
     * @param grid needed to determine tile
     * @param turn needed for changeFiguresControl method
     */
    public void aiStartControl(JPanel grid, boolean turn) {
        for (int i = 0; i < aiPlayer.randomNumberOfChanges(); i++) {
            aiControlHelper(grid, turn, false);
        }
    }

    /**
     * Combine gui and logic to control AiPlayer moves
     * Used in each turn if AiPlayer is playing
     * @param grid needed to determine tile
     * @param turn needed for figureMoveControl, moveAndBack and takebackControl methods
     */
    public void aiMoveControl(JPanel grid, boolean turn) {
        while (aiPlayer.nextMove()) {
            aiControlHelper(grid, turn, true);
        }
    }

    /**
     * Helper method for aiMoveControl and aiStartControl methods
     * initialization of chosen figure, move and inner methods arguments
     * @param grid needed to determine tile
     * @param turn needed for figureMoveControl, moveAndBack, takebackControl and changeFiguresControl methods
     * @param type needed to determine if AiPlayer wants to do figure change or move
     */
    private void aiControlHelper(JPanel grid, boolean turn, boolean type) {
        ArrayList<Integer> firstFigureCoords;
        if (type) {
            // Choose valid figure to move with
            firstFigureCoords = aiPlayer.chooseFigure();
        } else {
            // Choose first figure to change
            firstFigureCoords = aiPlayer.chooseStartFigureChange();
        }
        int[] x1y1 = new int[2];
        x1y1[0] = firstFigureCoords.getFirst();
        x1y1[1] = firstFigureCoords.getLast();
        ArrayList<Integer> secondFigureCoords;
        if (type) {
            // Choose valid move for the chosen figure and add it to possibleMoves
            secondFigureCoords = aiPlayer.chooseMove(x1y1[0], x1y1[1]);
            possibleMoves.addAll(secondFigureCoords);
        } else {
            // Choose second figure to change
            secondFigureCoords = aiPlayer.chooseStartFigureChange();
        }
        // Below is setup of arguments for figureMoveControl or changeFiguresControl methods
        int x2 = secondFigureCoords.getFirst();
        int y2 = secondFigureCoords.getLast();
        JPanel[] cells = new JPanel[2];
        cells[0] = (JPanel) grid.getComponent(x1y1[0] * 8 + x1y1[1]);
        cells[1] = (JPanel) grid.getComponent(x2 * 8 + y2);
        JLabel[] twoLabels = new JLabel[2];
        twoLabels[0] = (JLabel) cells[0].getComponent(0);
        twoLabels[1] = (JLabel) cells[1].getComponent(0);
        String[] imagePaths = new String[2];
        char figureOne = board.getFigure(x1y1[0], x1y1[1]);
        if (Character.isUpperCase(figureOne)) {
            imagePaths[0] = "/" + figureOne + ".png";
        } else if (Character.isLowerCase(figureOne)) {
            imagePaths[0] = "/" + figureOne + figureOne + ".png";
        } else {
            imagePaths[0] = null;
        }
        char figureTwo = board.getFigure(x2, y2);
        if (Character.isUpperCase(figureTwo)) {
            imagePaths[1] = "/" + figureTwo + ".png";
        } else if (Character.isLowerCase(figureTwo)) {
            imagePaths[1] = "/" + figureTwo + figureTwo + ".png";
        } else {
            imagePaths[1] = null;
        }
        if (type) {
            figureMoveControl(x1y1, x2, y2, twoLabels, imagePaths, turn, grid);
            // If board is the same, takeback two moves
            if (moveAndBack(turn)) {
                takebackControl(grid, turn);
                takebackControl(grid, turn);
            }
        } else {
            changeFiguresControl(x1y1, x2, y2, twoLabels, imagePaths);
        }
    }

    /**
     * Combine gui with logic to move figures
     * Used after checkMove method and clicking empty tile on the board for HumanPlayer
     * or used in aiMoveControl method for AiPlayer
     * @param x1y1 horizontal and vertical position of the first tile
     * @param x2 horizontal position of the second tile
     * @param y2 vertical position of the second tile
     * @param twoLabels needed to change imageLabels in GUI
     * @param imagePaths needed to change imageNames in GUI
     * @param turn needed to determine player
     * @param grid needed for removeFigureControl
     */
    public void figureMoveControl(int[] x1y1, int x2, int y2, JLabel[] twoLabels, String[] imagePaths, boolean turn, JPanel grid) {
        boolean ret = false;
        // Check if player clicked valid empty tile to move figure
        for (int i = 0; i < possibleMoves.size(); i += 2) {
            int xCoord = possibleMoves.get(i);
            int yCoord = possibleMoves.get(i + 1);
            if (xCoord == x2 && yCoord == y2) {
                ret = true;
                break;
            }
        }
        mainGame.changeTileBackground(possibleMoves, false);
        possibleMoves.clear();
        // If player clicked valid empty tile
        if (ret) {
            if (turn) {
                if ((goldHumanPlayer == null && aiPlayer.getMoveCount() == 0) ||
                        (goldHumanPlayer != null && goldHumanPlayer.getMoveCount() == 0)) {
                    notation.addMove("\n" + notation.getRound() + "g");
                    mainGame.startNotation();
                }
            } else {
                if ((silverHumanPlayer == null && aiPlayer.getMoveCount() == 0) ||
                        (silverHumanPlayer != null && silverHumanPlayer.getMoveCount() == 0)) {
                    notation.addMove("\n" + notation.getRound() + "s");
                    mainGame.startNotation();
                }
            }
            String position = updateNotationControl(x1y1, x2, y2);
            changeFiguresControl(x1y1, x2, y2, twoLabels, imagePaths);
            String endNotation = position + removeFigureControl(grid);
            board.saveMatrix();
            notation.addMove(endNotation);
            notation.saveNotation();
            index++;
            if (turn) {
                if (goldHumanPlayer == null) {
                    aiPlayer.incMoveCount();
                } else {
                    goldHumanPlayer.incMoveCount();
                }
            } else {
                if (silverHumanPlayer == null) {
                    aiPlayer.incMoveCount();
                } else {
                    silverHumanPlayer.incMoveCount();
                }
            }
            mainGame.setMoveCount();
            mainGame.updateNotation(endNotation);
        }
    }

    /**
     * Combine gui with logic to change figures at the start
     * Used after clicking any figure on the board before both players clicked Ready button
     * For AiPlayer it is used in aiStartControl method
     * @param x1y1 horizontal and vertical position of the first figure
     * @param x2 horizontal position of the second figure
     * @param y2 vertical position of the second figure
     * @param twoLabels needed to change imageLabels in GUI
     * @param imagePaths needed to change imageNames in GUI
     */
    public void changeFiguresControl(int[] x1y1, int x2, int y2, JLabel[] twoLabels, String[] imagePaths) {
        char figure1 = board.getFigure(x1y1[0], x1y1[1]);
        char figure2 = board.getFigure(x2, y2);
        if (figure1 != figure2) {
            board.changeFigure(x1y1[0], x1y1[1], figure2);
            board.changeFigure(x2, y2, figure1);
            mainGame.imageChange(twoLabels[0], imagePaths[1]);
            mainGame.imageChange(twoLabels[1], imagePaths[0]);
        }
    }

    /**
     * Adds removed figures to notation
     * Uses removeFigureHelper for all four traps
     * @param grid needed to get JLabels
     * @return removed figures as String
     */
    private String removeFigureControl(JPanel grid) {
        return removeFigureHelper(grid, 2, 2) + removeFigureHelper(grid, 2, 5) +
                removeFigureHelper(grid, 5, 2) + removeFigureHelper(grid, 5, 5);
    }

    /**
     * Helper method to possibly remove figures on all four traps
     * @param grid needed to get JLabels
     * @param x horizontal position of tile
     * @param y vertical position of tile
     * @return String of figure removed figure or ""
     */
    private String removeFigureHelper(JPanel grid, int x, int y) {
        if ((x == 2 && y == 2) || (x == 2 && y == 5) || (x == 5 && y == 2) || (x == 5 && y == 5)) {
            char figure = board.getFigure(x, y);
            char figureN = board.getFigure(x - 1, y);
            char figureS = board.getFigure(x + 1, y);
            char figureW = board.getFigure(x, y - 1);
            char figureE = board.getFigure(x, y + 1);
            // Check if player has some of their figures next to the figure on the remove tile
            if (Character.isUpperCase(figure) && (Character.isUpperCase(figureN) || Character.isUpperCase(figureS)
                    || Character.isUpperCase(figureW) || Character.isUpperCase(figureE))) {
                return "";
            } else if (Character.isLowerCase(figure) && (Character.isLowerCase(figureN) || Character.isLowerCase(figureS)
                    || Character.isLowerCase(figureW) || Character.isLowerCase(figureE))) {
                return "";
            } else if (figure == ' ') {
                return "";
            }
            // If not remove the figure on the remove tile
            board.removeFigure(x, y);
            char v = (char) ('1' + x);
            char h = (char) ('a' + y);
            JPanel cell = (JPanel) grid.getComponent(x * 8 + y);
            JLabel label = (JLabel) cell.getComponent(0);
            mainGame.imageRemove(label);
            return " " + figure + h + v + "x";
        }
        return "";
    }

    /**
     * Checks if the board is still the same at the end of the round for the playing player
     * Used when player clicks End Turn button
     * @param turn needed to determine player
     * @return True, if board is the same, false otherwise
     */
    public boolean moveAndBack(boolean turn) {
        int move;
        if (turn) {
            if (goldHumanPlayer == null) {
                move = aiPlayer.getMoveCount();
            } else {
                move = goldHumanPlayer.getMoveCount();
            }
        } else {
            if (silverHumanPlayer == null) {
                move = aiPlayer.getMoveCount();
            } else {
                move = silverHumanPlayer.getMoveCount();
            }
        }
        char[][] matrix = board.loadBoard(board.getBoardIndex() - move - 1);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getFigure(i, j) != matrix[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if player needs to do push when they click End Turn button
     * If yes they cannot end turn, otherwise they can
     * @param turn needed to determine player on the turn
     * @return True if they must finish push, false otherwise
     */
    public boolean mustDoPush(boolean turn) {
        boolean result;
        if (turn) {
            if (goldHumanPlayer == null) {
                result = moveRules.isPushCheck(aiPlayer);
            } else {
                result = moveRules.isPushCheck(goldHumanPlayer);
            }
        } else {
            if (silverHumanPlayer == null) {
                result = moveRules.isPushCheck(aiPlayer);
            } else {
                result = moveRules.isPushCheck(silverHumanPlayer);
            }
        }
        return result;
    }

    /**
     * Combine gui with logic after clicking End Turn button
     * Used after clicking End Turn button
     * For AiPlayer it is used in aiMoveControl method
     * @param turn needed to determine player
     */
    public void endTurnControl(boolean turn) {
        if (turn) {
            if (goldHumanPlayer == null) {
                aiPlayer.resetMoveCount();
            } else {
                goldHumanPlayer.resetMoveCount();
            }
        } else {
            if (silverHumanPlayer == null) {
                aiPlayer.resetMoveCount();
            } else {
                silverHumanPlayer.resetMoveCount();
            }
            notation.addRound();
        }
        moveRules.resetAttributes();
        mainGame.setRound();
        mainGame.setMoveCount();
    }

    /**
     * Combine gui with takeback Participant method
     * Used after clicking Takeback button
     * For AiPlayer it is used in aiMoveControl method
     * @param grid needed to be able to determine tile on screen
     * @param turn needed to determine which player is on the move
     */
    public void takebackControl(JPanel grid, boolean turn) {
        if (turn) {
            if (goldHumanPlayer == null) {
                aiPlayer.decMoveCount();
            } else {
                goldHumanPlayer.decMoveCount();
            }
        } else {
            if (silverHumanPlayer == null) {
                aiPlayer.decMoveCount();
            } else {
                silverHumanPlayer.decMoveCount();
            }
        }
        board.decBoardIndex();
        index--;
        char[][] matrix = board.getBoard();
        char[][] previousMatrix = board.loadBoard(index - 1);
        changeBoardAndNotation(grid, matrix, previousMatrix);
        board.removeLastBoard();
        notation.removeLastMove();
        mainGame.setMoveCount();
    }

    /**
     * Combine gui with logic to display particular notation and board
     * Used after clicking <-- button
     * @param grid needed to be able to determine tile on screen
     */
    public void leftArrowControl(JPanel grid) {
        if (index > 2) {
            char[][] matrix = board.loadBoard(index - 1);
            index--;
            char[][] previousMatrix = board.loadBoard(index - 1);
            changeBoardAndNotation(grid, matrix, previousMatrix);
        }
    }

    /**
     * Combine gui with logic to display particular notation and board
     * Used after clicking --> button
     * @param grid needed to be able to determine tile on screen
     */
    public void rightArrowControl(JPanel grid) {
        if (index < board.getBoardIndex()) {
            char[][] matrix = board.loadBoard(index - 1);
            index++;
            char[][] nextMatrix = board.loadBoard(index - 1);
            changeBoardAndNotation(grid, matrix, nextMatrix);
        }
    }

    /**
     * Combine gui with logic to display current notation and board
     * Used after clicking tiles or other buttons than <-- and -->
     * @param grid needed to be able to determine tile on screen
     */
    public void resetBoardAndNotation(JPanel grid) {
        char[][] previousMatrix = board.loadBoard(index - 1);
        char[][] matrix = board.getBoard();
        index = board.getBoardIndex();
        changeBoardAndNotation(grid, previousMatrix, matrix);
    }

    /**
     * Helper method for changing board and notation
     * @param grid needed to be able to determine tile on screen
     * @param firstMatrix matrix that will change
     * @param secondMatrix matrix that will be changed to
     */
    private void changeBoardAndNotation(JPanel grid, char[][] firstMatrix, char[][] secondMatrix) {
        changeBoard(grid, firstMatrix, secondMatrix);
        changeNotation();
    }

    /**
     * Helper method for changing board
     * @param grid needed to be able to determine tile on screen
     * @param firstMatrix matrix that will change
     * @param secondMatrix matrix that will be changed to
     */
    private void changeBoard(JPanel grid, char[][] firstMatrix, char[][] secondMatrix) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (firstMatrix[i][j] != secondMatrix[i][j]) {
                    char figure = secondMatrix[i][j];
                    JPanel cell = (JPanel) grid.getComponent(i * 8 + j);
                    JLabel label = (JLabel) cell.getComponent(0);
                    if (Character.isUpperCase(figure)) {
                        String name = "/" + figure + ".png";
                        mainGame.imageChange(label, name);
                    } else if (Character.isLowerCase(figure)) {
                        String name = "/" + figure + figure + ".png";
                        mainGame.imageChange(label, name);
                    } else {
                        mainGame.imageRemove(label);
                    }
                }
            }
        }
    }

    /**
     * Helper method for changing notation
     * If game is loaded from savefile then it is used in MainGame class too
     */
    public void changeNotation() {
        ArrayList<ArrayList<String>> lists = notation.getListOfNotations();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < index; i++) {
            ArrayList<String> list = lists.get(i);
            for (String s : list) {
                builder.append(s);
            }
        }
        String result = builder.toString();
        mainGame.setNotationText(result);
    }

    /**
     * Combine gui with logic to choose winner
     * Used at the end of each round
     * @param current player who made the last move
     * @param next player whose turn is next
     * @param time if player exceeded round time they lose
     */
    public void chooseWinnerControl(Player current, Player next, boolean time) {
        String winner;
        // If player exceeded time limit for their round, they lose by time limit condition
        if (time) {
            winner = next.getName() + ": Opponent exceeded round time limit!";
        } else {
            winner = board.chooseWinner(current, next);
        }
        if (winner != null) {
            if (winner.contains(current.getName())) {
                notation.addMove(" lost\n");
                mainGame.endGameNotation(" lost\n");
            } else {
                notation.addMove(" won\n");
                mainGame.endGameNotation(" won\n");
            }
            mainGame.updateWinner(winner);
        }
    }

    /**
     * Setter used in MainGame class to set needed logic classes
     * @param mainGame needed to get reference to MainGame class
     */
    public void setMainGame(MainGame mainGame) {
        this.mainGame = mainGame;
        if (gameData != null) {
            if (goldHumanPlayer == null) {
                mainGame.setLogicClasses(gameData.mainGameTurn, gameData.mainGameWin, gameData.mainGameWinPlayer,
                        notation, board, time, aiPlayer, silverHumanPlayer);
            } else if (silverHumanPlayer == null) {
                mainGame.setLogicClasses(gameData.mainGameTurn, gameData.mainGameWin, gameData.mainGameWinPlayer,
                        notation, board, time, goldHumanPlayer, aiPlayer);
            } else {
                mainGame.setLogicClasses(gameData.mainGameTurn, gameData.mainGameWin, gameData.mainGameWinPlayer,
                        notation, board, time, goldHumanPlayer, silverHumanPlayer);
            }
        } else {
            if (goldHumanPlayer == null) {
                mainGame.setLogicClasses(notation, board, time, aiPlayer, silverHumanPlayer);
            } else if (silverHumanPlayer == null) {
                mainGame.setLogicClasses(notation, board, time, goldHumanPlayer, aiPlayer);
            } else {
                mainGame.setLogicClasses(notation, board, time, goldHumanPlayer, silverHumanPlayer);
            }
        }
    }

    /**
     * Method to save game used in MainGame class
     * Saving all needed attributes into GameData class
     * @param filename name of the file to be saved
     */
    public void saveGame(String filename) {
        GameData gameData = new GameData();
        gameData.controllerIndex = index;
        gameData.timeSeconds = time.getSeconds();
        gameData.timeMinutes = time.getMinutes();
        gameData.notationListOfNotations = notation.getListOfNotations();
        gameData.notationRound = notation.getRound();
        gameData.boardBoardIndex = board.getBoardIndex();
        gameData.boardBoard = board.getBoard();
        gameData.boardBoardArray = board.getBoardArray();
        if (goldHumanPlayer == null) {
            saveGameHelperGold(gameData, aiPlayer.getName(), aiPlayer.getType(),
                    aiPlayer.getColor(), aiPlayer.getMoveCount(), aiPlayer.getRules());
        } else {
            saveGameHelperGold(gameData, goldHumanPlayer.getName(), goldHumanPlayer.getType(),
                    goldHumanPlayer.getColor(), goldHumanPlayer.getMoveCount(), goldHumanPlayer.getRules());
        }
        if (silverHumanPlayer == null) {
            saveGameHelperSilver(gameData, aiPlayer.getName(), aiPlayer.getType(),
                    aiPlayer.getColor(), aiPlayer.getMoveCount(), aiPlayer.getRules());
        } else {
            saveGameHelperSilver(gameData, silverHumanPlayer.getName(), silverHumanPlayer.getType(),
                    silverHumanPlayer.getColor(), silverHumanPlayer.getMoveCount(), silverHumanPlayer.getRules());
        }
        gameData.mainGameTurn = mainGame.isTurn();
        gameData.mainGameWin = mainGame.isWin();
        gameData.mainGameWinPlayer = mainGame.getWinPlayer();
        SaveAndLoad.save(gameData, filename);
    }

    /**
     * Helper method for Gold player used in saveGame method
     * @param gameData class to be saved
     * @param name player name
     * @param type player type
     * @param color player color
     * @param moveCount player move count
     * @param moveRules player set moveRules
     */
    private void saveGameHelperSilver(GameData gameData, String name, boolean type, String color, int moveCount, MoveRules moveRules) {
        gameData.silverPlayerName = name;
        gameData.silverPlayerType = type;
        gameData.silverPlayerColor = color;
        gameData.silverPlayerMoveCount = moveCount;
        gameData.silverRulesMovesAfterPushArray = moveRules.getMovesAfterPushArray();
        gameData.silverRulesCheckPush = moveRules.getPushCheck();
        gameData.silverRulesFiguresTurnBefore = moveRules.getFiguresTurnBefore();
        gameData.silverRulesXyArray = moveRules.getXyCoordsArray();
    }

    /**
     * Helper method for Silver player used in saveGame method
     * @param gameData class to be saved
     * @param name player name
     * @param type player type
     * @param color player color
     * @param moveCount player move count
     * @param moveRules player set moveRules
     */
    private void saveGameHelperGold(GameData gameData, String name, boolean type, String color, int moveCount, MoveRules moveRules) {
        gameData.goldPlayerName = name;
        gameData.goldPlayerType = type;
        gameData.goldPlayerColor = color;
        gameData.goldPlayerMoveCount = moveCount;
        gameData.goldRulesMovesAfterPushArray = moveRules.getMovesAfterPushArray();
        gameData.goldRulesCheckPush = moveRules.getPushCheck();
        gameData.goldRulesFiguresTurnBefore = moveRules.getFiguresTurnBefore();
        gameData.goldRulesXyArray = moveRules.getXyCoordsArray();
    }

    /**
     * Getter for index
     * @return index
     */
    public int getIndex() {
        return index;
    }

}
