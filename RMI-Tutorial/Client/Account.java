import java.rmi.Remote; // Importing the Remote interface, which is the base for all remote interfaces in RMI.
import java.rmi.RemoteException; // Importing the exception that must be handled in remote methods.

// An interface file that indicates what methods can be called remotely.
public interface Account extends Remote {

    // Remote methods must declare that they throw RemoteException.
    // This method returns the balance as a float and might throw a RemoteException if something goes wrong.
    public float getBalance() throws RemoteException;
    
    // This method adds the specified amount to the balance and returns the new balance.
    // It might throw a RemoteException if there's an issue with the remote call.
    public float addToBalance(float amount) throws RemoteException;
	
}