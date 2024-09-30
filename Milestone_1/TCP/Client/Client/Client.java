package Client;

import Server.Interface.*;

import java.util.*;
import java.io.*;

import TCPHandlers.TCPClientHandler;

public abstract class Client {

    // Instance of 
    TCPClientHandler tcpClientHandler = null;

    public Client() {
    }

    // setter for TCPClientHandler
    public void setTcpClientHandler(TCPClientHandler tcpClientHandler) {
        this.tcpClientHandler = tcpClientHandler;
    }

    public abstract void connectServer();

    public void start() {
        // Prepare for reading commands
        System.out.println();
        System.out.println("Location \"help\" for list of supported commands");

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            // Read the next command
            String command = "";
            Vector<String> arguments = new Vector<String>();
            try {
                System.out.print((char) 27 + "[32;1m\n>] " + (char) 27 + "[0m");
                command = stdin.readLine().trim();
            } catch (IOException io) {
                System.err.println((char) 27 + "[31;1mClient exception: " + (char) 27 + "[0m" + io.getLocalizedMessage());
                io.printStackTrace();
                System.exit(1);
            }

            try {
                arguments = parse(command);
                Command cmd = Command.fromString((String) arguments.elementAt(0));
                try {
                    execute(cmd, arguments);
                } catch (Exception e) {
                    System.out.println("Command exception: " + e.getLocalizedMessage());
                    connectServer();
                    execute(cmd, arguments);
                }
            } catch (Exception e) {
                System.err.println((char) 27 + "[31;1mCommand exception: " + (char) 27 + "[0mClient to Server connection and command execution exception");
                e.printStackTrace();
            }
        }
    }

    public void execute(Command cmd, Vector<String> arguments) throws NumberFormatException {
        switch (cmd) {
            case Help: {
                if (arguments.size() == 1) {
                    System.out.println(Command.description());
                } else if (arguments.size() == 2) {
                    Command l_cmd = Command.fromString((String) arguments.elementAt(1));
                    System.out.println(l_cmd.toString());
                } else {
                    System.err.println((char) 27 + "[31;1mCommand exception: " + (char) 27 + "[0mImproper use of help command. Location \"help\" or \"help,<CommandName>\"");
                }
                break;
            }
            case AddFlight: {
                checkArgumentsCount(4, arguments.size());

                System.out.println("Adding a new flight ");
                System.out.println("-Flight Number: " + arguments.elementAt(1));
                System.out.println("-Flight Seats: " + arguments.elementAt(2));
                System.out.println("-Flight Price: " + arguments.elementAt(3));

                int flightNum = toInt(arguments.elementAt(1));
                int flightSeats = toInt(arguments.elementAt(2));
                int flightPrice = toInt(arguments.elementAt(3));

                // add flight by sending tcp request to server
                String res = tcpClientHandler.send("addflight," + flightNum + "," + flightSeats + "," + flightPrice);
                System.out.println("Flight added with response: " + res);
                if (toBoolean(res)) {
                    System.out.println("Flight added");
                } else {
                    System.out.println("Flight could not be added");
                }
                break;
            }
            case QueryFlight: {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Querying a flight");
                System.out.println("-Flight Number: " + arguments.elementAt(1));

				System.out.println("");
                int flightNum = toInt(arguments.elementAt(1));

                // query flight by sending tcp request to server
                String res = tcpClientHandler.send("queryflight," + flightNum);

                int seats = toInt(res);
                System.out.println("Number of seats available: " + seats);
                break;
            }
            case Quit:
                checkArgumentsCount(1, arguments.size());

                System.out.println("Quitting client");
                System.exit(0);
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
