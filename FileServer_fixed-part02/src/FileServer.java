import java.io.*;
import java.net.*;
import java.util.*;

public class FileServer {
    private static final int PORT = 5000;
    private static final String SERVER_FOLDER = "server_files";

    public static void main(String[] args) {
        File folder = new File(SERVER_FOLDER);
        if (!folder.exists()) folder.mkdir();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("File Server running on port " + PORT + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                while (true) {
                    String command = dis.readUTF();
                    if (command.equalsIgnoreCase("list")) {
                        File folder = new File(SERVER_FOLDER);
                        String[] files = folder.list();
                        dos.writeInt(files.length);
                        for (String f : files) dos.writeUTF(f);
                    } else if (command.equalsIgnoreCase("upload")) {
                        String filename = dis.readUTF();
                        long size = dis.readLong();
                        FileOutputStream fos = new FileOutputStream(SERVER_FOLDER + "/" + filename);
                        byte[] buffer = new byte[4096];
                        int read;
                        long remaining = size;
                        while ((read = dis.read(buffer, 0, (int)Math.min(buffer.length, remaining))) > 0) {
                            fos.write(buffer, 0, read);
                            remaining -= read;
                        }
                        fos.close();
                        dos.writeUTF("Upload complete.");
                    } else if (command.equalsIgnoreCase("download")) {
                        String filename = dis.readUTF();
                        File file = new File(SERVER_FOLDER + "/" + filename);
                        if (!file.exists()) {
                            dos.writeLong(-1);
                        } else {
                            dos.writeLong(file.length());
                            FileInputStream fis = new FileInputStream(file);
                            byte[] buffer = new byte[4096];
                            int read;
                            while ((read = fis.read(buffer)) > 0) {
                                dos.write(buffer, 0, read);
                            }
                            fis.close();
                        }
                    } else if (command.equalsIgnoreCase("delete")) {
                        String filename = dis.readUTF();
                        File file = new File(SERVER_FOLDER + "/" + filename);
                        if (file.exists()) {
                            file.delete();
                            dos.writeUTF("File deleted.");
                        } else {
                            dos.writeUTF("File not found.");
                        }
                    } else if (command.equalsIgnoreCase("exit")) {
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
