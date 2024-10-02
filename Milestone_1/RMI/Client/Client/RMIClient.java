package Client;

import Server.Interface.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

import java.util.*;
import java.io.*;


/**
 * RMIClient.java
 * 
 * This class extends the abstract `Client` class and implements the functionality for connecting to a remote 
 * server using Java RMI (Remote Method Invocation). 
 * 
 * It allows the client to communicate with the resource 
 * manager running on the server to manage resources like flights, cars, and rooms.
 * 
 * The client continuously tries to establish a connection with the RMI server and, once connected, 
 * allows users to issue commands through the command-line interface.
 * 
 * Functions:
 * - main(String[] args): Entry point for the RMI client. Accepts optional arguments for server hostname and server RMI object name, and starts the client.
 * - RMIClient(): Constructor that calls the superclass `Client` constructor.
 * - connectServer(): Initiates the connection to the server using default host, port, and server name.
 * - connectServer(String server, int port, String name): Connects to the specified RMI server and looks up the resource manager object. Continuously retries until a successful connection is established.
 */
public class RMIClient extends Client
{
	private static String s_serverHost = "localhost";
        // recommended to hange port last digits to your group number
	private static int s_serverPort = 3019;
	private static String s_serverName = "Middleware";

	//TODO: ADD YOUR GROUP NUMBER TO COMPILE
	private static String s_rmiPrefix = "group_19_";

	public static void main(String args[])
	{	
		if (args.length > 0)
		{
			s_serverHost = args[0];
		}
		if (args.length > 1)
		{
			s_serverName = args[1];
		}
		if (args.length > 2)
		{
			System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUsage: java client.RMIClient [server_hostname [server_rmiobject]]");
			System.exit(1);
		}

		// Get a reference to the RMIRegister
		try {
			RMIClient client = new RMIClient();
			client.connectServer();
			client.start();
		} 
		catch (Exception e) {    
			System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public RMIClient()
	{
		super();
	}

	public void connectServer()
	{
		connectServer(s_serverHost, s_serverPort, s_serverName);
	}

	public void connectServer(String server, int port, String name)
	{
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					m_resourceManager = (IResourceManager)registry.lookup(s_rmiPrefix + name);
					System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
					break;
				}
				catch (NotBoundException|RemoteException e) {
					if (first) {
						System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
						first = false;
					}
				}
				Thread.sleep(500);
			}
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
	}
}

