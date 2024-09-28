package Server.Common;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import Server.Interface.IResourceManager;

public class Middleware extends ResourceManager {
    
    private IResourceManager flight_resource_mnager = null;
    private IResourceManager car_resource_manager = null;
    private IResourceManager room_resource_manager = null;

    public Middleware(String p_name)
    {
        super(p_name);
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
