package Client;

import Server.Common.*;
import Server.Interface.*;

import TCPHandlers.*;

// TCPClient initializes the TCPClientHandler to enable connection with server (middleware) and starts the client.
public class TCPClient extends Client {
    private static String serverHost = "localhost";

	private static int serverPort = 4019;

	public static void main(String args[])
	{
		if (args.length > 0)
		{
			serverHost = args[0];
		}

		try {
            TCPClient client = new TCPClient();
			client.connectServer();
            client.start(); // start getting input from command line, and send to server
		} 
		catch (Exception e) {    
			System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public TCPClient()
	{
		super();
	}

	public void connectServer()
	{
        // Open connection to server, by creating a new socket.
        // We will create new socket by creating TCPClientHandler object.
        try {
            this.setTcpClientHandler(new TCPClientHandler(serverHost));
        } catch (Exception e) {
            System.err.println("Error from TCPClient.connectServer: could not connect to server");
            e.printStackTrace();
        }
	}
}