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

            // Read frp, inFromClient and output to outToClient
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
            case "addcars": {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Adding new cars");
				System.out.println("-Car Location: " + arguments.elementAt(1));
				System.out.println("-Number of Cars: " + arguments.elementAt(2));
				System.out.println("-Car Price: " + arguments.elementAt(3));

				String location = arguments.elementAt(1);
				int numCars = toInt(arguments.elementAt(2));
				int price = toInt(arguments.elementAt(3));

				Boolean res = resourceManager.addCars(location, numCars, price);
				System.out.println("Cars added with response: " + res);
				if (res) {
					System.out.println("Cars added");
                    return res.toString();
				} else {
					System.out.println("Cars could not be added");
				}
				break;
			}
			case "addrooms": {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Adding new rooms");
				System.out.println("-Room Location: " + arguments.elementAt(1));
				System.out.println("-Number of Rooms: " + arguments.elementAt(2));
				System.out.println("-Room Price: " + arguments.elementAt(3));

	       		String location = arguments.elementAt(1);
				int numRooms = toInt(arguments.elementAt(2));
				int price = toInt(arguments.elementAt(3));

				Boolean res = resourceManager.addRooms(location, numRooms, price);
				System.out.println("Rooms added with response: " + res);

				if (res) {
					System.out.println("Rooms added");
                    return res.toString();
				} else {
					System.out.println("Rooms could not be added");
				}
				break;
			}
			case "addcustomer": {
				checkArgumentsCount(1, arguments.size());

				System.out.println("Adding a new customer:=");

				int customer = resourceManager.newCustomer();

				System.out.println("Add customer ID: " + customer);
                return Integer.toString(customer);
			}
			case "addcustomerid": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Adding a new customer");
				System.out.println("-Customer ID: " + arguments.elementAt(1));

				int customerID = toInt(arguments.elementAt(1));

				Boolean res = resourceManager.newCustomer(customerID);

				if (res) {
					System.out.println("Add customer ID: " + customerID);
                    return res.toString();
				} else {
					System.out.println("Customer could not be added");
				}
				break;
			}
			case "deleteflight": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Deleting a flight");
				System.out.println("-Flight Number: " + arguments.elementAt(1));

				int flightNum = toInt(arguments.elementAt(1));

				Boolean res = resourceManager.deleteFlight(flightNum);

				if (res) {
					System.out.println("Flight Deleted");
                    return res.toString();
				} else {
					System.out.println("Flight could not be deleted");
				}
				break;
			}
			case "deletecars": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Deleting all cars at a particular location");
				System.out.println("-Car Location: " + arguments.elementAt(1));

				String location = arguments.elementAt(1);

				Boolean res = resourceManager.deleteCars(location);

				if (res) {
					System.out.println("Cars Deleted");
                    return res.toString();
				} else {
					System.out.println("Cars could not be deleted");
				}
				break;
			}
			case "deleterooms": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Deleting all rooms at a particular location");
				System.out.println("-Car Location: " + arguments.elementAt(1));

				String location = arguments.elementAt(1);

				Boolean res = resourceManager.deleteRooms(location);

				if (res) {
					System.out.println("Rooms Deleted");
                    return res.toString();
				} else {
					System.out.println("Rooms could not be deleted");
				}
				break;
			}
			case "deletecustomer": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Deleting a customer from the database");
				System.out.println("-Customer ID: " + arguments.elementAt(1));
				
				int customerID = toInt(arguments.elementAt(1));

				Boolean res = resourceManager.deleteCustomer(customerID);

				if (res) {
					System.out.println("Customer Deleted");
                    return res.toString();
				} else {
					System.out.println("Customer could not be deleted");
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
			case "querycars": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying cars location");
				System.out.println("-Car Location: " + arguments.elementAt(1));
				
				String location = arguments.elementAt(1);

				int numCars = resourceManager.queryCars(location);
				System.out.println("Number of cars at this location: " + numCars);
                return Integer.toString(numCars);
			}
			case "queryrooms": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying rooms location");
				System.out.println("-Room Location: " + arguments.elementAt(1));
				
			
				String location = arguments.elementAt(1);

				int numRoom = resourceManager.queryRooms(location);
				System.out.println("Number of rooms at this location: " + numRoom);
                return Integer.toString(numRoom);
			}
			case "querycustomer": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying customer information");
				System.out.println("-Customer ID: " + arguments.elementAt(1));

				int customerID = toInt(arguments.elementAt(1));

				String bill = resourceManager.queryCustomerInfo(customerID);
				System.out.print(bill);
                return bill;              
			}
			case "queryflightprice": {
				checkArgumentsCount(2, arguments.size());
				
				System.out.println("Querying a flight price");
				System.out.println("-Flight Number: " + arguments.elementAt(1));

				int flightNum = toInt(arguments.elementAt(1));

				int price = resourceManager.queryFlightPrice(flightNum);
				System.out.println("Price of a seat: " + price);
                return Integer.toString(price);
			}
			case "querycarsprice": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying cars price");
				System.out.println("-Car Location: " + arguments.elementAt(1));

				String location = arguments.elementAt(1);

				int price = resourceManager.queryCarsPrice(location);

				System.out.println("Price of cars at this location: " + price);
                return Integer.toString(price);
			}
			case "queryroomprice": {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying rooms price");
				System.out.println("-Room Location: " + arguments.elementAt(1));

				String location = arguments.elementAt(1);

				int price = resourceManager.queryRoomsPrice(location);
				System.out.println("Price of rooms at this location: " + price);
                return Integer.toString(price);
			}
			case "reserveflight": {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Reserving seat in a flight");
				System.out.println("-Customer ID: " + arguments.elementAt(1));
				System.out.println("-Flight Number: " + arguments.elementAt(2));

				int customerID = toInt(arguments.elementAt(1));
				int flightNum = toInt(arguments.elementAt(2));

				Boolean res = resourceManager.reserveFlight(customerID, flightNum);

				if (res) {
					System.out.println("Flight Reserved");
                    return res.toString();
				} else {
					System.out.println("Flight could not be reserved");
				}
				break;
			}
			case "reservecar": {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Reserving a car at a location");
				System.out.println("-Customer ID: " + arguments.elementAt(1));
				System.out.println("-Car Location: " + arguments.elementAt(2));

				int customerID = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);

				Boolean res = resourceManager.reserveCar(customerID, location);

				if (res) {
					System.out.println("Car Reserved");
                    return res.toString();
				} else {
					System.out.println("Car could not be reserved");
				}
				break;
			}
			case "reserveroom": {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Reserving a room at a location");
				System.out.println("-Customer ID: " + arguments.elementAt(1));
				System.out.println("-Room Location: " + arguments.elementAt(2));
				
				int customerID = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);

				Boolean res = resourceManager.reserveRoom(customerID, location);

				if (res) {
					System.out.println("Room Reserved");
                    return res.toString();
				} else {
					System.out.println("Room could not be reserved");
				}
				break;
			}
			case "bundle": {
				if (arguments.size() < 6) {
					System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mBundle command expects at least 6 arguments. Location \"help\" or \"help,<CommandName>\"");
					break;
				}

				System.out.println("Reserving an bundle");
				System.out.println("-Customer ID: " + arguments.elementAt(1));
				for (int i = 0; i < arguments.size() - 5; ++i)
				{
					System.out.println("-Flight Number: " + arguments.elementAt(2+i));
				}
				System.out.println("-Location for Car/Room: " + arguments.elementAt(arguments.size()-3));
				System.out.println("-Book Car: " + arguments.elementAt(arguments.size()-2));
				System.out.println("-Book Room: " + arguments.elementAt(arguments.size()-1));

				int customerID = toInt(arguments.elementAt(1));
				Vector<String> flightNumbers = new Vector<String>();
				for (int i = 0; i < arguments.size() - 5; ++i)
				{
					flightNumbers.addElement(arguments.elementAt(2+i));
				}
				String location = arguments.elementAt(arguments.size()-3);
				Boolean car = toBoolean(arguments.elementAt(arguments.size()-2));
				Boolean room = toBoolean(arguments.elementAt(arguments.size()-1));

				String strArg = "bundle," + customerID;
				for (int i = 0; i < flightNumbers.size(); ++i)
				{
					strArg += "," + flightNumbers.elementAt(i);
				}
				strArg += "," + location + "," + car + "," + room;

				Boolean res = resourceManager.bundle(customerID, flightNumbers, location, car, room);

				if (res) {
					System.out.println("Bundle Reserved");
				} else {
					System.out.println("Bundle could not be reserved");
				}
				break;
			}
            case "updatereservation": {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Updating reservation");
                System.out.println("-Reserved Item Key: " + arguments.elementAt(1));
                System.out.println("-New Item Count: " + arguments.elementAt(2));

                String reservedItemKey = arguments.elementAt(1);
                int newItemCount = toInt(arguments.elementAt(2));

                Boolean res = resourceManager.updateReservation(reservedItemKey, newItemCount);

                if (res) {
                    System.out.println("Reservation Updated");
                    return res.toString();
                } else {
                    System.out.println("Reservation could not be updated");
                }
                break;
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
