import java.rmi.registry.Registry; // Importing RMI registry classes.
import java.rmi.registry.LocateRegistry; // Importing class to locate or create the RMI registry.
import java.rmi.RemoteException; // Importing the exception that remote methods can throw.
import java.rmi.AlreadyBoundException; // Exception thrown if an object is already bound in the registry.
import java.rmi.server.UnicastRemoteObject; // Used to export the remote object and make it available to clients.

// This is a class that implements the Account interface, making it a remote object.
public class AccountImpl implements Account {

    float balance; // Variable to store the balance.
    int ID; // Variable to store the account ID.

    // Constructor to initialize balance and ID.
    public AccountImpl(float b, int id) {
        balance = b; 
        ID = id; 
    }

    // Implementation of the getBalance method from the Account interface.
    public float getBalance() {
        return balance; 
    }

    // Implementation of the addToBalance method from the Account interface.
    // Adds the given amount to the balance and returns the new balance.
    public float addToBalance(float amount) {
        return balance += amount; 
    }

    // Main method to set up the RMI server.
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        // First, I create a new instance of the object.
        AccountImpl acc = new AccountImpl(1234, 2000);

        // Then, we EXPORT this object and make it known to the world.
        // UnicastRemoteObject.exportObject creates a stub (proxy) for the object that can be used by the client.
        // The second argument '0' lets the system choose a port for the RMI server.
        Account accproxy = (Account) UnicastRemoteObject.exportObject(acc, 0);

        // Creating or locating the RMI registry on the specified port (passed as an argument).
        Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0])); // Param is the port number.

        // Binding our proxy object to the registry with the name "Acc".
        // This makes the object available to clients that lookup "Acc" in the registry.
        registry.bind("Acc", accproxy);
    }
}