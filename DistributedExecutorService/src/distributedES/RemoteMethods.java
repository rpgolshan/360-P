package distributedES;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
 
public interface RemoteMethods extends Remote {

	boolean executeCancel(String DistribTaskID, boolean interruptable) throws RemoteException;

    <T> void executeCallable(Callable<T> c, String DistribTaskID) throws RemoteException;

	void executeRunnable(Runnable rtask, String myID, Object result) throws RemoteException;

	<T> T executeGet(String DistribTaskID) throws RemoteException;

	<T> T executeInvokeAny(Collection<? extends Callable<T>> list) throws RemoteException;

	 boolean executeisDone(String DistribTaskID) throws RemoteException;
	 
	 boolean executeIsTerminated() throws RemoteException;
	 
	 void executeShutdown() throws RemoteException;
	 
	 List<Runnable> executeShutdownNow() throws RemoteException;
	 
	 int executeGetNode() throws RemoteException;
}