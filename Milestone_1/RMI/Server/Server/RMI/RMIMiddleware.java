package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.rmi.NotBoundException;
import java.util.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class RMIMiddleware extends Middleware {

    private static String middleware_server_name = "Middleware";
    private static String middleware_rmiPrefix = "group_19_";

    // Resource Manager names
    private static String s_flightServerName;
    private static String s_flightServerHost;
    private static int s_flightServerPort;

    private static String s_carServerName;
    private static String s_carServerHost;
    private static int s_carServerPort;

    private static String s_roomServerName;
    private static String s_roomServerHost;
    private static int s_roomServerPort;

    public static void main(String[] args) {

        // Step 1: Middleware fetches resource managers
        if (args.length == 3) {
            try {
                // Flight server info
                String[] flightInfo = args[0].split(",");
                s_flightServerName = middleware_rmiPrefix + flightInfo[0];
                s_flightServerHost = flightInfo[1];
                s_flightServerPort = Integer.parseInt(flightInfo[2]);

                // Car server info
                String[] carInfo = args[1].split(",");
                s_carServerName = middleware_rmiPrefix + carInfo[0];
                s_carServerHost = carInfo[1];
                s_carServerPort = Integer.parseInt(carInfo[2]);

                // Room server info
                String[] roomInfo = args[2].split(",");
                s_roomServerName = middleware_rmiPrefix + roomInfo[0];
                s_roomServerHost = roomInfo[1];
                s_roomServerPort = Integer.parseInt(roomInfo[2]);
            } catch (Exception e) {
                System.err.println((char) 27 + "[31;1mMiddleware exception: " + (char) 27 + "[0mUncaught exception");
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.err.println((char) 27 + "[31;1mMiddleware exception: " + (char) 27 + "[0mInvalid number of arguments; expected 3 args");
            System.exit(1);
        }

        // Step 2: Try to connect to the ResourceManagers and register middleware resource manager
        try {
            RMIMiddleware middleware = new RMIMiddleware(middleware_server_name);
            
            // Connect to the three resource managers
            middleware.connectServer("Flight", s_flightServerHost, s_flightServerPort, s_flightServerName);
            middleware.connectServer("Car", s_carServerHost, s_carServerPort, s_carServerName);
            middleware.connectServer("Room", s_roomServerHost, s_roomServerPort, s_roomServerName);

            // Step 3: Middleware talks to client and binds to RMI
            IResourceManager resourceManager = (IResourceManager) UnicastRemoteObject.exportObject(middleware, 0);

            // Bind the remote object's stub in the registry; adjust port if appropriate
            Registry l_registry;
            try {
                l_registry = LocateRegistry.createRegistry(3019);
            } catch (RemoteException e) {
                l_registry = LocateRegistry.getRegistry(3019);
            }
            final Registry registry = l_registry;
            registry.rebind(middleware_rmiPrefix + middleware_server_name, resourceManager);

            // Handle shutdown
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        registry.unbind(middleware_rmiPrefix + middleware_server_name);
                        System.out.println("'" + middleware_server_name + "' resource manager unbound");
                    } catch (Exception e) {
                        System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("'" + middleware_server_name + "' resource manager server ready and bound to '" + middleware_rmiPrefix + middleware_server_name + "'");
        } catch (Exception e) {
            System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public RMIMiddleware(String name) {
        super(name);
    }

    

}