package cz.cvut.fel.pjv.logic;

import java.util.ArrayList;

/**
 * Class used to record the playing game
 */
public class Notation {

    /**
     * Notation of one round
     */
    private ArrayList<String> notation;

    /**
     * List of all notations
     */
    private final ArrayList<ArrayList<String>> listOfNotations;

    /**
     * Counter of game rounds
     */
    private int round;

    /**
     * Constructor to initialize ArrayLists and round counter
     */
    public Notation() {
        this.notation = new ArrayList<>();
        this.listOfNotations = new ArrayList<>();
        this.round = 1;
    }

    /**
     * Constructor to initialize attributes from loaded values
     * @param listOfNotations arrayList of notations
     * @param round number of current round
     */
    public Notation(ArrayList<ArrayList<String>> listOfNotations, int round) {
        this.notation = new ArrayList<>();
        this.listOfNotations = listOfNotations;
        this.round = round;
    }

    /**
     * Adds figure place or move to notation
     * Used in Controller class
     * @param position position of the figure e.g. Eb1 or rd5e,...
     */
    public void addMove(String position) {
        this.notation.add(position);
    }

    /**
     * Removes last notation from list of notations
     * Used in Controller class
     */
    public void removeLastMove() {
        this.listOfNotations.removeLast();
    }

    /**
     * Saves the notation of one move to the listOfNotations
     * and initializes new notation array for the next round
     * Used after each move
     */
    public void saveNotation() {
        listOfNotations.add(notation);
        this.notation = new ArrayList<>();
    }

    /**
     * Getter for listOfNotations
     * @return listOfNotations as ArrayList of ArrayLists
     */
    public ArrayList<ArrayList<String>> getListOfNotations() {return listOfNotations;}

    /**
     * Getter for the notation array of the current round
     * Used in Controller class
     * @return notation as ArrayList
     */
    public ArrayList<String> getNotation() {
        return notation;
    }

    /**
     * Adds +1 to round counter if Silver player is on the move
     * Used at the end of Silver player round
     */
    public void addRound() {
        round++;
    }

    /**
     * Gets the number of rounds played by player
     * @return number of rounds played
     */
    public int getRound() {
        return round;
    }

}
