package cz.cvut.fel.pjv.logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Notation saving and loading notation lists
 */
public class NotationTest {

    @Test
    public void testNotationSaveAndGet() {
        Notation notation = new Notation();
        notation.addMove("Ra1s");
        // Assert for adding move to notation
        Assertions.assertEquals("Ra1s", notation.getNotation().getLast());

        notation.saveNotation();
        // Assertion for saving notation to notation list and check if new notation is empty
        Assertions.assertTrue(notation.getNotation().isEmpty());
        Assertions.assertEquals("Ra1s", notation.getListOfNotations().getLast().getLast());

        notation.removeLastMove();
        // Assertion if list of notations is empty after removing the previous notation
        Assertions.assertTrue(notation.getListOfNotations().isEmpty());
    }

}
