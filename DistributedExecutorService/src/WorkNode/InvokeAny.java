import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
 
public interface InvokeAny extends Remote {
    <T> T executeInvokeAny(Collection<? extends Callable<T>> list) throws RemoteException;
}