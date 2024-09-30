// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.ResourceManager;

import Server.Common.*;
import TCPHandlers.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPResourceManager extends ResourceManager 
{
	private static String serverName = "Server";
	// server socket
	private ServerSocket serverSocket;

	public static void main(String args[])
	{
		if (args.length > 0)
		{
			serverName = args[0];
		}
			
		// Create the RMI server entry
		try {
			// Create a new Server object
			TCPResourceManager server = new TCPResourceManager(serverName);

			// To accept connections from the client, we need to create a server socket
            // TCPServerHandler tcpServerHandler = new TCPServerHandler(server);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
                        server.serverSocket.close();
						System.out.println("TCP Server connection to client closed");
					}
					catch(Exception e) {
						System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
						e.printStackTrace();
					}
				}
			});                                       
			System.out.println("TCP Server connection to client starting...");
			while (true) {
				Socket socket = server.serverSocket.accept();
				new TCPServerHandler(server, socket).start(); // start thread to receive requests from client!
			}
			// tcpServerHandler.start();	
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

	}

	public TCPResourceManager(String name) throws IOException {
		super(name);
		this.serverSocket = new ServerSocket(4019);
	}
}
