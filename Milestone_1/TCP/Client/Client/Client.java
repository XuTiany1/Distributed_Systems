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
			case AddCars: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Adding new cars");
				System.out.println("-Car Location: " + arguments.elementAt(1));
				System.out.println("-Number of Cars: " + arguments.elementAt(2));
				System.out.println("-Car Price: " + arguments.elementAt(3));

				String location = arguments.elementAt(1);
				int numCars = toInt(arguments.elementAt(2));
				int price = toInt(arguments.elementAt(3));

				// add cars by sending tcp request to server
				String res = tcpClientHandler.send("addcars," + location + "," + numCars + "," + price);
				System.out.println("Cars added with response: " + res);
				if (toBoolean(res)) {
					System.out.println("Cars added");
				} else {
					System.out.println("Cars could not be added");
				}
				break;
			}
			case AddRooms: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Adding new rooms");
				System.out.println("-Room Location: " + arguments.elementAt(1));
				System.out.println("-Number of Rooms: " + arguments.elementAt(2));
				System.out.println("-Room Price: " + arguments.elementAt(3));

	       		String location = arguments.elementAt(1);
				int numRooms = toInt(arguments.elementAt(2));
				int price = toInt(arguments.elementAt(3));

				// add rooms by sending tcp request to server
				String res = tcpClientHandler.send("addrooms," + location + "," + numRooms + "," + price);
				System.out.println("Rooms added with response: " + res);

				if (toBoolean(res)) {
					System.out.println("Rooms added");
				} else {
					System.out.println("Rooms could not be added");
				}
				break;
			}
			case AddCustomer: {
				checkArgumentsCount(1, arguments.size());

				System.out.println("Adding a new customer:=");

				int customer = toInt(tcpClientHandler.send("addcustomer"));

				System.out.println("Add customer ID: " + customer);
				break;
			}
			case AddCustomerID: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Adding a new customer");
				System.out.println("-Customer ID: " + arguments.elementAt(1));

				int customerID = toInt(arguments.elementAt(1));

				// add customer by sending tcp request to server
				String res = tcpClientHandler.send("addcustomerid," + customerID);

				if (toBoolean(res)) {
					System.out.println("Add customer ID: " + customerID);
				} else {
					System.out.println("Customer could not be added");
				}
				break;
			}
			case DeleteFlight: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Deleting a flight");
				System.out.println("-Flight Number: " + arguments.elementAt(1));

				int flightNum = toInt(arguments.elementAt(1));

				// delete flight by sending tcp request to server
				String res = tcpClientHandler.send("deleteflight," + flightNum);

				if (toBoolean(res)) {
					System.out.println("Flight Deleted");
				} else {
					System.out.println("Flight could not be deleted");
				}
				break;
			}
			case DeleteCars: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Deleting all cars at a particular location");
				System.out.println("-Car Location: " + arguments.elementAt(1));

				String location = arguments.elementAt(1);

				// delete cars by sending tcp request to server
				String res = tcpClientHandler.send("deletecars," + location);

				if (toBoolean(res)) {
					System.out.println("Cars Deleted");
				} else {
					System.out.println("Cars could not be deleted");
				}
				break;
			}
			case DeleteRooms: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Deleting all rooms at a particular location");
				System.out.println("-Car Location: " + arguments.elementAt(1));

				String location = arguments.elementAt(1);

				// delete rooms by sending tcp request to server
				String res = tcpClientHandler.send("deleterooms," + location);

				if (toBoolean(res)) {
					System.out.println("Rooms Deleted");
				} else {
					System.out.println("Rooms could not be deleted");
				}
				break;
			}
			case DeleteCustomer: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Deleting a customer from the database");
				System.out.println("-Customer ID: " + arguments.elementAt(1));
				
				int customerID = toInt(arguments.elementAt(1));

				// delete customer by sending tcp request to server
				String res = tcpClientHandler.send("deletecustomer," + customerID);

				if (toBoolean(res)) {
					System.out.println("Customer Deleted");
				} else {
					System.out.println("Customer could not be deleted");
				}
				break;
			}
            case QueryFlight: {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Querying a flight");
                System.out.println("-Flight Number: " + arguments.elementAt(1));

                int flightNum = toInt(arguments.elementAt(1));

                // query flight by sending tcp request to server
                String res = tcpClientHandler.send("queryflight," + flightNum);

                int seats = toInt(res);
                System.out.println("Number of seats available: " + seats);
                break;
            }
			case QueryCars: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying cars location");
				System.out.println("-Car Location: " + arguments.elementAt(1));
				
				String location = arguments.elementAt(1);

				// query cars by sending tcp request to server
				String res = tcpClientHandler.send("querycars," + location);

				int numCars = toInt(res);
				System.out.println("Number of cars at this location: " + numCars);
				break;
			}
			case QueryRooms: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying rooms location");
				System.out.println("-Room Location: " + arguments.elementAt(1));
				
			
				String location = arguments.elementAt(1);

				String res = tcpClientHandler.send("queryrooms," + location);

				int numRoom = toInt(res);
				System.out.println("Number of rooms at this location: " + numRoom);
				break;
			}
			case QueryCustomer: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying customer information");
				System.out.println("-Customer ID: " + arguments.elementAt(1));

				int customerID = toInt(arguments.elementAt(1));

				String bill = tcpClientHandler.send("querycustomer," + customerID);
				System.out.print(bill);
				break;               
			}
			case QueryFlightPrice: {
				checkArgumentsCount(2, arguments.size());
				
				System.out.println("Querying a flight price");
				System.out.println("-Flight Number: " + arguments.elementAt(1));

				int flightNum = toInt(arguments.elementAt(1));

				// query flight price by sending tcp request to server
				String res = tcpClientHandler.send("queryflightprice," + flightNum);

				int price = toInt(res);
				System.out.println("Price of a seat: " + price);
				break;
			}
			case QueryCarsPrice: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying cars price");
				System.out.println("-Car Location: " + arguments.elementAt(1));

				String location = arguments.elementAt(1);

				String res = tcpClientHandler.send("querycarsprice," + location);

				int price = toInt(res);
				System.out.println("Price of cars at this location: " + price);
				break;
			}
			case QueryRoomsPrice: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Querying rooms price");
				System.out.println("-Room Location: " + arguments.elementAt(1));

				String location = arguments.elementAt(1);

				int price = toInt(tcpClientHandler.send("queryroomsprice," + location));
				System.out.println("Price of rooms at this location: " + price);
				break;
			}
			case ReserveFlight: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Reserving seat in a flight");
				System.out.println("-Customer ID: " + arguments.elementAt(1));
				System.out.println("-Flight Number: " + arguments.elementAt(2));

				int customerID = toInt(arguments.elementAt(1));
				int flightNum = toInt(arguments.elementAt(2));

				String res = tcpClientHandler.send("reserveflight," + customerID + "," + flightNum);

				if (toBoolean(res)) {
					System.out.println("Flight Reserved");
				} else {
					System.out.println("Flight could not be reserved");
				}
				break;
			}
			case ReserveCar: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Reserving a car at a location");
				System.out.println("-Customer ID: " + arguments.elementAt(1));
				System.out.println("-Car Location: " + arguments.elementAt(2));

				int customerID = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);

				String res = tcpClientHandler.send("reservecar," + customerID + "," + location);

				if (toBoolean(res)) {
					System.out.println("Car Reserved");
				} else {
					System.out.println("Car could not be reserved");
				}
				break;
			}
			case ReserveRoom: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Reserving a room at a location");
				System.out.println("-Customer ID: " + arguments.elementAt(1));
				System.out.println("-Room Location: " + arguments.elementAt(2));
				
				int customerID = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);

				String res = tcpClientHandler.send("reserveroom," + customerID + "," + location);

				if (toBoolean(res)) {
					System.out.println("Room Reserved");
				} else {
					System.out.println("Room could not be reserved");
				}
				break;
			}
			case Bundle: {
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
				boolean car = toBoolean(arguments.elementAt(arguments.size()-2));
				boolean room = toBoolean(arguments.elementAt(arguments.size()-1));

				String strArg = "bundle," + customerID;
				for (int i = 0; i < flightNumbers.size(); ++i)
				{
					strArg += "," + flightNumbers.elementAt(i);
				}
				strArg += "," + location + "," + car + "," + room;

				String res = tcpClientHandler.send(strArg);

				if (toBoolean(res)) {
					System.out.println("Bundle Reserved");
				} else {
					System.out.println("Bundle could not be reserved");
				}
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
