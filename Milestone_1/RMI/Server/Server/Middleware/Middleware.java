package Server.Middleware;

import Server.Common.*;
import Server.Interface.*;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Vector;

public abstract class Middleware extends ResourceManager {
    
    // Instance of
	IResourceManager flight_resourceManager = null;
	IResourceManager car_resourceManager = null;
	IResourceManager room_resourceManager = null;

    public Middleware(String name) {
        super(name);
    }

	public void set_flight_resourceManager(IResourceManager flight_resourceManager) {
		this.flight_resourceManager = flight_resourceManager;
	}

	public void set_car_resourceManager(IResourceManager car_resourceManager) {
		this.car_resourceManager = car_resourceManager;
	}

	public void set_room_resourceManager(IResourceManager room_resourceManager) {
		this.room_resourceManager = room_resourceManager;
	}

	@Override
	public boolean addFlight(int flightNum, int flightSeats, int flightPrice) throws RemoteException
	{
		return flight_resourceManager.addFlight(flightNum, flightSeats, flightPrice);
	}

	@Override
	public boolean addCars(String location, int numCars, int price) throws RemoteException
	{
		return car_resourceManager.addCars(location, numCars, price);
	}

	@Override
	public boolean addRooms(String location, int numRooms, int price) throws RemoteException
	{
		return room_resourceManager.addRooms(location, numRooms, price);
	}

	@Override
	public int newCustomer() throws RemoteException
	{
		Trace.info("RM::newCustomer() called");
        // Generate a globally unique ID for the new customer; if it generates duplicates for you, then adjust
        int cid = Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND))
                + String.valueOf(Math.round(Math.random() * 100 + 1)));
        Customer customer = new Customer(cid);
        writeData(customer.getKey(), customer);
        Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid);
        return cid;
	}

	@Override
	public boolean newCustomer(int cid) throws RemoteException
	{
        Trace.info("RM::newCustomer(" + cid + ") called");
        Customer customer = (Customer) readData(Customer.getKey(cid));
        if (customer == null) {
            customer = new Customer(cid);
            writeData(customer.getKey(), customer);
            Trace.info("RM::newCustomer(" + cid + ") created a new customer");
            return true;
        } else {
            Trace.info("INFO: RM::newCustomer(" + cid + ") failed--customer already exists");
            return false;
        }
	}

	@Override
    public boolean deleteCustomer(int customerID) {
        Trace.info("RM::deleteCustomer(" + customerID + ") called");
        Customer customer = (Customer) readData(Customer.getKey(customerID));
        if (customer == null) {
            Trace.warn("RM::deleteCustomer(" + customerID + ") failed--customer doesn't exist");
            return false;
        } else {
            // Increase the reserved numbers of all reservable items which the customer reserved. 
            RMHashMap reservations = customer.getReservations();
            for (String reservedKey : reservations.keySet()) {
                ReservedItem reserveditem = customer.getReservedItem(reservedKey);
                Trace.info("RM::deleteCustomer(" + customerID + ") has reserved " + reserveditem.getKey() + " " + reserveditem.getCount() + " times");

				if (reserveditem.getKey().contains("flight")) {
					flight_resourceManager.updateReservation(reserveditem.getKey(), reserveditem.getCount());
				}

				if (reserveditem.getKey().contains("car")) {
					car_resourceManager.updateReservation(reserveditem.getKey(), reserveditem.getCount());
				}

				if (reserveditem.getKey().contains("room")) {
					room_resourceManager.updateReservation(reserveditem.getKey(), reserveditem.getCount());
				}
            }

            // Remove the customer from the storage
            removeData(customer.getKey());
            Trace.info("RM::deleteCustomer(" + customerID + ") succeeded");
            return true;
        }
    }

	@Override
	public boolean deleteFlight(int flightNum) throws RemoteException
	{
		return flight_resourceManager.deleteFlight(flightNum);
	}

	@Override
	public boolean deleteCars(String location) throws RemoteException
	{
		return car_resourceManager.deleteCars(location);
	}

	@Override
	public boolean deleteRooms(String location) throws RemoteException
	{
		return room_resourceManager.deleteRooms(location);
	}

	@Override
	public int queryFlight(int flightNum) throws RemoteException
	{
		return flight_resourceManager.queryFlight(flightNum);
	}

	@Override
	public int queryCars(String location) throws RemoteException
	{
		return car_resourceManager.queryCars(location);
	}
	
	@Override
	public int queryRooms(String location) throws RemoteException
	{
		return room_resourceManager.queryRooms(location);
	}

	@Override
	public int queryFlightPrice(int flightNum) throws RemoteException
	{
		return flight_resourceManager.queryFlightPrice(flightNum);
	}

	@Override
	public int queryCarsPrice(String location) throws RemoteException {
		return car_resourceManager.queryCarsPrice(location);
	}

	@Override
	public int queryRoomsPrice(String location) throws RemoteException {
		return room_resourceManager.queryRoomsPrice(location);
	}

	@Override
	public boolean reserveFlight(int customerID, int flightNum) throws RemoteException {
        // Read customer object if it exists (and read lock it)
        Customer customer = (Customer) readData(Customer.getKey(customerID));
        if (customer == null) {
            Trace.warn("RM::reserveflight failed--customer doesn't exist");
            return false;
        }

		int price = flight_resourceManager.queryFlightPrice(flightNum);

		Boolean res = flight_resourceManager.reserveFlight(customerID, flightNum);

		if (res) {
			customer.reserve(Flight.getKey(flightNum), String.valueOf(flightNum), price);
			writeData(customer.getKey(), customer);
			return true;
		}
		return false;
	}

	@Override
	public boolean reserveCar(int customerID, String location) throws RemoteException {
		// Read customer object if it exists (and read lock it)
		Customer customer = (Customer) readData(Customer.getKey(customerID));
		if (customer == null) {
			Trace.warn("RM::reserveCar failed--customer doesn't exist");
			return false;
		}

		int price = car_resourceManager.queryCarsPrice(location);

		Boolean res = car_resourceManager.reserveCar(customerID, location);

		if (res) {
			customer.reserve(Car.getKey(location), location, price);
			writeData(customer.getKey(), customer);
			return true;
		}
		return false;
	}

	@Override
	public boolean reserveRoom(int customerID, String location) throws RemoteException {
		// Read customer object if it exists (and read lock it)
		Customer customer = (Customer) readData(Customer.getKey(customerID));
		if (customer == null) {
			Trace.warn("RM::reserveRoom failed--customer doesn't exist");
			return false;
		}

		int price = room_resourceManager.queryRoomsPrice(location);

		Boolean res = room_resourceManager.reserveRoom(customerID, location);

		if (res) {
			customer.reserve(Room.getKey(location), location, price);
			writeData(customer.getKey(), customer);
			return true;
		}
		return false;
	}

	@Override
	public boolean bundle(int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
		return false;
	}
    
}
