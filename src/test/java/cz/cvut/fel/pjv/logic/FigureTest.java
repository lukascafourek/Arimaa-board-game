package cz.cvut.fel.pjv.logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Figure strength method
 */
public class FigureTest {

    @Test
    public void testFigureStrength() {
        int figureStrength = Figure.getFigureStrength('E');
        // Assertion for expected Gold figure strength
        Assertions.assertEquals(5, figureStrength);

        figureStrength = Figure.getFigureStrength('e');
        // Assertion for expected Silver figure strength
        Assertions.assertEquals(5, figureStrength);

        figureStrength = Figure.getFigureStrength(' ');
        // Assertion for non-existent figure strength
        Assertions.assertEquals(-1, figureStrength);
    }

}
