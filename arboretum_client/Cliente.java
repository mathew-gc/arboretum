package arboretum_client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private static final int SERVER_PORT = 12345; // Puerto del servidor
    private static final int BOARD_SIZE = 5; // Tamaño del tablero (número de filas y columnas)

    private JFrame frame;
    private JLabel nameLabel; // Etiqueta para el campo de nombre
    private JTextField nameField;
    private JLabel gameLabel; // Etiqueta para el campo de nombre de partida
    private JTextField textField;
    private JPanel boardPanel; // Panel que representa el tablero
    private JButton createButton;
    private JButton joinButton;
    private JButton exitButton; // Botón para salir de la partida

    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private String playerName; // Nombre del jugador
    private String gameName; // Nombre de la partida

    public Cliente() {
        initializeGUI();
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameName = textField.getText().trim();
                if (!gameName.isEmpty()) {
                    sendMessage("CREATE|" + gameName);
                    createButton.setEnabled(false);
                    joinButton.setEnabled(false);
                }
            }
        });

        joinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameName = textField.getText().trim();
                if (!gameName.isEmpty()) {
                    sendMessage("JOIN|" + gameName);
                    createButton.setEnabled(false);
                    joinButton.setEnabled(false);
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage("EXIT");
                createButton.setEnabled(true);
                joinButton.setEnabled(true);
            }
        });
    }

    private void initializeGUI() {
        frame = new JFrame("Cliente");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        nameLabel = new JLabel("Nombre del jugador:");
        nameLabel.setBounds(10, 10, 300, 30);
        frame.getContentPane().add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(10, 40, 300, 30);
        frame.getContentPane().add(nameField);

        gameLabel = new JLabel("Nombre de la partida:");
        gameLabel.setBounds(10, 80, 300, 30);
        frame.getContentPane().add(gameLabel);

        textField = new JTextField();
        textField.setBounds(10, 110, 300, 30);
        frame.getContentPane().add(textField);

        createButton = new JButton("Crear Partida");
        createButton.setBounds(10, 150, 150, 30);
        frame.getContentPane().add(createButton);

        joinButton = new JButton("Unirse a Partida");
        joinButton.setBounds(170, 150, 150, 30);
        frame.getContentPane().add(joinButton);

        exitButton = new JButton("Salir");
        exitButton.setBounds(650, 150, 100, 30);
        frame.getContentPane().add(exitButton);

        // Crear el panel del tablero
        boardPanel = new JPanel();
        boardPanel.setBounds(10, 190, 760, 360);
        boardPanel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        frame.getContentPane().add(boardPanel);

        // Agregar las posiciones al tablero
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JPanel positionPanel = new JPanel();
                positionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                boardPanel.add(positionPanel);
            }
        }

        frame.setVisible(true);
    }

    public void connectToServer() {
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            socket = new Socket(serverAddress, SERVER_PORT);
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            getPlayerName();
            startListening();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void getPlayerName() {
        playerName = nameField.getText().trim();
        if (playerName.isEmpty()) {
            playerName = "Player";
        }
    }

    public void startListening() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (input.hasNextLine()) {
                        String message = input.nextLine();
                        handleGameMessage(message);
                    }
                }
            }
        });
        thread.start();
    }

    private void handleGameMessage(String message) {
        String[] parts = message.split("\\|");
        String command = parts[0];

        if (command.equals("GAME_CREATED")) {
            gameName = parts[1];
            JOptionPane.showMessageDialog(frame, "Se ha creado la partida '" + gameName + "'");
            JOptionPane.showMessageDialog(frame, "Te has unido a la partida '" + gameName + "'");
            createButton.setEnabled(false);
            joinButton.setEnabled(false);
        } else if (command.equals("JOINED_GAME")) {
            gameName = parts[1];
            JOptionPane.showMessageDialog(frame, "Te has unido a la partida '" + gameName + "'");
            createButton.setEnabled(false);
            joinButton.setEnabled(false);
        }

        // Lógica adicional para manejar otros comandos y actualizar la interfaz de usuario según sea necesario
    }

    private void sendMessage(String message) {
        output.println(message);
        textField.setText("");
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.connectToServer();
    }
}
