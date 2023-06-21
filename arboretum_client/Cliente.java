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

    private JFrame frame;
    private JLabel nameLabel; // Etiqueta para el campo de nombre
    private JTextField nameField;
    private JLabel gameLabel; // Etiqueta para el campo de nombre de partida
    private JTextField textField;
    private JTextArea textArea;
    private JButton createButton;
    private JButton joinButton;

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
                }
            }
        });

        joinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameName = textField.getText().trim();
                if (!gameName.isEmpty()) {
                    sendMessage("JOIN|" + gameName);
                }
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

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 190, 760, 360);
        frame.getContentPane().add(scrollPane);

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
            textArea.append("Se ha creado la partida '" + gameName + "'\n");
            textArea.append("Te has unido a la partida '" + gameName + "'\n");
        } else if (command.equals("JOINED_GAME")) {
            gameName = parts[1];
            textArea.append("Te has unido a la partida '" + gameName + "'\n");
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



