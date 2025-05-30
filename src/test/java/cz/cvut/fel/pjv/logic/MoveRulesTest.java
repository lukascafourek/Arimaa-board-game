package cz.cvut.fel.pjv.logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

/**
 * Test class for MoveRules move methods
 */
public class MoveRulesTest {

    private Board board;
    private HumanPlayer one;
    private MoveRules moveRules;

    @BeforeEach
    public void setUp() {
        Notation notation = new Notation();
        board = new Board(notation);
        moveRules = new MoveRules(board);
        one = new HumanPlayer("One", false, "Gold", notation, board, moveRules);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.initialize(i, j, null);
            }
        }
    }

    @Test
    public void testFigureFrozen() {
        board.changeFigure(0, 0, 'R');
        board.changeFigure(1, 0, 'e');
        board.changeFigure(0, 1, 'm');
        // Check if figure is frozen
        Assertions.assertTrue(moveRules.freeze(0, 0));
    }

    @Test
    public void testNormalMove() {
        board.changeFigure(0, 0, 'R');
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(1);
        expected.add(0);
        expected.add(0);
        expected.add(1);
        ArrayList<Integer> moves = moveRules.moveRule(one, 0, 0);
        // Assert if normal moves list has expected values
        Assertions.assertEquals(expected, moves);
    }

    @Test
    public void testPullMove() {
        board.changeFigure(0, 0, 'E');
        board.changeFigure(1, 0, 'r');
        one.checkMove(0, 0);
        board.changeFigure(0, 1, 'E');
        board.changeFigure(0, 0, ' ');
        ArrayList<Integer> moves = moveRules.pullRule(one, 1, 0);
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(0);
        expected.add(0);
        // Assert if pull move can be done
        Assertions.assertEquals(expected, moves);
    }

    @Test
    public void testPushMove() {
        board.changeFigure(0, 1, 'E');
        board.changeFigure(0, 0, 'r');
        ArrayList<Integer> moves = moveRules.pushRule(one, 0, 0);
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(1);
        expected.add(0);
        // Assert if push move can be done
        Assertions.assertEquals(expected, moves);

        board.changeFigure(0, 0, ' ');
        board.changeFigure(1, 0, 'r');
        one.incMoveCount();
        ArrayList<Integer> moveAfterPush = moveRules.moveRule(one, 0, 1);
        ArrayList<Integer> expectedPush = new ArrayList<>();
        expectedPush.add(0);
        expectedPush.add(0);
        // Assert that player must go to tile they made push move on
        Assertions.assertEquals(expectedPush, moveAfterPush);
    }

}
