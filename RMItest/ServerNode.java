//a 'Server' that acts as the nodes for the ES
//

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
        
public class ServerNode implements Compute, Get {
    
	Hashtable<String,Future<Object>> futures = new Hashtable();
	
	static ExecutorService pool;
	static UID UniqueID;
	
    public ServerNode() {
    	super();
    	pool = Executors.newCachedThreadPool();	
    	UniqueID = new UID();
    }
	
	@Override
	public void executeCallable(Callable<Object> c, String DistribTaskID) throws RemoteException {
		Future<Object> f = pool.submit(c);
		futures.put(DistribTaskID,f);//
	}
	
	@Override
	public void executeRunnable(Runnable r, String DistribTaskID, Object result) throws RemoteException {
		Future<Object> f = pool.submit(r, result);
		futures.put(DistribTaskID, f);
	}

	
	@Override
	public Object executeGet(String DistribTaskID) throws RemoteException {
		Object result = null;
		Future<Object> f = futures.get(DistribTaskID);
		try {
			result = f.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
    public static void main(String args[]) {
        
        try {
        	//create instance of remote object and export it to RMI runtime
            ServerNode obj = new ServerNode();
            Compute stub = (Compute) UnicastRemoteObject.exportObject(obj, 0);//object/tcp port
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(UniqueID.toString(), stub);
            
            System.err.println("Node ready: " + UniqueID.toString());

        } catch (Exception e) {
            System.err.println("Node exception: " + e.toString());
            e.printStackTrace();
        }
    }


}