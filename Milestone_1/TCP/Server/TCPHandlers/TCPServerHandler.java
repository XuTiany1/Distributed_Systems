package TCPHandlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;
import Server.Interface.IResourceManager;

/*
    * TCPServerHandler handles TCP connection as a SERVER to the client it wants to listen from.
    * It opens a socket to communicate with a client.
    * Note that this class is a thread, so multiple requests from client can be handled at the same time by running multiple TCPServerHandlers.
    * ALSO note that a ResourceManager to the constructor.
        * If the passed ResourceManager is a Middleware, the TCPServerHandler will forward the requests to the ResourceManager by calling the execute function.
        * If the passed ResourceManager is a Server, the TCPServerHandler will execute the function directly, also by calling the execute function.
*/
public class TCPServerHandler extends Thread {

    private int port = 4019;
    // private ServerSocket serverSocket = new ServerSocket(port);
    private Socket socket;
    PrintWriter outToClient;
    BufferedReader inFromClient;

    IResourceManager resourceManager; // Either Middleware or Server could be passed here


    public TCPServerHandler(IResourceManager resourceManager, Socket socket) throws IOException {
        this.socket = socket;
        this.resourceManager = resourceManager;
    }

    public void run() {
        try {
            // create new buffers for input and output for every run
            this.inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.outToClient = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Running TCPServerHandler thread!");

            // read message from client
            String message = null;
            // message = inFromClient.readLine();
            while ((message = inFromClient.readLine()) != null) {
                System.out.println("TCPServerHandler - client message received: " + message);

                // parse message
                Vector<String> arguments = parse(message);
                String command = arguments.elementAt(0);

                String res = execute(command, arguments, resourceManager);

                outToClient.println(res); // send execution result to client
            }

            System.out.println("TCPServerHandler - closing socket...");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Vector<String> parse(String command) {
        Vector<String> arguments = new Vector<String>();
        StringTokenizer tokenizer = new StringTokenizer(command, ",");
        String argument = "";
        while (tokenizer.hasMoreTokens()) {
            argument = tokenizer.nextToken();
            argument = argument.trim();
            arguments.add(argument);
        }
        return arguments;
    }

    public String execute(String command, Vector<String> arguments, IResourceManager resourceManager) {
        switch (command) {
            case "addflight": {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Adding a new flight ");
				System.out.println("-Flight Number: " + arguments.elementAt(1));
				System.out.println("-Flight Seats: " + arguments.elementAt(2));
				System.out.println("-Flight Price: " + arguments.elementAt(3));

				int flightNum = toInt(arguments.elementAt(1));
				int flightSeats = toInt(arguments.elementAt(2));
				int flightPrice = toInt(arguments.elementAt(3));
                
                Boolean res = resourceManager.addFlight(flightNum, flightSeats, flightPrice);
				if (res) {
					System.out.println("Flight added");
                    return res.toString();
				} else {
					System.out.println("Flight could not be added");
				}
				break;
            }
			case "queryflight": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying a flight");
				System.out.println("-Flight Number: " + arguments.elementAt(1));

				int flightNum = toInt(arguments.elementAt(1));

				int seats = resourceManager.queryFlight(flightNum);
				return Integer.toString(seats);
			}
        }
        System.out.println("Invalid command, no matching case found");
        return "Invalid command, no matching case found";
    }

    // closes connection to NO LONGER accept any connections from client
    public void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkArgumentsCount(Integer expected, Integer actual) throws IllegalArgumentException {
        if (expected != actual) {
            throw new IllegalArgumentException("Invalid number of arguments. Expected " + (expected - 1) + ", received " + (actual - 1) + ". Location \"help,<CommandName>\" to check usage of this command");
        }
    }

    public static int toInt(String string) throws NumberFormatException {
        return (Integer.valueOf(string)).intValue();
    }

    public static boolean toBoolean(String string)
    {
        return (Boolean.valueOf(string)).booleanValue();
    }

}
