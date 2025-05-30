package cz.cvut.fel.pjv.gui;

import cz.cvut.fel.pjv.Controller;
import cz.cvut.fel.pjv.Launcher;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * Screen to configure players (names, human/AI)
 */
public class Start extends JPanel implements ActionListener {

    private boolean aiOne = false, aiTwo = false;
    private String playerOne = null, playerTwo = null;
    private final Launcher game;
    private JCheckBox playerOneAI, playerTwoAI;
    private final JFrame thisFrame;
    private static final Logger logger = Logger.getLogger(Start.class.getName());

    public Start(Launcher game, JFrame thisFrame) {
        this.game = game;
        this.thisFrame = thisFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // add title at the top
        JLabel titleLabel = new JLabel("Start Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 64));
        add(titleLabel, BorderLayout.NORTH);

        // Bottom Buttons panel, add buttons
        JPanel buttonPanel = new JPanel();
        addButton(buttonPanel, "Game");
        addButton(buttonPanel, "Back");
        add(buttonPanel, BorderLayout.SOUTH);

        // Center Panel with GroupLayout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Player panels
        JPanel playerOnePanel = createPlayerInputSection("Player One: Gold");
        JPanel playerTwoPanel = createPlayerInputSection("Player Two: Silver");

        centerPanel.add(playerOnePanel);
        centerPanel.add(playerTwoPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createPlayerInputSection(String title) {
        // create GroupLayout
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // add nameLabel, nameField, aiCheckbox and okButton
        JLabel nameLabel = new JLabel(title);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(200, 40));
        JButton okButton = new JButton("OK");
        JCheckBox aiCheckbox = new JCheckBox("AI");

        // Store references for later access in handleGameStart
        if (title.contains("Player One")) {
            playerOneAI = aiCheckbox;
        } else {
            playerTwoAI = aiCheckbox;
        }
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check if entered name si valid
                validateName(nameField, title);
                thisFrame.revalidate();
                thisFrame.repaint();
            }
        });
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(nameLabel)
                .addComponent(nameField)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addComponent(aiCheckbox)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(50)
                .addComponent(nameLabel)
                .addGap(20)
                .addComponent(nameField)
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(okButton)
                        .addComponent(aiCheckbox)));

        return panel;
    }

    private void validateName(JTextField nameField, String title) {
        // validation for player names after clicking OK button
        String name = nameField.getText();
        if (name.matches("^[a-zA-Z0-9]+$")) {
            if (title.contains("Player One")) {
                playerOne = name;
            } else {
                playerTwo = name;
            }
        } else {
            if (title.contains("Player One")) {
                playerOne = null;
                JOptionPane.showMessageDialog(this, "Invalid name for Player One. " +
                        "Please use only alphanumeric characters.");
            } else {
                playerTwo = null;
                JOptionPane.showMessageDialog(this, "Invalid name for Player Two. " +
                        "Please use only alphanumeric characters.");
            }
        }
    }

    private void handleGameStart() {
        // Checking if everything is alright to start the game
        if (playerOneAI.isSelected() && playerTwoAI.isSelected()) {
            JOptionPane.showMessageDialog(this, "Cannot start game with both players as AI.");
        } else if (playerOne == null || playerTwo == null) {
            JOptionPane.showMessageDialog(this, """
                    Cannot start game without naming both players.
                    Please enter valid name for both players.
                    Name is set after clicking proper OK button.""");
        // start the game
        } else {
            if (playerOneAI.isSelected()) {
                aiOne = true;
            }
            if (playerTwoAI.isSelected()) {
                aiTwo = true;
            }
            Controller controller = new Controller(playerOne, playerTwo, aiOne, aiTwo);
            game.showMainGame(controller, false);
        }
    }

    private void addButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        button.setPreferredSize(new Dimension(160, 40));
        panel.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        switch (sourceButton.getText()) {
            case "Game":
                // check if everything fine to start MainGame screen
                handleGameStart();
                thisFrame.revalidate();
                thisFrame.repaint();
                break;
            case "Back":
                game.showMainMenu();
                break;
            default:
                logger.warning("Unexpected click");
                break;
        }
    }

}
