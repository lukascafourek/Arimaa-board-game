package cz.cvut.fel.pjv.logic;

/**
 * Util class imported to MoveRules class for getting figure strength
 */
public class Figure {

    /**
     * Constructor prohibited
     */
    public Figure() {}

    /**
     * Gets the strength of the given char figure
     * Used in MoveRules class methods
     * @param figureType type of the figure
     * @return strength of the figure as integer
     */
    public static int getFigureStrength(char figureType) {
        return switch (figureType) {
            case 'E', 'e' -> 5;
            case 'M', 'm' -> 4;
            case 'H', 'h' -> 3;
            case 'D', 'd' -> 2;
            case 'C', 'c' -> 1;
            case 'R', 'r' -> 0;
            default -> -1;
        };
    }

}
