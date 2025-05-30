package cz.cvut.fel.pjv.gui;

import cz.cvut.fel.pjv.Launcher;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * Screen which opens after launching the game
 */
public class MainMenu extends JPanel implements ActionListener {

    private final Launcher game;
    private static final Logger logger = Logger.getLogger(MainMenu.class.getName());

    public MainMenu(Launcher game) {
        this.game = game;
        setLayout(new BorderLayout());

        // title at the top
        JLabel titleLabel = new JLabel("ARIMAA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 64));
        add(titleLabel, BorderLayout.NORTH);

        // area to show button column
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));

        // button panel
        JPanel buttonPanel = new JPanel();
        GroupLayout buttons = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttons);
        buttons.setAutoCreateGaps(true);
        buttons.setAutoCreateContainerGaps(true);

        // add buttons
        JButton start = addButton("Start");
        JButton load = addButton("Load");
        JButton howToPlay = addButton("How To Play");
        JButton exit = addButton("Exit");
        buttons.setHorizontalGroup(buttons.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(start)
                .addComponent(load)
                .addComponent(howToPlay)
                .addComponent(exit));
        buttons.setVerticalGroup(buttons.createSequentialGroup()
                .addGap(50)
                .addComponent(start)
                .addGap(50)
                .addComponent(load)
                .addGap(50)
                .addComponent(howToPlay)
                .addGap(50)
                .addComponent(exit));

        columnPanel.add(buttonPanel);
        add(columnPanel, BorderLayout.CENTER);
    }

    private JButton addButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        button.setMinimumSize(new Dimension(160, 40));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        switch (sourceButton.getText()) {
            case "Start":
                game.showStart();
                break;
            case "Load":
                game.showLoadGame();
                break;
            case "How To Play":
                game.showHowToPlay();
                break;
            case "Exit":
                System.exit(0);
                break;
            default:
                logger.warning("Unexpected click");
                break;
        }
    }

}
