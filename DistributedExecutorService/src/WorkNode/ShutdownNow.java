import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
 
public interface ShutdownNow extends Remote {
    List<Runnable> executeShutdownNow() throws RemoteException;
}