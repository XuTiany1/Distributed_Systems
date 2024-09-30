package TCPHandlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;
import Server.Interface.IResourceManager;

public class TCPServerHandler extends Thread {

    private Socket socket;
    PrintWriter outToClient;
    BufferedReader inFromClient;

    IResourceManager resourceManager; // Either Middleware or Server could be passed here

    private int port = 4019;

    TCPServerHandler(IResourceManager resourceManager) throws IOException {
        this.socket = new ServerSocket(port).accept();
        this.resourceManager = resourceManager;
    }

    public void run() {
        try {
            // create new buffers for input and output for every run
            this.inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.outToClient = new PrintWriter(socket.getOutputStream(), true);

            // read message from client
            String message = null;
            while ((message = inFromClient.readLine()) != null) {
                System.out.println("message received: " + message);
                String result = "Working!";

                // parse message
                Vector<String> arguments = parse(message);
                String command = arguments.elementAt(0);

                String res = execute(command, arguments, resourceManager);

                outToClient.println(res); // send execution result to client
            }
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

				if (resourceManager.addFlight(flightNum, flightSeats, flightPrice)) {
					System.out.println("Flight added");
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
				System.out.println("Number of seats available: " + seats);
				break;
			}
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

    public static boolean toBoolean(String string)// throws Exception
    {
        return (Boolean.valueOf(string)).booleanValue();
    }

}
