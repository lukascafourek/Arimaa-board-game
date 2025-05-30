package cz.cvut.fel.pjv.logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Board saving and loading matrices methods
 */
public class BoardTest {

    @Test
    public void testBoardSaveMatrix() {
        Notation notation = new Notation();
        Board board = new Board(notation);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.initialize(i, j, null);
            }
        }
        board.changeFigure(0, 0, 'R');
        board.saveMatrix();
        char[][] matrixOne = board.loadBoard(0);
        // Assertion for loadBoard method for first board
        Assertions.assertEquals('R', matrixOne[0][0]);
        Assertions.assertEquals(' ', matrixOne[1][1]);

        board.changeFigure(1, 1, 'R');
        char[][] matrixTwo = board.getBoard();
        // Assertions for getBoard method for second board
        Assertions.assertEquals('R', matrixTwo[0][0]);
        Assertions.assertEquals('R', matrixTwo[1][1]);

        board.removeLastBoard();
        char[][] matrix = board.getBoard();
        // Assertions for getBoard method after removing last board
        Assertions.assertEquals('R', matrix[0][0]);
        Assertions.assertEquals(' ', matrix[1][1]);
    }

}
