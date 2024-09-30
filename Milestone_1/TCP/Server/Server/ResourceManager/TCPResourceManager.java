// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.ResourceManager;

import Server.Common.*;
import TCPHandlers.*;

public class TCPResourceManager extends ResourceManager 
{
	private static String serverName = "Server";

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
            TCPServerHandler tcpServerHandler = new TCPServerHandler(server);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
                        tcpServerHandler.closeConnection();
						System.out.println("TCP Server connection to client closed");
					}
					catch(Exception e) {
						System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
						e.printStackTrace();
					}
				}
			});                                       
			System.out.println("TCP Server connection to client ready");
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

	}

	public TCPResourceManager(String name)
	{
		super(name);
	}
}
