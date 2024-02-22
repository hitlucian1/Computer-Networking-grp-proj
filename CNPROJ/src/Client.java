import java.io.*;
import java.net.Socket;
import java.util.List;

public class Client {

    public void listFiles() {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("LIST_FILES");
            List<String> fileList = (List<String>) in.readObject();

            System.out.println("Available files on the server: " + fileList);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(String fileName) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("DOWNLOAD_FILE");
            out.writeObject(fileName);

            try (BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream("downloaded_" + fileName))) {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                System.out.println("File downloaded: " + fileName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.listFiles();
        client.downloadFile("sample.txt"); // Download a sample file
    }
}