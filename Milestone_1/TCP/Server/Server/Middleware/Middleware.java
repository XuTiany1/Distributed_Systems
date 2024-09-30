package Server.Middleware;

import Server.Common.*;
import Server.Interface.*;
import java.util.Vector;

import TCPHandlers.*;

public abstract class Middleware extends ResourceManager {
    
    // Instance of
    private TCPClientHandler flightTcpClientHandler = null;
    private TCPClientHandler carTcpClientHandler = null;
    private TCPClientHandler roomTcpClientHandler = null;


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
	public int queryFlight(int flightNum)
	{
        return toInt(flightTcpClientHandler.send("queryflight," + flightNum));
	}

	@Override
	public int queryCars(String location)
	{
        return toInt(carTcpClientHandler.send("querycars," + location));
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