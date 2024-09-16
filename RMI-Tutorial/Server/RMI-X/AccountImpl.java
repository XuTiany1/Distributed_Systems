import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.AlreadyBoundException;
import java.rmi.server.UnicastRemoteObject;

public class AccountImpl implements Account
{
			
  float balance;
  int ID;
 			
  public AccountImpl(float b, int id){  balance=b; ID=id; }
  public float getBalance() { return balance; }

  // Synchronized method to ensure thread safety when modifying the balance.
  // This prevents race conditions when multiple threads try to access this method.
  public synchronized float addToBalance (float amount)
  {
      float newbalance = balance + amount;
      try { Thread.sleep(5000); } catch (Exception e) {}
      balance = newbalance;
      return balance;
   };
			

    // Main method to set up the RMI server.
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        // Creating a new instance of the AccountImpl object with initial values.
        AccountImpl acc = new AccountImpl(1234, 2000);

        // Exporting this object to create a stub (proxy) for remote method calls.
        // The second argument '0' allows the system to select an available port automatically.
        Account accproxy = (Account) UnicastRemoteObject.exportObject(acc, 0);

        Registry registry;

        try {
            // Attempting to create a new RMI registry on the specified port.
            registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
        } catch (RemoteException e) {
            // If creating the registry fails, likely because one is already running on the port.
            System.out.println("Trying to connect to an external registry");

            // Attempting to connect to an existing registry on the specified port.
            registry = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
        }

        // Binding the proxy object to the registry with the name "Acc".
        // This makes the remote object available to clients under the name "Acc".
        registry.bind("Acc", accproxy);
    }
}
