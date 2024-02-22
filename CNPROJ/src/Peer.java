import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Peer {
    private static final int PORT = 12345;
    private List<String> sharedFiles;

    public Peer() {
        this.sharedFiles = new ArrayList<>();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shareFile(String fileName) {
        sharedFiles.add(fileName);
    }

    public List<String> getSharedFiles() {
        return sharedFiles;
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            String request = (String) in.readObject();
            System.out.println("Received request: " + request);

            switch (request) {
                case "LIST_FILES":
                    sendFileList(out);
                    break;
                case "DOWNLOAD_FILE":
                    String fileName = (String) in.readObject();
                    sendFile(fileName, out);
                    break;
                default:
                    System.out.println("Invalid request from client.");
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendFileList(ObjectOutputStream out) throws IOException {
        out.writeObject(sharedFiles);
    }

    private void sendFile(String fileName, ObjectOutputStream out) throws IOException {
        try (BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            System.out.println("File sent: " + fileName);
        }
    }

    public static void main(String[] args) {
        Peer peer = new Peer();
        peer.shareFile("sample.txt"); // Share a sample file
        peer.startServer();
    }
}