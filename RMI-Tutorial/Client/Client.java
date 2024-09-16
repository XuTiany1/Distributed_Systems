import java.rmi.registry.*; // Importing RMI registry classes

public class Client {
    public static void main(String args[]) {
        try {
            // PARAM 1 -> the machine on which the server is running
            // PARAM 2 -> port number of the registry
            // 'LocateRegistry.getRegistry' is used to get a reference to the registry on the specified host and port.
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            
            // 'lookup' method is used to find a reference to the remote object registered with the name "Acc".
            // The object returned is cast to the Account interface, allowing remote method calls.
            Account acc = (Account) registry.lookup("Acc");
          
            // Now I can call methods on it as if it is a local object!
            // Here, calling the 'addToBalance' method on the remote Account object and adding 20.0f to the balance.
            float newBal = acc.addToBalance(20.0f);
            
            // Printing the new balance received from the remote method call.
            System.out.println(newBal);
          
        } catch (Exception e) {
            // If any exception occurs during the RMI process, it will be caught here and printed.
            System.err.println("exception: " + e.getMessage());
        }
    }
}