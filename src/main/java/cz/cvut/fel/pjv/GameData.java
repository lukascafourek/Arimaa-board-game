package cz.cvut.fel.pjv;

import java.util.ArrayList;

/**
 * Class to save game attributes and be saved to JSON file
 */
public class GameData {

    public int controllerIndex;
    public int timeSeconds;
    public int timeMinutes;
    public ArrayList<ArrayList<String>> notationListOfNotations = new ArrayList<>();
    public int notationRound;
    public int boardBoardIndex;
    public char[][] boardBoard = new char[8][8];
    public char[][][] boardBoardArray = new char[400][8][8];
    public String goldPlayerName;
    public String silverPlayerName;
    public boolean goldPlayerType;
    public boolean silverPlayerType;
    public String goldPlayerColor;
    public String silverPlayerColor;
    public int goldPlayerMoveCount;
    public int silverPlayerMoveCount;
    public ArrayList<ArrayList<Integer>> goldRulesMovesAfterPushArray = new ArrayList<>(2);
    public ArrayList<ArrayList<Integer>> silverRulesMovesAfterPushArray = new ArrayList<>(2);
    public boolean[] goldRulesCheckPush = new boolean[4];
    public boolean[] silverRulesCheckPush = new boolean[4];
    public char[] goldRulesFiguresTurnBefore = new char[2];
    public char[] silverRulesFiguresTurnBefore = new char[2];
    public int[][] goldRulesXyArray = new int[2][2];
    public int[][] silverRulesXyArray = new int[2][2];
    public boolean mainGameTurn;
    public boolean mainGameWin;
    public String mainGameWinPlayer;

}
