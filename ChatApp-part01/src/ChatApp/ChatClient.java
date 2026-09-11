package ChatApp;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
//        final String HOST = "10.200.65.65";

        final String HOST = "10.200.64.7";
        final int PORT = 5000;

        try (Socket socket = new Socket(HOST, PORT)) {
            System.out.println("Connected to server.");

            // Receive thread
            Thread receiveThread = new Thread(() -> {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))) {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println("Shikha: " + msg);
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