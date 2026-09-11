package ChatApp;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatServer {
    public static void main(String[] args) {
        final int PORT = 5000;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT + "...");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected: " + socket.getInetAddress());

            // Receive thread
            Thread receiveThread = new Thread(() -> {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))) {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println("Ritu: " + msg);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });
            receiveThread.start();

            // Send loop
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 Scanner sc = new Scanner(System.in)) {
                while (true) {
                    String msg = sc.nextLine();
                    if (msg.equalsIgnoreCase("/quit")) break;
                    out.println(msg);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}