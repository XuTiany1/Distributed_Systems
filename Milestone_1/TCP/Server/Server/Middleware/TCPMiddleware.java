package Server.Middleware;

import Server.Common.*;
import Server.Interface.*;

import TCPHandlers.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/*
    * TCPMiddleware works as a SERVER to the client (having a ServerSocket to listen to client requests)
    and as a CLIENT to the Server ResourceManagers (having TCPClientHandlers to send requests to the Server ResourceManagers with sockets open)
    * TCPMiddlware keeps on listening for requests from client, and when request is received, it forwards the request to the appropriate Server ResourceManagers.
    
*/
public class TCPMiddleware extends Middleware {
    private static String flightServerHost = "localhost";
    private static String carServerHost = "localhost";
	private static String roomServerHost = "localhost";
    // recommended to hange port last digits to your group number
	private static int flightServerPort = 4019;
	private static int carServerPort = 4019;
	private static int roomServerPort = 4019;

    private ServerSocket serverSocket;

	// middleware port
	final private int port = 4019;

	public static void main(String args[])
	{
		// 1. expose mideleware as a server to the client

		// Create a new Middleware object
		TCPMiddleware middleware;
        try {
            middleware = new TCPMiddleware();
        } catch (IOException e) {
            System.err.println("Error from TCPMiddleware.main: could not create middleware");
            e.printStackTrace();
            return;
        }
		// Create the TCP middleware entry
		try {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
                        middleware.serverSocket.close();
                        System.out.println("TCP Middleware connection to client closed");
					}
					catch(Exception e) {
						System.err.println((char)27 + "[31;1mMiddleware exception: " + (char)27 + "[0mUncaught exception");
						e.printStackTrace();
					}
				}
			});                                       
			System.out.println("TCP Middleware connection to Client starting...");
			while (true) {
				Socket socket = middleware.serverSocket.accept(); // needs to accept connection from client for every new thread
				new TCPServerHandler(middleware, socket).start(); // start thread to receive requests from client!
			}
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

	public TCPMiddleware() throws IOException {
		super("Middleware");
        this.serverSocket = new ServerSocket(port);
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