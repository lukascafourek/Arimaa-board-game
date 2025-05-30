package cz.cvut.fel.pjv;

import cz.cvut.fel.pjv.gui.*;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

/**
 * Main program to combine logic and gui of the game
 */
public class Launcher {

    private final JFrame mainFrame;

    private MainGame mainGame;

    private static final Logger logger = Logger.getLogger(Launcher.class.getName());

    public Launcher() {
        mainFrame = new JFrame("Arimaa");
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // Added for right thread termination if cross close icon is clicked
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (mainGame != null) {
                    mainGame.getTime().end();
                    try {
                        mainGame.getTimeThread().join();
                    } catch (InterruptedException ex) {
                        logger.warning("Time thread was interrupted with exception " + ex.getMessage());
                    }
                }
                logger.info("Arimaa game ending");
                System.exit(0);
            }
        });
        mainFrame.setSize(1000, 800);
        mainFrame.setLocationRelativeTo(null);
        showMainMenu();
        mainFrame.setVisible(true);
    }

    public void showMainMenu() {
        mainFrame.getContentPane().removeAll();
        mainFrame.getContentPane().add(new MainMenu(this));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void showStart() {
        mainFrame.getContentPane().removeAll();
        mainFrame.getContentPane().add(new Start(this, mainFrame));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void showHowToPlay() {
        mainFrame.getContentPane().removeAll();
        mainFrame.getContentPane().add(new HowToPlay(this, false, mainFrame));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void showLoadGame() {
        mainFrame.getContentPane().removeAll();
        mainFrame.getContentPane().add(new Load(this, mainFrame));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void showMainGame(Controller controller, boolean loaded) {
        mainFrame.getContentPane().removeAll();
        if (loaded) {
            mainGame = new MainGame(this, mainFrame, controller, true);
        } else {
            mainGame = new MainGame(this, mainFrame, controller, false);
        }
        mainFrame.getContentPane().add(mainGame);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logger.info("Arimaa game starting");
                new Launcher();
            }
        });
    }

}
