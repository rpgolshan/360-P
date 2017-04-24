import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
 
public interface Compute extends Remote {
    <T> void executeCallable(Callable<T> c, String DistribTaskID) throws RemoteException;

	void executeRunnable(Runnable rtask, String myID, Object result) throws RemoteException;
}