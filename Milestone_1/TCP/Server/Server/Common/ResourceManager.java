// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.Common;

import Server.Interface.*;
import java.util.*;


public class ResourceManager implements IResourceManager
{
	protected String m_name = "";
	protected RMHashMap m_data = new RMHashMap();

	public ResourceManager(String p_name)
	{
		m_name = p_name;
	}

	// Reads a data item
	protected RMItem readData(String key)
	{
		synchronized(m_data) {
			RMItem item = m_data.get(key);
			if (item != null) {
				return (RMItem)item.clone();
			}
			return null;
		}
	}

	// Writes a data item
	protected void writeData(String key, RMItem value)
	{
		synchronized(m_data) {
			m_data.put(key, value);
		}
	}

	// Remove the item out of storage
	protected void removeData(String key)
	{
		synchronized(m_data) {
			m_data.remove(key);
		}
	}

	// Deletes the encar item
	protected boolean deleteItem(String key)
	{
		Trace.info("RM::deleteItem(" + key + ") called");
		ReservableItem curObj = (ReservableItem)readData(key);
		// Check if there is such an item in the storage
		if (curObj == null)
		{
			Trace.warn("RM::deleteItem(" + key + ") failed--item doesn't exist");
			return false;
		}
		else
		{
			if (curObj.getReserved() == 0)
			{
				removeData(curObj.getKey());
				Trace.info("RM::deleteItem(" + key + ") item deleted");
				return true;
			}
			else
			{
				Trace.info("RM::deleteItem(" + key + ") item can't be deleted because some customers have reserved it");
				return false;
			}
		}
	}

	// Query the number of available seats/rooms/cars
	protected int queryNum(String key)
	{
		Trace.info("RM::queryNum(" + key + ") called");
		ReservableItem curObj = (ReservableItem)readData(key);
		int value = 0;  
		if (curObj != null)
		{
			value = curObj.getCount();
		}
		Trace.info("RM::queryNum(" + key + ") returns count=" + value);
		return value;
	}    

	// Query the price of an item
	protected int queryPrice(String key)
	{
		Trace.info("RM::queryPrice(" + key + ") called");
		ReservableItem curObj = (ReservableItem)readData(key);
		int value = 0; 
		if (curObj != null)
		{
			value = curObj.getPrice();
		}
		Trace.info("RM::queryPrice(" + key + ") returns cost=$" + value);
		return value;        
	}

	// Reserve an item
	protected boolean reserveItem(int customerID, String key, String location)
	{
		// Check if the item is available
		ReservableItem item = (ReservableItem)readData(key);
		if (item == null)
		{
			Trace.warn("RM::reserveItem(" + customerID + ", " + key + ", " + location + ") failed--item doesn't exist");
			return false;
		}
		else if (item.getCount() == 0)
		{
			Trace.warn("RM::reserveItem(" + customerID + ", " + key + ", " + location + ") failed--No more items");
			return false;
		}
		else
		{            

			// Decrease the number of available items in the storage
			item.setCount(item.getCount() - 1);
			item.setReserved(item.getReserved() + 1);
			writeData(item.getKey(), item);

			Trace.info("RM::reserveItem(" + customerID + ", " + key + ", " + location + ") succeeded");
			return true;
		}        
	}

	// Create a new flight, or add seats to existing flight
	// NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
	public boolean addFlight(int flightNum, int flightSeats, int flightPrice)
	{
		Trace.info("RM::addFlight(" + flightNum + ", " + flightSeats + ", $" + flightPrice + ") called");
		Flight curObj = (Flight)readData(Flight.getKey(flightNum));
		if (curObj == null)
		{
			// Doesn't exist yet, add it
			Flight newObj = new Flight(flightNum, flightSeats, flightPrice);
			writeData(newObj.getKey(), newObj);
			Trace.info("RM::addFlight() created new flight " + flightNum + ", seats=" + flightSeats + ", price=$" + flightPrice);
		}
		else
		{
			// Add seats to existing flight and update the price if greater than zero
			curObj.setCount(curObj.getCount() + flightSeats);
			if (flightPrice > 0)
			{
				curObj.setPrice(flightPrice);
			}
			writeData(curObj.getKey(), curObj);
			Trace.info("RM::addFlight() modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice);
		}
		return true;
	}

	// Create a new car location or add cars to an existing location
	// NOTE: if price <= 0 and the location already exists, it maintains its current price
	public boolean addCars(String location, int count, int price)
	{
		Trace.info("RM::addCars(" + location + ", " + count + ", $" + price + ") called");
		Car curObj = (Car)readData(Car.getKey(location));
		if (curObj == null)
		{
			// Car location doesn't exist yet, add it
			Car newObj = new Car(location, count, price);
			writeData(newObj.getKey(), newObj);
			Trace.info("RM::addCars() created new location " + location + ", count=" + count + ", price=$" + price);
		}
		else
		{
			// Add count to existing car location and update price if greater than zero
			curObj.setCount(curObj.getCount() + count);
			if (price > 0)
			{
				curObj.setPrice(price);
			}
			writeData(curObj.getKey(), curObj);
			Trace.info("RM::addCars() modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
		}
		return true;
	}

	// Create a new room location or add rooms to an existing location
	// NOTE: if price <= 0 and the room location already exists, it maintains its current price
	public boolean addRooms(String location, int count, int price)
	{
		Trace.info("RM::addRooms(" + location + ", " + count + ", $" + price + ") called");
		Room curObj = (Room)readData(Room.getKey(location));
		if (curObj == null)
		{
			// Room location doesn't exist yet, add it
			Room newObj = new Room(location, count, price);
			writeData(newObj.getKey(), newObj);
			Trace.info("RM::addRooms() created new room location " + location + ", count=" + count + ", price=$" + price);
		} else {
			// Add count to existing object and update price if greater than zero
			curObj.setCount(curObj.getCount() + count);
			if (price > 0)
			{
				curObj.setPrice(price);
			}
			writeData(curObj.getKey(), curObj);
			Trace.info("RM::addRooms() modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
		}
		return true;
	}

	// Deletes flight
	public boolean deleteFlight(int flightNum)
	{
		return deleteItem(Flight.getKey(flightNum));
	}

	// Delete cars at a location
	public boolean deleteCars(String location)
	{
		return deleteItem(Car.getKey(location));
	}

	// Delete rooms at a location
	public boolean deleteRooms(String location)
	{
		return deleteItem(Room.getKey(location));
	}

	// Returns the number of empty seats in this flight
	public int queryFlight(int flightNum)
	{
		return queryNum(Flight.getKey(flightNum));
	}

	// Returns the number of cars available at a location
	public int queryCars(String location)
	{
		return queryNum(Car.getKey(location));
	}

	// Returns the amount of rooms available at a location
	public int queryRooms(String location)
	{
		return queryNum(Room.getKey(location));
	}

	// Returns price of a seat in this flight
	public int queryFlightPrice(int flightNum)
	{
		return queryPrice(Flight.getKey(flightNum));
	}

	// Returns price of cars at this location
	public int queryCarsPrice(String location)
	{
		return queryPrice(Car.getKey(location));
	}

	// Returns room price at this location
	public int queryRoomsPrice(String location)
	{
		return queryPrice(Room.getKey(location));
	}

	public String queryCustomerInfo(int customerID)
	{
		Trace.info("RM::queryCustomerInfo(" + customerID + ") called");
		Customer customer = (Customer)readData(Customer.getKey(customerID));
		if (customer == null)
		{
			Trace.warn("RM::queryCustomerInfo(" + customerID + ") failed--customer doesn't exist");
			// NOTE: don't change this--WC counts on this value indicating a customer does not exist...
			return "";
		}
		else
		{
			Trace.info("RM::queryCustomerInfo(" + customerID + ")");
			System.out.println(customer.getBill());
			return customer.getBill();
		}
	}

	public int newCustomer()
	{
		// customer operations are handled by the middleware
		return -1;
	}

	public boolean newCustomer(int customerID)
	{
		return false; // customer operations are handled by the middleware
	}

	public boolean deleteCustomer(int customerID)
	{
		return false; // customer operations are handled by the middleware
	}

	// Adds flight reservation to this customer
	public boolean reserveFlight(int customerID, int flightNum)
	{
		return reserveItem(customerID, Flight.getKey(flightNum), String.valueOf(flightNum));
	}

	// Adds car reservation to this customer
	public boolean reserveCar(int customerID, String location)
	{
		return reserveItem(customerID, Car.getKey(location), location);
	}

	// Adds room reservation to this customer
    public boolean reserveRoom(int customerID, String location)
	{
		return reserveItem(customerID, Room.getKey(location), location);
	}

	// Reserve bundle 
	public boolean bundle(int customerId, Vector<String> flightNumbers, String location, boolean car, boolean room)
	{

		return false;
	}

	public String getName()
	{
		return m_name;
	}

	public boolean updateReservation(String itemKey, int itemCount){
		try {
			ReservableItem item  = (ReservableItem)readData(itemKey);

			item.setReserved(item.getReserved() - itemCount);
			item.setCount(item.getCount() + itemCount);
			writeData(item.getKey(), item);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
 
