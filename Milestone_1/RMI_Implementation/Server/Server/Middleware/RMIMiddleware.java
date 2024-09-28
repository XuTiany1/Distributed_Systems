package Server.Middleware;
import Server.Interface.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

//  * Functions:
//  * - ResourceManager(String p_name): Constructor to initialize the resource manager with a specific name.
//  * - readData(String key): Reads a data item from storage.
//  * - writeData(String key, RMItem value): Writes a data item to storage.
//  * - removeData(String key): Removes a data item from storage.
//  * - deleteItem(String key): Deletes a reservable item if it has no reservations.
//  * - queryNum(String key): Returns the number of available items (seats, rooms, cars).
//  * - queryPrice(String key): Returns the price of an item.
//  * - reserveItem(int customerID, String key, String location): Reserves an item for a customer and updates storage.
//  * - addFlight(int flightNum, int flightSeats, int flightPrice): Adds a flight or updates an existing flight's seat count and price.
//  * - addCars(String location, int count, int price): Adds cars or updates an existing location's car count and price.
//  * - addRooms(String location, int count, int price): Adds rooms or updates an existing location's room count and price.
//  * - deleteFlight(int flightNum): Deletes a flight.
//  * - deleteCars(String location): Deletes all cars at a location.
//  * - deleteRooms(String location): Deletes all rooms at a location.
//  * - queryFlight(int flightNum): Returns the number of available seats in a flight.
//  * - queryCars(String location): Returns the number of available cars at a location.
//  * - queryRooms(String location): Returns the number of available rooms at a location.
//  * - queryFlightPrice(int flightNum): Returns the price per seat for a flight.
//  * - queryCarsPrice(String location): Returns the price of cars at a location.
//  * - queryRoomsPrice(String location): Returns the price of rooms at a location.
//  * - newCustomer(): Creates a new customer with a unique ID.
//  * - newCustomer(int customerID): Creates a new customer with the specified ID.
//  * - deleteCustomer(int customerID): Deletes a customer and releases any reserved items.
//  * - reserveFlight(int customerID, int flightNum): Reserves a flight for a customer.
//  * - reserveCar(int customerID, String location): Reserves a car for a customer.
//  * - reserveRoom(int customerID, String location): Reserves a room for a customer.
//  * - bundle(int customerId, Vector<String> flightNumbers, String location, boolean car, boolean room): Placeholder for bundling reservations (not implemented).
//  * - getName(): Returns the name of the resource manager.
//  */

public class RMIMiddleware extends Middleware {
    private static String flight_serverHost = "localhost";
    private static String car_serverHost = "localhost";
	private static String room_serverHost = "localhost";
    // recommended to hange port last digits to your group number
	private static int flight_serverPort = 3019;
	private static int car_serverPort = 3019;
	private static int room_serverPort = 3019;

	// KK: These server names are the same as the ones already mentioned in run_servers.sh
	private static String flight_serverName = "Flights";
	private static String car_serverName = "Cars";
	private static String room_serverName = "Rooms";

	//TODO: ADD YOUR GROUP NUMBER TO COMPILE
	private static String middleware_rmiPrefix = "group_19_";
	private static String middleware_name = "Middleware";

	public static void main(String args[])
	{
		// 1. expose mideleware as a server to the client
		
		RMIMiddleware middleware = null;
		// Create the RMI middleware entry
		try {
			// Create a new Middleware object
			middleware = new RMIMiddleware();

			// Dynamically generate the stub (client proxy)
			IResourceManager resourceManager = (IResourceManager)UnicastRemoteObject.exportObject(middleware, 0);

			// Bind the remote object's stub in the registry; adjust port if appropriate
			Registry l_registry;
			try {
				l_registry = LocateRegistry.createRegistry(3019);
			} catch (RemoteException e) {
				l_registry = LocateRegistry.getRegistry(3019);
			}
			final Registry registry = l_registry;
			registry.rebind(middleware_rmiPrefix + middleware_name, resourceManager);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						registry.unbind(middleware_rmiPrefix + middleware_name);
						System.out.println("'" + middleware_name + "' resource manager unbound");
					}
					catch(Exception e) {
						System.err.println((char)27 + "[31;1mMiddleware exception: " + (char)27 + "[0mUncaught exception");
						e.printStackTrace();
					}
				}
			});                                       
			System.out.println("'" + middleware_name + "' resource manager middleware ready and bound to '" + middleware_rmiPrefix + middleware_name + "'");
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mMiddleware exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

		// 2. middleware as a client to the RMIServer
		if (args.length > 0)
		{
			flight_serverHost = args[0];
		}
		if (args.length > 1)
		{
			car_serverHost = args[1];
		}
		if (args.length > 2)
		{
			room_serverHost = args[2];
		}

		// Get a reference to the RMIRegister
		try {
			middleware.connectServers();
		} 
		catch (Exception e) {    
			System.err.println((char)27 + "[31;1mMiddleware exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public RMIMiddleware()
	{
		super("Middleware");
	}

	public void connectServers()
	{
		connectFlightServer(flight_serverHost, flight_serverPort, flight_serverName);
		connectCarServer(car_serverHost, car_serverPort, car_serverName);
		connectRoomServer(room_serverHost, room_serverPort, room_serverName);
	}

	public void connectServers(ArrayList<String> servers, ArrayList<Integer> ports, ArrayList<String> names)
	{
		connectFlightServer(servers.get(0), ports.get(0), names.get(0));
		connectCarServer(servers.get(1), ports.get(1), names.get(1));
		connectRoomServer(servers.get(2), ports.get(2), names.get(2));

	}

	public void connectFlightServer(String server, int port, String name)
	{
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					flight_resourceManager = (IResourceManager)registry.lookup(middleware_rmiPrefix + name);
					this.set_flight_resourceManager(flight_resourceManager);
					System.out.println("Connected to '" + name + "' flight server [" + server + ":" + port + "/" + middleware_rmiPrefix + name + "]");
					break;
				}
				catch (NotBoundException|RemoteException e) {
					if (first) {
						System.out.println("Waiting for '" + name + "' flight server [" + server + ":" + port + "/" + middleware_rmiPrefix + name + "]");
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

	public void connectCarServer(String server, int port, String name)
	{
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					car_resourceManager = (IResourceManager)registry.lookup(middleware_rmiPrefix + name);
					this.set_car_resourceManager(car_resourceManager);
					System.out.println("Connected to '" + name + "' car server [" + server + ":" + port + "/" + middleware_rmiPrefix + name + "]");
					break;
				}
				catch (NotBoundException|RemoteException e) {
					if (first) {
						System.out.println("Waiting for '" + name + "' car server [" + server + ":" + port + "/" + middleware_rmiPrefix + name + "]");
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

	public void connectRoomServer(String server, int port, String name)
	{
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					room_resourceManager = (IResourceManager)registry.lookup(middleware_rmiPrefix + name);
					this.set_room_resourceManager(room_resourceManager);
					System.out.println("Connected to '" + name + "' room server [" + server + ":" + port + "/" + middleware_rmiPrefix + name + "]");
					break;
				}
				catch (NotBoundException|RemoteException e) {
					if (first) {
						System.out.println("Waiting for '" + name + "' room server [" + server + ":" + port + "/" + middleware_rmiPrefix + name + "]");
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
