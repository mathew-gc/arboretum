package arboretum_client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientMenu {
    private JFrame frame;
    private JPanel mainPanel;
    private JTextField nameTextField;
    private JButton createButton;
    private JLabel statusLabel;
    private CardLayout cardLayout;
    private ClientNetworkManager networkManager;
    private ClientJuego clientJuego;

    public ClientMenu() {
        frame = new JFrame("Game Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, "Menu");

        frame.getContentPane().add(mainPanel);

        networkManager = new ClientNetworkManager();
    }

    public void show() {
        frame.setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        nameTextField = new JTextField();
        panel.add(new JLabel("Enter your name:"));
        panel.add(nameTextField);

        createButton = new JButton("Create Game");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createGame();
            }
        });
        panel.add(createButton);

        statusLabel = new JLabel("Status: Disconnected");
        panel.add(statusLabel);

        return panel;
    }

    private void createGame() {
        String name = nameTextField.getText();
        showGameScreen(name, "Loading...");

        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String roomCode = networkManager.connectToServer(name);
                if (roomCode != null) {
                    showGameScreen(name, roomCode);
                } else {
                    showErrorMessage("Failed to connect to the server.");
                }
            }
        });
        connectionThread.start();
    }

    private void showGameScreen(String name, String roomCode) {
        clientJuego = new ClientJuego(name, roomCode);
        mainPanel.add(clientJuego.getPanel(), "Game");
        cardLayout.show(mainPanel, "Game");
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        ClientMenu clientMenu = new ClientMenu();
        clientMenu.show();
    }
}


