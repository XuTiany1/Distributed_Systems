import java.rmi.Remote;
import java.rmi.RemoteException;
public interface Account extends Remote
{
	public float getBalance() throws RemoteException;
	public float addToBalance (float amount) throws RemoteException;
}

