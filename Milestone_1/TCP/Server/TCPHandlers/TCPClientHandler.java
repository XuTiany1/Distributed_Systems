
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClientHandler {
    private Socket socket;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;

    private String serverHost;
    private int serverPort;


    // initializes TCP Connection with server, opening socket.
    public TCPClientHandler(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;

        this.socket = new Socket(serverHost, serverPort); // establish a socket with a server using the given port#

        this.outToServer = new PrintWriter(socket.getOutputStream(), true); // open an output stream to the server
        this.inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); // open an input stream from the server
    }

    // closes connection to server by closing socket
    public void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // sends message to server socket
    public String send(String message) {
        outToServer.println(message); // send the user's input via the output stream to the server

        String res = null;
        try {
            res = inFromServer.readLine(); // receive the server's result via the input stream from the server
        } catch (Exception e) {
            System.err.println("Error from TCPClientHandler.send: could not read from server");
            e.printStackTrace();
        }

        return res;
    }

}
