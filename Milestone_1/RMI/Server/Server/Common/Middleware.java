package Server.Common;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import Server.Interface.IResourceManager;


/**
 * Methods the middleware should implement redirection/assignment of tasks for:
 * add for (flights, cars, rooms)
 * delete for (flights, cars, rooms)
 * query for (flights, cars, rooms)
 * reservation for (fligths, cars, rooms)
 * bundle function
 * 
 * other function:
 * connectserver to connect the three resources managers
 */
public class Middleware extends ResourceManager {
    
    private IResourceManager flight_resource_mnager = null;
    private IResourceManager car_resource_manager = null;
    private IResourceManager room_resource_manager = null;

    public Middleware(String p_name)
    {
        super(p_name);
    }


    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException
    {
        Trace.info("Adding rooms");
        synchronized (room_resource_manager) {
            try {
                return room_resource_manager.addRooms(location, numRooms, price);
            } catch (Exception e) {
                Trace.error(e.toString());
                return false;
            }
        }
    }

    public boolean deleteRooms(String location) throws RemoteException
    {
        Trace.info("delete rooms");
        synchronized (room_resource_manager) {
            try {
                return room_resource_manager.deleteRooms(location);
            } catch (Exception e) {
                Trace.error(e.toString());
                return false;
            }
        }
    }

    public int queryRooms(int id, String location) throws RemoteException
    {
        Trace.info("query Rooms");
        try {
            return room_resource_manager.queryRooms(location);
        } catch (Exception e) {
            Trace.error(e.toString());
            return -1;
        }
    }






    public void connectServer(String type, String server, int port, String name) {
        try {
            boolean first = true;
            while (true) {
                try {
                    Registry registry = LocateRegistry.getRegistry(server, port);
    
                    // Replace switch-case with if-else
                    if (type.equals("Flight")) {
                        flight_resource_mnager = (IResourceManager) registry.lookup(name);
                    } else if (type.equals("Car")) {
                        car_resource_manager = (IResourceManager) registry.lookup(name);
                    } else if (type.equals("Room")) {
                        room_resource_manager = (IResourceManager) registry.lookup(name);
                    }
    
                    System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + name + "]");
                    break;
                } catch (NotBoundException | RemoteException e) {
                    if (first) {
                        System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/" + name + "]");
                        first = false;
                    }
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }



    
}
