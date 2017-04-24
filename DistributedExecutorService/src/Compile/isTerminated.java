import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
 
public interface isTerminated extends Remote {
    boolean executeIsTerminated() throws RemoteException;
}