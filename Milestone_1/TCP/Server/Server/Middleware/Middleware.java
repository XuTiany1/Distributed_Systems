package Server.Middleware;

import Server.Common.*;
import Server.Interface.*;

import java.util.Calendar;
import java.util.Vector;

import TCPHandlers.*;

// Customer operations are handled in middleware
public abstract class Middleware extends ResourceManager {
    
    // Instance of
    private TCPClientHandler flightTcpClientHandler = null;
    private TCPClientHandler carTcpClientHandler = null;
    private TCPClientHandler roomTcpClientHandler = null;

	protected RMHashMap m_data = new RMHashMap();


    public Middleware(String name) {
        super(name);
    }

    // setter for TCPClientHandlers
    public void setFlightTcpClientHandler(TCPClientHandler flightTcpClientHandler) {
        this.flightTcpClientHandler = flightTcpClientHandler;
    }

    public void setCarTcpClientHandler(TCPClientHandler carTcpClientHandler) {
        this.carTcpClientHandler = carTcpClientHandler;
    }

    public void setRoomTcpClientHandler(TCPClientHandler roomTcpClientHandler) {
        this.roomTcpClientHandler = roomTcpClientHandler;
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

	// Reserve an item
	protected boolean reserveItem(int customerID, String key, String location)
	{
		Trace.info("RM::reserveItem(customer=" + customerID + ", " + key + ", " + location + ") called" );        
		// Read customer object if it exists (and read lock it)
		Customer customer = (Customer)readData(Customer.getKey(customerID));
		if (customer == null)
		{
			Trace.warn("RM::reserveItem(" + customerID + ", " + key + ", " + location + ")  failed--customer doesn't exist");
			return false;
		} 

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
			customer.reserve(key, location, item.getPrice());        
			writeData(customer.getKey(), customer);

			// Decrease the number of available items in the storage
			item.setCount(item.getCount() - 1);
			item.setReserved(item.getReserved() + 1);
			writeData(item.getKey(), item);

			Trace.info("RM::reserveItem(" + customerID + ", " + key + ", " + location + ") succeeded");
			return true;
		}        
	}

	@Override
	public boolean addFlight(int flightNum, int flightSeats, int flightPrice)
	{
		return toBoolean(flightTcpClientHandler.send("addflight," + flightNum + "," + flightSeats + "," + flightPrice));
	}

	@Override
	public boolean addCars(String location, int numCars, int price)
	{
        return toBoolean(carTcpClientHandler.send("addcars," + location + "," + numCars + "," + price));
	}

	@Override
	public boolean addRooms(String location, int numRooms, int price)
	{
        return toBoolean(roomTcpClientHandler.send("addrooms," + location + "," + numRooms + "," + price));
	}

    @Override
	// Deletes flight
	public boolean deleteFlight(int flightNum)
	{
		return toBoolean(flightTcpClientHandler.send("deleteflight," + flightNum));
	}

    @Override
	public boolean deleteCars(String location)
	{
		return toBoolean(carTcpClientHandler.send("deletecars," + location));
	}

	@Override
	public boolean deleteRooms(String location)
	{
		return toBoolean(roomTcpClientHandler.send("deleterooms," + location));
	}

	@Override
	public int queryFlight(int flightNum)
	{
        return toInt(flightTcpClientHandler.send("queryflight," + flightNum));
	}

	@Override
	public int queryCars(String location)
	{
        return toInt(carTcpClientHandler.send("querycars," + location));
	}

	@Override
	public int queryRooms(String location)
	{
		return toInt(roomTcpClientHandler.send("queryrooms," + location));
	}

	@Override
	public int queryFlightPrice(int flightNum)
	{
		return toInt(flightTcpClientHandler.send("queryflightprice," + flightNum));
	}

	@Override
	public int queryCarsPrice(String location)
	{
		return toInt(carTcpClientHandler.send("querycarsprice," + location));
	}

	@Override
	public int queryRoomsPrice(String location)
	{
		return toInt(roomTcpClientHandler.send("queryroomsprice," + location));
	}


    // Customer operations are handled in middleware
    @Override
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

    @Override
	public int newCustomer()
	{
        Trace.info("RM::newCustomer() called");
		// Generate a globally unique ID for the new customer; if it generates duplicates for you, then adjust
		int cid = Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
			String.valueOf(Math.round(Math.random() * 100 + 1)));
		Customer customer = new Customer(cid);
		writeData(customer.getKey(), customer);
		Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid);
		return cid;
	}

    @Override
	public boolean newCustomer(int customerID)
	{
		Trace.info("RM::newCustomer(" + customerID + ") called");
		Customer customer = (Customer)readData(Customer.getKey(customerID));
		if (customer == null)
		{
			customer = new Customer(customerID);
			writeData(customer.getKey(), customer);
			Trace.info("RM::newCustomer(" + customerID + ") created a new customer");
			return true;
		}
		else
		{
			Trace.info("INFO: RM::newCustomer(" + customerID + ") failed--customer already exists");
			return false;
		}
	}

    @Override
	public boolean deleteCustomer(int customerID)
	{
		Trace.info("RM::deleteCustomer(" + customerID + ") called");
		Customer customer = (Customer)readData(Customer.getKey(customerID));
		if (customer == null)
		{
			Trace.warn("RM::deleteCustomer(" + customerID + ") failed--customer doesn't exist");
			return false;
		}
		else
		{            
			// Increase the reserved numbers of all reservable items which the customer reserved. 
 			RMHashMap reservations = customer.getReservations();
			for (String reservedKey : reservations.keySet())
			{        
				ReservedItem reserveditem = customer.getReservedItem(reservedKey);
				Trace.info("RM::deleteCustomer(" + customerID + ") has reserved " + reserveditem.getKey() + " " +  reserveditem.getCount() +  " times");
                
                synchronized (flightTcpClientHandler) {
                    if (reserveditem.getKey().contains("flight")) {
                        flightTcpClientHandler.send("updatereservation," + reserveditem.getKey() + "," + reserveditem.getCount());
                    }
                }

                synchronized (carTcpClientHandler) {
                    if (reserveditem.getKey().contains("car")) {
                        carTcpClientHandler.send("updatereservation," + reserveditem.getKey() + "," + reserveditem.getCount());
                    }
                }

                synchronized (roomTcpClientHandler) {
                    if (reserveditem.getKey().contains("room")) {
                        roomTcpClientHandler.send("updatereservation," + reserveditem.getKey() + "," + reserveditem.getCount());
                    }
                }
			}

			// Remove the customer from the storage
			removeData(customer.getKey());
			Trace.info("RM::deleteCustomer(" + customerID + ") succeeded");
			return true;
		}
	}


	@Override
	public boolean reserveFlight(int customerID, int flightNum)
	{
        // Read customer object if it exists (and read lock it)
		Customer customer = (Customer)readData(Customer.getKey(customerID));
		if (customer == null)
		{
			Trace.warn("RM::reserveflight failed--customer doesn't exist");
			return false;
		}


        synchronized (flightTcpClientHandler) {
            // send request to flight server to get price
            String price = flightTcpClientHandler.send("queryflightprice," + flightNum);

            // send request to flight server
            String res = flightTcpClientHandler.send("reserveflight," + customerID + "," + flightNum);

            if (toBoolean(res)) {
                customer.reserve(Flight.getKey(flightNum), String.valueOf(flightNum), toInt(price));
                writeData(customer.getKey(), customer);
                return true;
            }
            return false;
        }
	}

	@Override
	public boolean reserveCar(int customerID, String location)
	{
		// Read customer object if it exists (and read lock it)
		Customer customer = (Customer)readData(Customer.getKey(customerID));
		if (customer == null)
		{
			Trace.warn("RM::reservecar failed--customer doesn't exist");
			return false;
		}

        synchronized (carTcpClientHandler) {
            // send request to car server to oget price

            String price = carTcpClientHandler.send("querycarsprice," + location);

            // send request to car server
            String res = carTcpClientHandler.send("reservecar," + customerID + "," + location);

            if (toBoolean(res)) {
                customer.reserve(Car.getKey(location), location, toInt(price));
                writeData(customer.getKey(), customer);
                return true;
            }
            return false;
        }
	}

	@Override
    public boolean reserveRoom(int customerID, String location)
	{
		// Read customer object if it exists (and read lock it)
        Customer customer = (Customer)readData(Customer.getKey(customerID));
        if (customer == null)
        {
            Trace.warn("RM::reserveroom failed--customer doesn't exist");
            return false;
        }

        synchronized (roomTcpClientHandler) {
            // send request to room server to get price
            String price = roomTcpClientHandler.send("queryroomsprice," + location);

            // send request to room server
            String res = roomTcpClientHandler.send("reserveroom," + customerID + "," + location);

            if (toBoolean(res)) {
                customer.reserve(Room.getKey(location), location, toInt(price));
                writeData(customer.getKey(), customer);
                return true;
            }
            return false;
        }
	}

	@Override
	public boolean bundle(int customerId, Vector<String> flightNumbers, String location, boolean car, boolean room)
	{
		
		return false;
	}
        
    // function to convert string to boolean
    public static boolean toBoolean(String string) {
        return (Boolean.valueOf(string)).booleanValue();
    }

    // function to convert string to integer
    public static int toInt(String string) {
        return (Integer.valueOf(string)).intValue();
    }
}