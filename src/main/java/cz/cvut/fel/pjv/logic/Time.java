package cz.cvut.fel.pjv.logic;

import cz.cvut.fel.pjv.gui.MainGame;
import java.util.logging.Logger;

/**
 * Class to record round time of playing player
 * Uses separate thread
 */
public class Time implements Runnable {

    /**
     * Maximum time limit for a round
     */
    private final int limit;

    /**
     * Seconds of round
     */
    private int seconds;

    /**
     * Minutes of round
     */
    private int minutes;

    /**
     * Determine if the thread is running or needs to end
     */
    private volatile boolean running;

    /**
     * Determine if the thread is paused due to saving the game
     */
    private volatile boolean paused;

    /**
     * Needed to use MainGame class
     */
    private MainGame mainGame;

    /**
     * Used for logging purposes
     */
    private static final Logger logger = Logger.getLogger(Time.class.getName());

    /**
     * Constructor initializes seconds, minutes, running and paused
     */
    public Time() {
        this.limit = 3;
        this.seconds = 0;
        this.minutes = 0;
        this.running = true;
        this.paused = false;
    }

    /**
     * Constructor initializes attributes from loaded values
     * @param seconds seconds of round
     * @param minutes minutes of round
     */
    public Time(int seconds, int minutes) {
        this.limit = 3;
        this.seconds = seconds;
        this.minutes = minutes;
        this.running = true;
        this.paused = false;
    }

    /**
     * Starts the time thread at the start of the game
     * Combined directly with gui
     */
    @Override
    public void run() {
        logger.info("Thread started");
        while (running) {
            if (!paused) {
                seconds++;
                minutes += seconds / 60;
                seconds = seconds % 60;
                String formattedTime = String.format("%02d:%02d", minutes, seconds);
                mainGame.updateTime(formattedTime);
                if (minutes >= limit) {
                    mainGame.exceededTime();
                    paused = true;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.severe("Time thread interrupted. Exception: " + e.getMessage());
            }
        }
        logger.info("Thread ended");
    }

    /**
     * Pause this thread because of saving the game
     * Used after validating save filename
     */
    public synchronized void pause() {
        paused = true;
    }

    /**
     * Resume this thread after saving the game
     * Used after finished logic to save game
     */
    public synchronized void resume() {
        paused = false;
    }

    /**
     * End this thread
     * Used after double-clicking Main Menu button or
     * End Game button in MainGame class or cross close icon
     */
    public void end() {
        running = false;
    }

    /**
     * Resets the stopwatch at the end of each round
     * Combined directly with gui
     */
    public synchronized void reset() {
        this.seconds = 0;
        this.minutes = 0;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        mainGame.updateTime(formattedTime);
    }

    /**
     * Setter for mainGame class to use its methods
     * @param mainGame class to be used
     */
    public void setMainGame(MainGame mainGame) {
        this.mainGame = mainGame;
    }

    /**
     * Getter for seconds
     * @return seconds
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Getter for minutes
     * @return minutes
     */
    public int getMinutes() {
        return minutes;
    }

}
