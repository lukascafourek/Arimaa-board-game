package cz.cvut.fel.pjv.gui;

import cz.cvut.fel.pjv.Controller;
import cz.cvut.fel.pjv.Launcher;
import cz.cvut.fel.pjv.logic.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Main screen where the actual game is on
 */
public class MainGame extends JPanel implements ActionListener {

    private boolean turn = true, firstClicked = false, win = false, processing = false;
    private String winPlayer = null;
    private final Launcher game;
    private final Controller controller;
    private Board board;
    private Notation notation;
    private Time time;
    private Thread timeThread;
    private Player goldPlayer;
    private Player silverPlayer;
    private final JFrame thisFrame;
    private JFrame howToPlayWindow, notationWindow;
    private JButton endGame, ready, takeback, endTurn;
    private JLabel winLabel, roundPlayerOne, roundPlayerTwo, timePlayerOne, timePlayerTwo, movesPlayerOne, movesPlayerTwo;
    private JPanel south, grid;
    private JTextField filenameField;
    private JTextArea notationModel;
    private final Map<JLabel, String> labelImagePaths = new HashMap<>();
    private final int[] firstClickFigureCoords = new int[2];
    private final JLabel[] twoLabelsClicked = new JLabel[2];
    private final String[] twoImagePaths = new String[2];
    private static final Logger logger = Logger.getLogger(MainGame.class.getName());

    public MainGame(Launcher game, JFrame thisFrame, Controller controller, boolean loaded) {
        this.game = game;
        this.controller = controller;
        controller.setMainGame(this);
        this.thisFrame = thisFrame;
        setLayout(new BorderLayout());
        initNorthPanel();
        initSouthPanel();
        initWestPanel();
        initEastPanel();
        setVisibleLabels();
        if (loaded) {
            initGrid(true);
            controller.changeNotation();
            if (notation.getRound() > 1) {
                ready.setEnabled(false);
                takeback.setEnabled(true);
                endTurn.setEnabled(true);
            }
            String formattedTime = String.format("%02d:%02d", time.getMinutes(), time.getSeconds());
            updateTime(formattedTime);
            if (win) {
                updateWinner(winPlayer);
            }
        } else {
            initGrid(false);
        }
        timeThread.start();
        logger.info("MainGame window initialized");
        if (goldPlayer.getType()) {
            controller.aiStartControl(grid, turn);
            readyHelper();
        }
    }

    private void initNorthPanel() {
        // north panel with grouplayout
        JPanel north = new JPanel();
        GroupLayout northGroup = new GroupLayout(north);
        north.setLayout(northGroup);
        northGroup.setAutoCreateGaps(true);
        northGroup.setAutoCreateContainerGaps(true);

        // add player, moves, round and time labels
        JLabel playerOne = new JLabel("Gold Player: " + goldPlayer.getName());
        playerOne.setFont(new Font("Arial", Font.BOLD, 16));
        movesPlayerOne = new JLabel("Move Count: " + goldPlayer.getMoveCount());
        movesPlayerOne.setFont(new Font("Arial", Font.BOLD, 16));
        movesPlayerOne.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        roundPlayerOne = new JLabel("Round: " + notation.getRound());
        roundPlayerOne.setFont(new Font("Arial", Font.BOLD, 16));
        roundPlayerOne.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        timePlayerOne = new JLabel();
        timePlayerOne.setFont(new Font("Arial", Font.BOLD, 16));
        northGroup.setHorizontalGroup(northGroup.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(northGroup.createSequentialGroup()
                        .addComponent(playerOne)
                        .addComponent(movesPlayerOne)
                        .addComponent(roundPlayerOne)
                        .addComponent(timePlayerOne)));
        northGroup.setVerticalGroup(northGroup.createSequentialGroup()
                .addGroup(northGroup.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(playerOne)
                        .addComponent(movesPlayerOne)
                        .addComponent(roundPlayerOne)
                        .addComponent(timePlayerOne)));

        north.setBorder(BorderFactory.createEmptyBorder(0, 120, 0, 0));
        add(north, BorderLayout.NORTH);
    }

    private void initSouthPanel() {
        // south panel with grouplayout
        south = new JPanel();
        GroupLayout southGroup = new GroupLayout(south);
        south.setLayout(southGroup);
        southGroup.setAutoCreateGaps(true);
        southGroup.setAutoCreateContainerGaps(true);

        // add player, moves, round, time, winner labels and endgame button
        JLabel playerTwo = new JLabel("Silver Player: " + silverPlayer.getName());
        playerTwo.setFont(new Font("Arial", Font.BOLD, 16));
        movesPlayerTwo = new JLabel("Move Count: " + silverPlayer.getMoveCount());
        movesPlayerTwo.setFont(new Font("Arial", Font.BOLD, 16));
        movesPlayerTwo.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        roundPlayerTwo = new JLabel("Round: " + notation.getRound());
        roundPlayerTwo.setFont(new Font("Arial", Font.BOLD, 16));
        roundPlayerTwo.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        timePlayerTwo = new JLabel();
        timePlayerTwo.setFont(new Font("Arial", Font.BOLD, 16));
        winLabel = new JLabel();
        winLabel.setFont(new Font("Arial", Font.BOLD, 32));
        winLabel.setVisible(false);
        endGame = addButton("End Game");
        endGame.setVisible(false);
        southGroup.setHorizontalGroup(southGroup.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(southGroup.createSequentialGroup()
                        .addComponent(playerTwo)
                        .addComponent(movesPlayerTwo)
                        .addComponent(roundPlayerTwo)
                        .addComponent(timePlayerTwo))
                .addComponent(winLabel)
                .addComponent(endGame));
        southGroup.setVerticalGroup(southGroup.createSequentialGroup()
                .addGroup(southGroup.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(playerTwo)
                        .addComponent(movesPlayerTwo)
                        .addComponent(roundPlayerTwo)
                        .addComponent(timePlayerTwo))
                .addComponent(winLabel)
                .addComponent(endGame));

        south.setBorder(BorderFactory.createEmptyBorder(0, 120, 89, 0));
        add(south, BorderLayout.SOUTH);
    }

    private void initWestPanel() {
        // west panel with grouplayout
        JPanel west = new JPanel();
        GroupLayout westGroup = new GroupLayout(west);
        west.setLayout(westGroup);
        westGroup.setAutoCreateGaps(true);
        westGroup.setAutoCreateContainerGaps(true);

        // add button column with buttons
        ready = addButton("Ready");
        JButton howToPlay = addButton("How To Play");
        takeback = addButton("Takeback");
        takeback.setEnabled(false);
        endTurn = addButton("End Turn");
        endTurn.setEnabled(false);
        westGroup.setHorizontalGroup(westGroup.createParallelGroup()
                .addComponent(ready)
                .addComponent(howToPlay)
                .addComponent(takeback)
                .addComponent(endTurn));
        westGroup.setVerticalGroup(westGroup.createSequentialGroup()
                .addGap(50)
                .addComponent(ready)
                .addGap(50)
                .addComponent(howToPlay)
                .addGap(50)
                .addComponent(takeback)
                .addGap(50)
                .addComponent(endTurn));
        add(west, BorderLayout.WEST);
    }

    private void initEastPanel() {
        // east panel with grouplayout
        JPanel east = new JPanel();
        GroupLayout eastGroup = new GroupLayout(east);
        east.setLayout(eastGroup);
        eastGroup.setAutoCreateGaps(true);
        eastGroup.setAutoCreateContainerGaps(true);

        // add MainMenu and Save buttons, filenameField and area for notationList
        JButton mainMenu = addButton("Main Menu");
        filenameField = new JTextField();
        filenameField.setMaximumSize(new Dimension(80, 40));
        JButton save = addButton("Save");
        notationModel = new JTextArea(23, 19);
        notationModel.setEditable(false);
        notationModel.setLineWrap(true);
        notationModel.setWrapStyleWord(true);
        JScrollPane notationList = new JScrollPane(this.notationModel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // add -->, <-- and All buttons
        JButton leftArrow = addButton("←");
        JButton rightArrow = addButton("→");
        JButton all = addButton("All");
        eastGroup.setHorizontalGroup(eastGroup.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(mainMenu)
                .addGroup(eastGroup.createSequentialGroup()
                        .addComponent(filenameField)
                        .addComponent(save))
                .addComponent(notationList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGroup(eastGroup.createSequentialGroup()
                        .addComponent(leftArrow)
                        .addComponent(rightArrow))
                .addComponent(all));
        eastGroup.setVerticalGroup(eastGroup.createSequentialGroup()
                .addComponent(mainMenu)
                .addGroup(eastGroup.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(filenameField)
                        .addComponent(save))
                .addComponent(notationList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGroup(eastGroup.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(leftArrow)
                        .addComponent(rightArrow))
                .addComponent(all));

        add(east, BorderLayout.EAST);
    }

    private void setVisibleLabels() {
        if (turn) {
            movesPlayerOne.setVisible(true);
            roundPlayerOne.setVisible(true);
            timePlayerOne.setVisible(true);
            movesPlayerTwo.setVisible(false);
            roundPlayerTwo.setVisible(false);
            timePlayerTwo.setVisible(false);
        } else {
            movesPlayerOne.setVisible(false);
            roundPlayerOne.setVisible(false);
            timePlayerOne.setVisible(false);
            movesPlayerTwo.setVisible(true);
            roundPlayerTwo.setVisible(true);
            timePlayerTwo.setVisible(true);
        }
    }

    private void initGrid(boolean loaded) {
        // add board in the middle of the window
        grid = new JPanel(new GridLayout(8, 8));
        createGridCells(loaded);
        add(grid, BorderLayout.CENTER);

    }

    private void createGridCells(boolean loaded) {
        // create cells and add figure images for first initialization
        String[][] imageMappings = {
                {"/R.png", "/R.png", "/R.png", "/R.png", "/R.png", "/R.png", "/R.png", "/R.png"},
                {"/D.png", "/H.png", "/C.png", "/M.png", "/E.png", "/C.png", "/H.png", "/D.png"},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {"/dd.png", "/hh.png", "/cc.png", "/ee.png", "/mm.png", "/cc.png", "/hh.png", "/dd.png"},
                {"/rr.png", "/rr.png", "/rr.png", "/rr.png", "/rr.png", "/rr.png", "/rr.png", "/rr.png"}
        };
        // Create cells with labels and images in the grid
        grid.setLayout(new GridLayout(8, 8));
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JPanel cell = new JPanel();
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                setCellColor(i, j, cell);
                String imagePath;
                if (loaded) {
                    char figure = board.getFigure(i, j);
                    if (figure == ' ') {
                        imagePath = null;
                    } else if (Character.isUpperCase(figure)) {
                        imagePath = "/" + figure + ".png";
                    } else {
                        imagePath = "/" + figure + figure + ".png";
                    }
                } else {
                    imagePath = imageMappings[i][j];
                }
                imageInit(cell, imagePath);
                board.initialize(i, j, imagePath);
                mouse(cell, i, j, imagePath);
                grid.add(cell);
            }
        }
    }

    private void setCellColor(int i, int j, JPanel cell) {
        if ((i == 2 && j == 2) || (i == 2 && j == 5) || (i == 5 && j == 2) || (i == 5 && j == 5)) {
            cell.setBackground(new Color(250, 100, 100, 128));
        } else if (i == 0) {
            cell.setBackground(new Color(250, 200, 50, 128));
        } else if (i == 7) {
            cell.setBackground(new Color(220, 220, 220, 128));
        } else {
            cell.setBackground(new Color(128, 128, 128, 128));
        }
    }

    public void changeTileBackground(ArrayList<Integer> tiles, boolean typeOfChange) {
        for (int i = 0; i < tiles.size(); i += 2) {
            int x = tiles.get(i);
            int y = tiles.get(i + 1);
            JPanel cell = (JPanel) grid.getComponent(x * 8 + y);
            if (typeOfChange) {
                cell.setBackground(new Color(100, 250, 100, 128));
            } else {
                setCellColor(x, y, cell);
            }
        }
        thisFrame.revalidate();
        thisFrame.repaint();
    }

    private void imageInit(JPanel cell, String name) {
        // load figure images from resources and add them to labels in cells
        try {
            if (name == null) {
                JLabel label = new JLabel();
                cell.add(label);
                labelImagePaths.put(label, null);
            } else {
                BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResource(name)));
                JLabel imageLabel = new JLabel();
                imageLabel.setIcon(new ImageIcon(image));
                cell.add(imageLabel);
                labelImagePaths.put(imageLabel, name);
            }
            thisFrame.revalidate();
            thisFrame.repaint();
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("Cannot load resource: " + e.getMessage());
        }
    }

    public void imageChange(JLabel imageLabel, String name) {
        // load figure images from resources and change them after figure move
        try {
            if (name == null) {
                imageLabel.setIcon(null);
                labelImagePaths.put(imageLabel, null);
            } else {
                BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResource(name)));
                imageLabel.setIcon(new ImageIcon(image));
                labelImagePaths.put(imageLabel, name);
            }
            thisFrame.revalidate();
            thisFrame.repaint();
        } catch (IllegalArgumentException | IOException e) {
            logger.severe("Cannot load resource: " + e.getMessage());
        }
    }

    public void imageRemove(JLabel imageLabel) {
        // remove image icon if figure is removed
        imageLabel.setIcon(null);
        labelImagePaths.put(imageLabel, null);
        thisFrame.revalidate();
        thisFrame.repaint();
    }

    private void mouse(JPanel cell, int i, int j, String imagePath) {
        // add listener for mouse when cell in the grid is clicked
        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((turn && goldPlayer.getType()) || (!turn && silverPlayer.getType()) || win || processing) {
                    return;
                }
                processing = true;
                if (controller.getIndex() != board.getBoardIndex()) {
                    controller.resetBoardAndNotation(grid);
                } else {
                    // Changing figures at the start before both players clicked ready
                    if (ready.isEnabled() && imagePath != null) {
                        logger.info("Clicked position (" + i + ", " + j + ") with figure " + board.getFigure(i, j));
                        // click on the first figure to be changed
                        if (!firstClicked && ((turn && i <= 1) || (!turn && i >= 6))) {
                            firstClicked = true;
                            firstClickFigureCoords[0] = i;
                            firstClickFigureCoords[1] = j;
                            twoLabelsClicked[0] = (JLabel) cell.getComponent(0);
                            twoImagePaths[0] = labelImagePaths.get(twoLabelsClicked[0]);
                        // click on the second figure to be changed
                        } else if (firstClicked && ((turn && i <= 1) || (!turn && i >= 6))) {
                            firstClicked = false;
                            twoLabelsClicked[1] = (JLabel) cell.getComponent(0);
                            twoImagePaths[1] = labelImagePaths.get(twoLabelsClicked[1]);
                            controller.changeFiguresControl(firstClickFigureCoords, i, j, twoLabelsClicked, twoImagePaths);
                        }
                    // Control for the main game after both players clicked Ready button
                    } else if (!ready.isEnabled()) {
                        // Check if player has not done 4 moves already
                        if (goldPlayer.getMoveCount() < 4 && silverPlayer.getMoveCount() < 4) {
                            // Gold player turn first click on figure
                            if (!firstClicked && turn) {
                                logger.info("Clicked position (" + i + ", " + j + ") with figure " + board.getFigure(i, j));
                                boolean move = controller.checkMoveControl(i, j, true);
                                // If move(s) can be done, save clicked figure attributes
                                if (move) {
                                    firstClickFigureCoords[0] = i;
                                    firstClickFigureCoords[1] = j;
                                    twoLabelsClicked[0] = (JLabel) cell.getComponent(0);
                                    twoImagePaths[0] = labelImagePaths.get(twoLabelsClicked[0]);
                                    firstClicked = true;
                                }
                            // Gold player turn second click on empty tile, save its attributes
                            } else if (firstClicked && turn) {
                                twoLabelsClicked[1] = (JLabel) cell.getComponent(0);
                                twoImagePaths[1] = labelImagePaths.get(twoLabelsClicked[1]);
                                controller.figureMoveControl(firstClickFigureCoords, i, j, twoLabelsClicked, twoImagePaths, turn, grid);
                                firstClicked = false;
                            // Silver player turn first click on figure
                            } else if (!firstClicked) {
                                logger.info("Clicked position (" + i + ", " + j + ") with figure " + board.getFigure(i, j));
                                boolean move = controller.checkMoveControl(i, j, false);
                                // If move(s) can be done, save clicked figure attributes
                                if (move) {
                                    firstClickFigureCoords[0] = i;
                                    firstClickFigureCoords[1] = j;
                                    twoLabelsClicked[0] = (JLabel) cell.getComponent(0);
                                    twoImagePaths[0] = labelImagePaths.get(twoLabelsClicked[0]);
                                    firstClicked = true;
                                }
                            // Silver player turn first click on empty tile, save its attributes
                            } else {
                                twoLabelsClicked[1] = (JLabel) cell.getComponent(0);
                                twoImagePaths[1] = labelImagePaths.get(twoLabelsClicked[1]);
                                controller.figureMoveControl(firstClickFigureCoords, i, j, twoLabelsClicked, twoImagePaths, turn, grid);
                                firstClicked = false;
                            }
                        } else {
                            showMaxMovesCountDialog();
                        }
                    }
                }
                processing = false;
            }
        });
    }

    private void showMaxMovesCountDialog() {
        JOptionPane.showMessageDialog(this, "You have reached maximum number of moves.");
    }

    public void showNoMovesWarningDialog() {
        JOptionPane.showMessageDialog(this, "No valid moves for clicked figure.");
    }

    private void validateName() {
        // validation for filename if it contains valid characters and is unique in saved_games directory
        String name = filenameField.getText();
        if (name.matches("^[a-zA-Z0-9]+$")) {
            String savingDir = System.getProperty("user.dir") + "/saved_games";
            File[] jsonFiles = new File(savingDir).listFiles();
            if (jsonFiles != null) {
                for (File jsonFile : jsonFiles) {
                    if (jsonFile.isFile()) {
                        if (jsonFile.getName().equals(name + ".json")) {
                            JOptionPane.showMessageDialog(this,
                                    "File with this name is already present. Please choose a different name.");
                            return;
                        }
                    }
                }
            }
            time.pause();
            controller.saveGame(name);
            time.resume();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid filename. Please use only alphanumeric characters.");
        }
    }

    private JButton addButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        button.setMinimumSize(new Dimension(100, 40));
        return button;
    }

    private void newHowToPlayWindow() {
        if (howToPlayWindow == null) {
            howToPlayWindow = new JFrame("Arimaa - How To Play");
            howToPlayWindow.setSize(800, 600);
            howToPlayWindow.setLocationRelativeTo(null);
            howToPlayWindow.getContentPane().removeAll();
            howToPlayWindow.getContentPane().add(new HowToPlay(game, true, howToPlayWindow));
        }
        howToPlayWindow.revalidate();
        howToPlayWindow.repaint();
        howToPlayWindow.setVisible(true);
    }

    private void newNotationWindow() {
        if (notationWindow == null) {
            notationWindow = new JFrame("Arimaa - Notation");
            notationWindow.setSize(800, 600);
            notationWindow.setLocationRelativeTo(null);
            notationWindow.getContentPane().removeAll();
            notationWindow.getContentPane().add(new NotationScreen(notationWindow, notationModel));
        } else {
            NotationScreen.updateNotationWindowText(notationModel);
        }
        notationWindow.revalidate();
        notationWindow.repaint();
        notationWindow.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (((turn && goldPlayer.getType()) || (!turn && silverPlayer.getType())) && !win || processing) {
            return;
        }
        JButton sourceButton = (JButton) e.getSource();
        switch (sourceButton.getText()) {
            case "End Game":
                time.end();
                try {
                    timeThread.join();
                } catch (InterruptedException ex) {
                    logger.severe("TimeThread interrupted with exception " + ex.getMessage());
                }
                game.showMainMenu();
                break;
            case "Ready":
                readyHelper();
                break;
            case "How To Play":
                if (controller.getIndex() != board.getBoardIndex()) {
                    controller.resetBoardAndNotation(grid);
                } else {
                    // open new HowToPlay window
                    newHowToPlayWindow();
                    thisFrame.revalidate();
                    thisFrame.repaint();
                }
                break;
            case "Takeback":
                if (controller.getIndex() != board.getBoardIndex()) {
                    controller.resetBoardAndNotation(grid);
                } else {
                    if ((turn && goldPlayer.getMoveCount() > 0) ||
                            (!turn && silverPlayer.getMoveCount() > 0)) {
                        controller.takebackControl(grid, turn);
                    }
                    thisFrame.revalidate();
                    thisFrame.repaint();
                }
                break;
            case "End Turn":
                endTurnHelper();
                break;
            case "Main Menu":
                if (controller.getIndex() != board.getBoardIndex()) {
                    controller.resetBoardAndNotation(grid);
                } else {
                    int result = JOptionPane.showConfirmDialog(
                            thisFrame,"Are you sure you want to go to the Main Menu?",
                            "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        time.end();
                        try {
                            timeThread.join();
                        } catch (InterruptedException ex) {
                            logger.severe("TimeThread interrupted with exception " + ex.getMessage());
                        }
                        game.showMainMenu();
                    } else {
                        thisFrame.revalidate();
                        thisFrame.repaint();
                    }
                }
                break;
            case "Save":
                if (controller.getIndex() != board.getBoardIndex()) {
                    controller.resetBoardAndNotation(grid);
                } else {
                    validateName();
                    thisFrame.revalidate();
                    thisFrame.repaint();
                }
                break;
            case "←":
                controller.leftArrowControl(grid);
                thisFrame.revalidate();
                thisFrame.repaint();
                break;
            case "→":
                controller.rightArrowControl(grid);
                thisFrame.revalidate();
                thisFrame.repaint();
                break;
            case "All":
                if (controller.getIndex() != board.getBoardIndex()) {
                    controller.resetBoardAndNotation(grid);
                } else {
                    // open new notation window
                    newNotationWindow();
                    thisFrame.revalidate();
                    thisFrame.repaint();
                }
                break;
            default:
                logger.warning("Unexpected click");
                break;
        }
    }

    private void readyHelper() {
        // if both players click ready, disable the button and show player one info
        // Silver player turn
        if (!turn) {
            movesPlayerOne.setVisible(true);
            roundPlayerOne.setVisible(true);
            timePlayerOne.setVisible(true);
            movesPlayerTwo.setVisible(false);
            roundPlayerTwo.setVisible(false);
            timePlayerTwo.setVisible(false);
            // need to disable after both players are ready
            ready.setEnabled(false);
            controller.startGame(turn);
            takeback.setEnabled(true);
            endTurn.setEnabled(true);
            setRound();
            turn = true;
            logger.info("Both players ready - game started");
            if (goldPlayer.getType()) {
                controller.aiMoveControl(grid, turn);
                endTurnHelper();
            }
        // Gold player turn
        } else {
            movesPlayerTwo.setVisible(true);
            roundPlayerTwo.setVisible(true);
            timePlayerTwo.setVisible(true);
            movesPlayerOne.setVisible(false);
            roundPlayerOne.setVisible(false);
            timePlayerOne.setVisible(false);
            controller.startGame(turn);
            turn = false;
        }
        time.reset();
        thisFrame.revalidate();
        thisFrame.repaint();
        if (!turn && silverPlayer.getType()) {
            controller.aiStartControl(grid, turn);
            readyHelper();
        }
    }

    private void endTurnHelper() {
        if (controller.getIndex() != board.getBoardIndex()) {
            controller.resetBoardAndNotation(grid);
        } else {
            // changes between showing player one or two info
            // Silver player end turn
            if (!turn && !controller.moveAndBack(false)) {
                if (!controller.mustDoPush(false)) {
                    controller.endTurnControl(false);
                    controller.chooseWinnerControl(silverPlayer, goldPlayer, false);
                    if (!win) {
                        movesPlayerOne.setVisible(true);
                        roundPlayerOne.setVisible(true);
                        timePlayerOne.setVisible(true);
                    }
                    movesPlayerTwo.setVisible(false);
                    roundPlayerTwo.setVisible(false);
                    timePlayerTwo.setVisible(false);
                    turn = true;
                    time.reset();
                    thisFrame.revalidate();
                    thisFrame.repaint();
                    logger.info("Player " + silverPlayer.getName() + " ended their Turn");
                    if (goldPlayer.getType() && !win) {
                        controller.aiMoveControl(grid, turn);
                        endTurnHelper();
                    }
                } else {
                    mustDoPushDialog();
                }
                // Gold player end turn
            } else if (turn && !controller.moveAndBack(true)) {
                if (!controller.mustDoPush(true)) {
                    controller.endTurnControl(true);
                    controller.chooseWinnerControl(goldPlayer, silverPlayer, false);
                    movesPlayerTwo.setVisible(true);
                    roundPlayerTwo.setVisible(true);
                    timePlayerTwo.setVisible(true);
                    movesPlayerOne.setVisible(false);
                    roundPlayerOne.setVisible(false);
                    timePlayerOne.setVisible(false);
                    turn = false;
                    time.reset();
                    thisFrame.revalidate();
                    thisFrame.repaint();
                    logger.info("Player " + goldPlayer.getName() + " ended their Turn");
                    if (silverPlayer.getType() && !win) {
                        controller.aiMoveControl(grid, turn);
                        endTurnHelper();
                    }
                } else {
                    mustDoPushDialog();
                }
            } else {
                moveAndBackDialog();
            }
        }
    }

    private void moveAndBackDialog() {
        JOptionPane.showMessageDialog(this, "Cannot end turn, board layout is the same as at the start of your turn.");
    }

    private void mustDoPushDialog() {
        JOptionPane.showMessageDialog(this, "Cannot end turn, player needs to finish push move.");
    }

    public void setLogicClasses(Notation notation, Board board, Time time, Player goldPlayer, Player silverPlayer) {
        this.notation = notation;
        this.board = board;
        this.time = time;
        time.setMainGame(this);
        this.timeThread = new Thread(time);
        this.goldPlayer = goldPlayer;
        this.silverPlayer = silverPlayer;
    }

    public void setLogicClasses(boolean turn, boolean win, String winPlayer, Notation notation, Board board,
                                Time time, Player goldPlayer, Player silverPlayer) {
        this.turn = turn;
        this.win = win;
        this.winPlayer = winPlayer;
        this.notation = notation;
        this.board = board;
        this.time = time;
        time.setMainGame(this);
        this.timeThread = new Thread(time);
        this.goldPlayer = goldPlayer;
        this.silverPlayer = silverPlayer;
    }

    public void setRound() {
        if (!turn) {
            roundPlayerOne.setText("Round: " + notation.getRound());
        } else {
            roundPlayerTwo.setText("Round: " + notation.getRound());
        }
        thisFrame.revalidate();
        thisFrame.repaint();
    }

    public void setMoveCount() {
        if (turn) {
            movesPlayerOne.setText("Move Count: " + goldPlayer.getMoveCount());
        } else {
            movesPlayerTwo.setText("Move Count: " + silverPlayer.getMoveCount());
        }
        thisFrame.revalidate();
        thisFrame.repaint();
    }

    public void startNotation() {
        if (turn) {
            notationModel.append("\n" + notation.getRound() + "g");
        } else {
            notationModel.append("\n" + notation.getRound() + "s");
        }
        thisFrame.revalidate();
        thisFrame.repaint();
    }

    public void updateNotation(String joinedNotation) {
        notationModel.append(joinedNotation);
        thisFrame.revalidate();
        thisFrame.repaint();
    }

    public void setNotationText(String text) {
        notationModel.setText(text);
        thisFrame.revalidate();
        thisFrame.repaint();
    }

    public void endGameNotation(String condition) {
        notationModel.append(condition);
        win = true;
        thisFrame.revalidate();
        thisFrame.repaint();
    }

    public void updateWinner(String whoWins) {
        time.pause();
        winPlayer = whoWins;
        // Winner label and EndGame button appear
        south.setBorder(BorderFactory.createEmptyBorder(0, 120, 0, 0));
        winLabel.setText("Winner: " + winPlayer);
        winLabel.setVisible(true);
        endGame.setVisible(true);
        takeback.setEnabled(false);
        endTurn.setEnabled(false);
        thisFrame.revalidate();
        thisFrame.repaint();
        logger.info("Game ended");
    }

    public void updateTime(String roundTime) {
        if (turn) {
            timePlayerOne.setText("Time: " + roundTime);
        } else {
            timePlayerTwo.setText("Time: " + roundTime);
        }
        thisFrame.revalidate();
        thisFrame.repaint();
    }

    public void exceededTime() {
        if (turn) {
            controller.chooseWinnerControl(goldPlayer, silverPlayer, true);
        } else {
            controller.chooseWinnerControl(silverPlayer, goldPlayer, true);
        }
    }

    public Time getTime() {
        return time;
    }

    public Thread getTimeThread() {
        return timeThread;
    }

    public boolean isTurn() {
        return turn;
    }

    public boolean isWin() {
        return win;
    }

    public String getWinPlayer() {
        return winPlayer;
    }

}
