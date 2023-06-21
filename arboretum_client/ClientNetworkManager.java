package arboretum_client;

import java.net.Socket;

import java.io.*;

public class ClientNetworkManager {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5000;

    public String connectToServer(String playerName) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            out.println(playerName);

            String receivedCode = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();

            socket.close();
            return receivedCode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

