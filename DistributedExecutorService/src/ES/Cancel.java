import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
 
public interface Cancel extends Remote {
    boolean executeCancel(String DistribTaskID, boolean interruptable) throws RemoteException;
}