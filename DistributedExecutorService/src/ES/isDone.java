import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
 
public interface isDone extends Remote {
    boolean executeisDone(String DistribTaskID) throws RemoteException;
}