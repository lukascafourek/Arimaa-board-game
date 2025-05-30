package cz.cvut.fel.pjv.gui;

import cz.cvut.fel.pjv.Launcher;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Screen for user manual and game moveRules
 */
public class HowToPlay extends JPanel implements ActionListener {

    private final JFrame window;
    private final boolean newWindow;
    private final Launcher game;
    private static final Logger logger = Logger.getLogger(HowToPlay.class.getName());

    public HowToPlay(Launcher game, boolean newWindow, JFrame window) {
        this.game = game;
        this.newWindow = newWindow;
        this.window = window;
        setLayout(new BorderLayout());

        // title at the top
        JLabel titleLabel = new JLabel("How To Play", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 64));
        add(titleLabel, BorderLayout.NORTH);

        // Area to display moveRules and links
        JPanel contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS)); // Arrange vertically
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // link panel
        JPanel linkPanel = new JPanel();
        GroupLayout links = new GroupLayout(linkPanel);
        linkPanel.setLayout(links);
        links.setAutoCreateGaps(true);
        links.setAutoCreateContainerGaps(true);

        // text field for moveRules
        JLabel text1 = new JLabel("Links to moveRules of the game can be found here.", SwingConstants.LEFT);
        JLabel text2 = new JLabel("The first link get you to the User manual.", SwingConstants.LEFT);

        // Add links
        JLabel link1 = addLink("https://gitlab.fel.cvut.cz/B232_B0B36PJV/cafoulu1/-/wikis/U%C5%BEivatelsk%C3%BD-manu%C3%A1l-hry",
                "User manual for the game");
        JLabel link2 = addLink("http://arimaa.com/arimaa/", "Official Arimaa Website");
        JLabel link3 = addLink("https://en.wikipedia.org/wiki/Arimaa", "Arimaa MoveRules on Wikipedia");
        JLabel link4 = addLink("https://en.wikibooks.org/wiki/Arimaa", "Arimaa MoveRules on Wikibooks");
        links.setHorizontalGroup(links.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(text1)
                .addComponent(text2)
                .addComponent(link1)
                .addComponent(link2)
                .addComponent(link3)
                .addComponent(link4));
        links.setVerticalGroup(links.createSequentialGroup()
                .addComponent(text1)
                .addComponent(text2)
                .addGap(20)
                .addComponent(link1)
                .addComponent(link2)
                .addComponent(link3)
                .addComponent(link4));

        contentArea.add(linkPanel);
        add(contentArea, BorderLayout.CENTER);

        // add back button
        JPanel backButton = new JPanel();
        addButton(backButton);
        add(backButton, BorderLayout.SOUTH);
    }

    private JLabel addLink(final String url, String text) {
        JLabel link = new JLabel(text, SwingConstants.LEFT);
        link.setForeground(Color.BLUE.darker());
        link.setForeground(Color.BLUE.darker());
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (IOException | URISyntaxException ex) {
                        JOptionPane.showMessageDialog(HowToPlay.this, "Cannot open link in browser.");
                        logger.warning("Cannot open link in browser.");
                    }
                }
            }
        });
        return link;
    }

    private void addButton(JPanel panel) {
        JButton button = new JButton("Back");
        button.addActionListener(this);
        button.setPreferredSize(new Dimension(160, 40));
        panel.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // If HowToPlay is not a new window, transform it to MainMenu, otherwise dispose it
        if (!newWindow) {
            game.showMainMenu();
        } else {
            window.dispose();
        }
    }

}
