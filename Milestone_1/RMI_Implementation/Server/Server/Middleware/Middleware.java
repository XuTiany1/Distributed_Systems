package Server.Middleware;

public abstract class Middleware extends ResourceManager {
    
    // Instance of
	IResourceManager flight_resourceManager = null;
	IResourceManager car_resourceManager = null;
	IResourceManager room_resourceManager = null;

    public Middleware(String name) {
        super(name);
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
		return flight_resourceManager.newCustomer();
	}

	@Override
	public boolean newCustomer(int cid) throws RemoteException
	{
		return flight_resourceManager.newCustomer(cid);
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
	public boolean deleteRooms(String location) throws RemoteException {
		return room_resourceManager.deleteRooms(location);
	}

	@Override
	public int queryFlight(int flightNum) throws RemoteException {
		return flight_resourceManager.queryFlight(flightNum);
	}

	@Override
	public int queryCars(String location) throws RemoteException {
		return car_resourceManager.queryCars(location);
	}

	@Override
	public int queryRooms(String location) throws RemoteException {
		return room_resourceManager.queryRooms(location);
	}

	@Override
	public int queryFlightPrice(int flightNum) throws RemoteException {
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
		return flight_resourceManager.reserveFlight(customerID, flightNum);
	}

	@Override
	public boolean reserveCar(int customerID, String location) throws RemoteException {
		return car_resourceManager.reserveCar(customerID, location);
	}

	@Override
	public boolean reserveRoom(int customerID, String location) throws RemoteException {
		return room_resourceManager.reserveRoom(customerID, location);
	}

	@Override
	public boolean bundle(int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
		return false;
	}
    
}
