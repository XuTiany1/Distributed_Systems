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
  public float addToBalance (float amount) {  return balance+=amount; };
			
  public static void main (String[] args) throws RemoteException, AlreadyBoundException
  {
      AccountImpl acc = new AccountImpl(1234, 2000);
      Account accproxy = (Account) UnicastRemoteObject.exportObject(acc, 0);
      //Registry registry = LocateRegistry.getRegistry();
      Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
      registry.bind("Acc", accproxy);
  }
}

