package cz.cvut.fel.pjv.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Screen which contains a list with complete game notation
 */
public class NotationScreen extends JPanel implements ActionListener {
    
    private final JFrame notationWindow;
    private static JTextArea notationModel;

    public NotationScreen(JFrame notationWindow, JTextArea notationTextArea) {
        this.notationWindow = notationWindow;
        setLayout(new BorderLayout());

        // Title at the top
        JLabel titleLabel = new JLabel("Notation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 64));
        add(titleLabel, BorderLayout.NORTH);

        // Area to display notation list
        JPanel notationPanel = new JPanel(new BorderLayout());
        add(notationPanel, BorderLayout.CENTER);

        // Text area for whole game notation
        String originalText = notationTextArea.getText();
        notationModel = new JTextArea();
        notationModel.setText(originalText);
        notationModel.setEditable(false);
        notationModel.setLineWrap(false);

        // notation JScrollPane with potential scrollbars
        JScrollPane notationScrollPane = new JScrollPane(notationModel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        notationPanel.add(notationScrollPane, BorderLayout.CENTER);

        // Buttons Panel, add back button
        JPanel buttonsPanel = new JPanel();
        addButton(buttonsPanel);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void addButton(JPanel panel) {
        JButton button = new JButton("Back");
        button.addActionListener(this);
        button.setPreferredSize(new Dimension(160, 40));
        panel.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        notationWindow.dispose();
    }

    public static void updateNotationWindowText(JTextArea notationTextArea) {
        // need to update when MainGame notation is ahead
        String updatedText = notationTextArea.getText();
        notationModel.setText(updatedText);
    }

}
