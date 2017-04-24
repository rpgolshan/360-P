//a 'Server' that acts as the nodes for the ES
//

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
        
public class WorkNode implements RemoteMethods {
    
	Hashtable<String,Future<?>> futures = new Hashtable();
	
	static ExecutorService pool;
	static UID UniqueID;
	
    public WorkNode() {
    	super();
    	pool = Executors.newCachedThreadPool();	
    	UniqueID = new UID();
    }
	
	@Override
	public <T> void executeCallable(Callable<T> c, String DistribTaskID) throws RemoteException {
		Future<T> f = pool.submit(c);
		futures.put(DistribTaskID,f);//
	}
	
	@Override
	public void executeRunnable(Runnable r, String DistribTaskID, Object result) throws RemoteException {
		Future<Object> f = pool.submit(r, result);
		futures.put(DistribTaskID, f);
	}

	
	@Override
	public <T> T executeGet(String DistribTaskID) throws RemoteException {
		T result = null;
		Future<T> f = (Future<T>) futures.get(DistribTaskID);
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
	
	@Override
	public boolean executeCancel(String DistribTaskID, boolean interruptable) throws RemoteException {
			Future<Object> f = (Future<Object>) futures.get(DistribTaskID);
			return f.cancel(interruptable);
	}
		
	@Override
	public void executeShutdown() throws RemoteException {
		pool.shutdown();
	}
	
	@Override
	public List<Runnable> executeShutdownNow() throws RemoteException {
		return pool.shutdownNow();
	}
	
	@Override
	public boolean executeIsTerminated() throws RemoteException {
		return pool.isTerminated();
	}

	@Override
	public <T> T executeInvokeAny(Collection<? extends Callable<T>> list) throws RemoteException {
		T result = null;
		try {
			result = pool.invokeAny(list);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public boolean executeisDone(String DistribTaskID) throws RemoteException {
		Future<Object> f = (Future<Object>) futures.get(DistribTaskID);
		return f.isDone();
	}

	
    public static void main(String args[]) {
        
        try {
        	Registry registry = null;
        	String hostname = null;
        	Integer port = null;
        	if(args.length==2){
        		if(args.length>2){
        			System.err.println("Only first two parameters (hostname, port) will be used");
        		}
        		hostname = args[0];
        		port = Integer.parseInt(args[1]);
        		registry = LocateRegistry.getRegistry(hostname,port);
        	}
        	if(args.length == 1){
        		hostname = args[0];
        		registry = LocateRegistry.getRegistry(hostname);
        	}else if(args.length == 0){
        		registry = LocateRegistry.getRegistry();
        	}
            WorkNode obj = new WorkNode();
            RemoteMethods stub = (RemoteMethods) UnicastRemoteObject.exportObject(obj, 0);//object/tcp port          
            registry.bind(UniqueID.toString(), stub);
        
            System.err.println("Node ready: " + UniqueID.toString());

        } catch (Exception e) {
            System.err.println("Node exception: " + e.toString());
            e.printStackTrace();
        }
    }


}
