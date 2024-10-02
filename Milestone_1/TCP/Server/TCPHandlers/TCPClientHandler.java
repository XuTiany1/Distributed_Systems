package TCPHandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
    * TCPClientHandler handles TCP connection as a CLIENT to the server it wants to connect to.
    * It opens a socket to communicate with a server.
*/

public class TCPClientHandler {
    private Socket socket;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;

    private String serverHost;
    private int port = 4019;


    // initializes TCP Connection with server, opening socket.
    public TCPClientHandler(String serverHost) throws IOException {
        this.serverHost = serverHost;

        boolean first = true;
        while (true) {
            try {
                this.socket = new Socket(serverHost, port); // establish a socket with a server using the given port#

                this.outToServer = new PrintWriter(socket.getOutputStream(), true); // open an output stream to the server
                this.inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); // open an input stream from the server
                System.out.println("TCPClientHandler - connected to server host " + serverHost + "!");
                break;
            } catch (Exception e) {
                if (first) {
                    System.out.println("Waiting for server host " + serverHost + " connection");
                    first = false;
                }
            }
        }

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

        String res = "";
        try {
            // continue reading until there is no more data
            String dataRead;
            dataRead = inFromServer.readLine();
            System.err.println("inital dataRead: " + dataRead);
            while ((dataRead = inFromServer.readLine()) != null) {
                System.err.println("TCPClientHandler - dataRead: " + dataRead);
                res += dataRead;
                res += "\n";
            }
            System.out.println("TCPClientHandler - Server response: " + res);
        } catch (Exception e) {
            System.err.println("Error from TCPClientHandler.send: could not read from server");
            e.printStackTrace();
        }

        return res;
    }

}
