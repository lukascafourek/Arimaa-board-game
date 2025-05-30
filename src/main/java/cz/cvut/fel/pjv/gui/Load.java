package cz.cvut.fel.pjv.gui;

import cz.cvut.fel.pjv.Controller;
import cz.cvut.fel.pjv.GameData;
import cz.cvut.fel.pjv.Launcher;
import cz.cvut.fel.pjv.logic.SaveAndLoad;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;

/**
 * Screen for a list with saved games
 */
public class Load extends JPanel implements ActionListener {

    private String selectedSaveGame = null;
    private final Launcher game;
    private final JFrame thisFrame;
    private DefaultListModel<String> listModel;
    private static final Logger logger = Logger.getLogger(Load.class.getName());

    public Load(Launcher game, JFrame thisFrame) {
        this.game = game;
        this.thisFrame = thisFrame;
        setLayout(new BorderLayout());

        // Title at the top
        JLabel titleLabel = new JLabel("Load Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 64));
        add(titleLabel, BorderLayout.NORTH);

        // Buttons Panel, add buttons
        JPanel buttonsPanel = new JPanel();
        JButton load = addButton("Load");
        load.setEnabled(false);
        buttonsPanel.add(load);
        JButton delete = addButton("Delete");
        delete.setEnabled(false);
        buttonsPanel.add(delete);
        JButton back = addButton("Back");
        buttonsPanel.add(back);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Area to display saved games
        JPanel savedGamesPanel = new JPanel(new BorderLayout());
        add(savedGamesPanel, BorderLayout.CENTER);

        // savedGamesList to be added to savedGamesPanel
        JList<String> savedGamesList = getStringJList();
        savedGamesPanel.add(new JScrollPane(savedGamesList), BorderLayout.CENTER);
        savedGamesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    selectedSaveGame = savedGamesList.getSelectedValue();
                    buttonsPanel.getComponent(0).setEnabled(selectedSaveGame != null);
                    buttonsPanel.getComponent(1).setEnabled(selectedSaveGame != null);
                }
            }
        });
    }

    private JList<String> getStringJList() {
        // Getting all json files from saved_games directory as JList of strings
        listModel = new DefaultListModel<>();
        String savingDir = System.getProperty("user.dir") + "/saved_games";
        FilenameFilter jsonFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".json");
            }
        };
        File[] savedGameFiles = new File(savingDir).listFiles(jsonFilter);
        if (savedGameFiles != null) {
            for (File file : savedGameFiles) {
                if (file.isFile()) {
                    listModel.addElement(file.getName());
                }
            }
        }
        return new JList<>(listModel);
    }

    private JButton addButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        button.setPreferredSize(new Dimension(160, 40));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        switch (sourceButton.getText()) {
            case "Load":
                GameData gameData = SaveAndLoad.load(selectedSaveGame);
                if (gameData != null) {
                    Controller controller = new Controller(gameData);
                    game.showMainGame(controller, true);
                } else {
                    thisFrame.revalidate();
                    thisFrame.repaint();
                }
                break;
            case "Delete":
                int result = JOptionPane.showConfirmDialog(
                        thisFrame,"Are you sure you want to delete file " + selectedSaveGame + "?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    if (SaveAndLoad.delete(selectedSaveGame)) {
                        listModel.removeElement(selectedSaveGame);
                        selectedSaveGame = null;
                    }
                }
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
