package arboretum_client;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ClientJuego {
    private JPanel panel;
    private JLabel roomLabel;
    private JPanel playerPanel;
    private Map<String, JLabel> scoreLabels;

    public ClientJuego(String playerName, String roomCode) {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        roomLabel = new JLabel("Room: " + roomCode);
        roomLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(roomLabel, BorderLayout.NORTH);

        playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(0, 2)); // 0 filas, 2 columnas

        scoreLabels = new HashMap<>();

        JPanel playerInfoPanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(playerName);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerInfoPanel.add(nameLabel, BorderLayout.NORTH);
        JLabel scoreLabel = new JLabel("Score: 0");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerInfoPanel.add(scoreLabel, BorderLayout.CENTER);
        playerPanel.add(playerInfoPanel);
        scoreLabels.put(playerName, scoreLabel);

        panel.add(playerPanel, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void updatePlayerScore(String playerName, int score) {
        JLabel scoreLabel = scoreLabels.get(playerName);
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }
    }

    public void addPlayer(String playerName) {
        if (!scoreLabels.containsKey(playerName)) {
            JPanel playerInfoPanel = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(playerName);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            playerInfoPanel.add(nameLabel, BorderLayout.NORTH);
            JLabel scoreLabel = new JLabel("Score: 0");
            scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            playerInfoPanel.add(scoreLabel, BorderLayout.CENTER);
            playerPanel.add(playerInfoPanel);
            scoreLabels.put(playerName, scoreLabel);
            panel.revalidate();
            panel.repaint();
        }
    }

    public void removePlayer(String playerName) {
        JLabel scoreLabel = scoreLabels.remove(playerName);
        if (scoreLabel != null) {
            JPanel playerInfoPanel = (JPanel) scoreLabel.getParent().getParent();
            playerPanel.remove(playerInfoPanel);
            panel.revalidate();
            panel.repaint();
        }
    }
}
