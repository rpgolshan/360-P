package worknode;
//a 'Server' that acts as the nodes for the ES
//

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import distributedES.DistributedFutureTask;
import distributedES.RemoteMethods;
        
public class WorkNode implements RemoteMethods {
    
	Hashtable<String,Future<?>> futures = new Hashtable<String, Future<?>>();
	Lock ShutdownLock = new ReentrantLock();
	static ExecutorService pool;
	static UID UniqueID;
	static Integer numTasks = 0;
	
	
    public WorkNode() {
    	super();
    	pool = Executors.newSingleThreadExecutor();
    	Thread t = new Thread(new TaskTeller());
    	t.start();
    	UniqueID = new UID();
    	
    }
	
	@Override
	public <T> void executeCallable(Callable<T> c, String DistribTaskID) throws RemoteException {
		ShutdownLock.lock();
		numTasks++;
		Thread dec = new Thread(new Decrementer());
		Future<T> f = pool.submit(c);
		pool.submit(dec);
		futures.put(DistribTaskID,f);//
		ShutdownLock.unlock();
	}
	
	@Override
	public void executeRunnable(Runnable r, String DistribTaskID, Object result) throws RemoteException {
		ShutdownLock.lock();
		numTasks++;
		Thread dec = new Thread(new Decrementer());
		Future<Object> f = pool.submit(r, result);
		pool.submit(dec);
		futures.put(DistribTaskID, f);
		ShutdownLock.unlock();
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
	public <T> T executeInvokeAny(Collection<? extends Callable<T>> list) throws RemoteException {
		T result = null;
		try {
			numTasks++;
			result = pool.invokeAny(list);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			Thread dec = new Thread(new Decrementer());
			pool.submit(dec);
		}
		return result;
	}
	
	@Override
	public boolean executeisDone(String DistribTaskID) throws RemoteException {
		Future<Object> f = (Future<Object>) futures.get(DistribTaskID);
		return f.isDone();
	}


	@Override
	public int executeGetNode() throws RemoteException {
		return numTasks;
	}
	
	Lock numTaskLock = new ReentrantLock();
	
	public class Decrementer implements Runnable{

		@Override
		public void run() {
			numTaskLock.lock();
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			numTasks--;
			numTaskLock.unlock();
		}
		
	}
	
	@Override
	public void executeShutdown(ArrayList<String> dFT) throws RemoteException {
		for(String dft : dFT){
			Future<?> f = futures.get(dft);
			//f.cancel(false);
		}
	}


	
	@Override
	public ArrayList<String> executeShutdownNow(ArrayList<String> dFT) throws RemoteException {
		ShutdownLock.lock();
		
		ArrayList<String> notFin = new ArrayList<String>();
		for(String dft : dFT){
			
			Future<?> f = futures.get(dft);
	
			if(f.cancel(true)){
				notFin.add(dft);
			}
		}
		ShutdownLock.unlock();
		return notFin;
	}

	@Override
	public boolean executeIsTerminated(ArrayList<String> dFT) throws RemoteException {
		for(String dft : dFT){
			Future<?> f = futures.get(dft);
			if(!f.isDone()){
				return false;
			}
		}
		return true;
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
	
	public class TaskTeller implements Runnable{

		@Override
		public void run() {
			
			while(true){
				System.out.println("~I have " + numTasks + " Tasks~");
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}
		}
		
	}



}
