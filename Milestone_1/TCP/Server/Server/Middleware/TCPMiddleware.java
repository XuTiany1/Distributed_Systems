package Server.Middleware;

import Server.Common.*;
import Server.Interface.*;
import java.util.Vector;
import java.util.ArrayList;
import TCPHandlers.*;

public class TCPMiddleware extends Middleware {
    private static String flightServerHost = "localhost";
    private static String carServerHost = "localhost";
	private static String roomServerHost = "localhost";
    // recommended to hange port last digits to your group number
	private static int flightServerPort = 4019;
	private static int carServerPort = 4019;
	private static int roomServerPort = 4019;

	public static void main(String args[])
	{
		// 1. expose mideleware as a server to the client
		
		TCPMiddleware middleware = null;
		// Create the RMI middleware entry
		try {
			// Create a new Middleware object
			middleware = new TCPMiddleware();

            // To accept connections from the client, we need to create a server socket
            TCPServerHandler tcpServerHandler = new TCPServerHandler(middleware);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
                        tcpServerHandler.closeConnection();
					}
					catch(Exception e) {
						System.err.println((char)27 + "[31;1mMiddleware exception: " + (char)27 + "[0mUncaught exception");
						e.printStackTrace();
					}
				}
			});                                       
			System.out.println("TCP Middleware connection to client ready");
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mMiddleware exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

		// 2. middleware as a client to the Server ResourceManagers
		if (args.length > 0)
		{
			flightServerHost = args[0];
		}
		if (args.length > 1)
		{
			carServerHost = args[1];
		}
		if (args.length > 2)
		{
			roomServerHost = args[2];
		}

		try {
			middleware.connectServers();
		} 
		catch (Exception e) {    
			System.err.println((char)27 + "[31;1mMiddleware exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public TCPMiddleware()
	{
		super("Middleware");
	}

	public void connectServers()
	{
        // Open connection to servers, by creating a new socket for each.
        // We will create new sockets by creating TCPClientHandler objects
        try {
            this.setFlightTcpClientHandler(new TCPClientHandler(flightServerHost));
            this.setCarTcpClientHandler(new TCPClientHandler(carServerHost));
            this.setRoomTcpClientHandler(new TCPClientHandler(roomServerHost));
        } catch (Exception e) {
            System.err.println("Error from TCPMiddleware.connectServers: could not connect to servers");
            e.printStackTrace();
        }
	}
}