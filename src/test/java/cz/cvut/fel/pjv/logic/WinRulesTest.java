package cz.cvut.fel.pjv.logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the WinRules win methods
 */
public class WinRulesTest {

    private Notation notation;
    private Board board;
    private HumanPlayer one;
    private WinRules winRules;

    @BeforeEach
    public void setUp() {
        notation = new Notation();
        board = new Board(notation);
        winRules = board.getWinRules();
        MoveRules moveRules = new MoveRules(board);
        one = new HumanPlayer("One", false, "Gold", notation, board, moveRules);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.initialize(i, j, null);
            }
        }
    }

    @Test
    public void testWinRabbits() {
        board.changeFigure(7, 7, 'R');
        // Assert winRabbits method
        Assertions.assertTrue(winRules.winRabbits(one));
    }

    @Test
    public void testLossNoRabbits() {
        // Assert lossNoRabbits method
        Assertions.assertTrue(winRules.lossNoRabbits(one));
    }

    @Test
    public void testLossNoMoves() {
        board.changeFigure(0, 0, 'R');
        board.changeFigure(1, 0, 'e');
        board.changeFigure(0, 1, 'm');
        // Assert lossNoMoves method
        Assertions.assertTrue(winRules.lossNoMoves(one));
    }

    @Test
    public void testMaxRoundsReached() {
        for (int i = 0; i < 50; i++) {
            notation.addRound();
        }
        board.changeFigure(0, 0, 'R');
        board.changeFigure(0, 1, 'R');
        board.changeFigure(1, 0, 'r');
        // Assert maxRoundsReached method
        Assertions.assertTrue(winRules.maxRoundsReached());
    }

}
