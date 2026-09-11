import java.io.*;
import java.net.*;
import java.util.*;

public class FileClient {
    private static final String SERVER_IP = "10.200.65.65"; // Change to your Server PC's IP
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Connected to File Server at " + SERVER_IP + ":" + SERVER_PORT);

            while (true) {
                System.out.println("\nCommands: list, upload, download, delete, exit");
                System.out.print("Enter command: ");
                String cmd = sc.nextLine();
                dos.writeUTF(cmd);

                if (cmd.equalsIgnoreCase("list")) {
                    int count = dis.readInt();
                    System.out.println("Files on server:");
                    for (int i = 0; i < count; i++) {
                        System.out.println(dis.readUTF());
                    }
                } else if (cmd.equalsIgnoreCase("upload")) {
                    System.out.print("Enter file path to upload: ");
                    String path = sc.nextLine();
                    File file = new File(path);
                    if (!file.exists()) {
                        System.out.println("File not found.");
                        continue;
                    }
                    dos.writeUTF(file.getName());
                    dos.writeLong(file.length());
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = fis.read(buffer)) > 0) {
                        dos.write(buffer, 0, read);
                    }
                    fis.close();
                    System.out.println(dis.readUTF());
                } else if (cmd.equalsIgnoreCase("download")) {
                    System.out.print("Enter file name to download: ");
                    String filename = sc.nextLine();
                    dos.writeUTF(filename);
                    long size = dis.readLong();
                    if (size == -1) {
                        System.out.println("File not found on server.");
                    } else {
                        FileOutputStream fos = new FileOutputStream("client_files/" + filename);
                        byte[] buffer = new byte[4096];
                        int read;
                        long remaining = size;
                        while ((read = dis.read(buffer, 0, (int)Math.min(buffer.length, remaining))) > 0) {
                            fos.write(buffer, 0, read);
                            remaining -= read;
                        }
                        fos.close();
                        System.out.println("Download complete.");
                    }
                } else if (cmd.equalsIgnoreCase("delete")) {
                    System.out.print("Enter file name to delete: ");
                    String filename = sc.nextLine();
                    dos.writeUTF(filename);
                    System.out.println(dis.readUTF());
                } else if (cmd.equalsIgnoreCase("exit")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
