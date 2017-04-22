import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class DistributedFutureTask extends FutureTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean recievedObj;
	private boolean hasInit;
	private boolean isCallable;
	private Object Obj;
	private Object RunReturnObj;
	String host;
	Callable Ctask;
	Runnable Rtask;
	String node;
	String myID;
	
	public DistributedFutureTask(Runnable runnable, Object result) {
		super(runnable, result);
		recievedObj = false;
		hasInit = false;
		isCallable = false;
		RunReturnObj = result;
		Rtask = runnable;
	}
	
	public DistributedFutureTask(Runnable runnable) {
		super(runnable, "done");
		recievedObj = false;
		hasInit = false;
		isCallable = false;
		RunReturnObj = false;
		Rtask = runnable;
	}
	
	
	public DistributedFutureTask(Callable callable) {
		super(callable);
		Ctask = callable;
		recievedObj = false;
		hasInit = false;
		isCallable = true;
	}
	
	public void Initialize(String host, String NodeID, String myID){
		this.host = host;
		this.node = NodeID;
		this.myID = myID;
		System.out.println("My ID is " + NodeID + "And I'm connecting to node " + NodeID);
	}

	public void Execute() {
		try { 		
		Registry registry = LocateRegistry.getRegistry(host);
        Compute stub = (Compute) registry.lookup(node);
        if(isCallable){
        	stub.executeCallable(Ctask, myID);
        }else{
        	stub.executeRunnable(Rtask, myID, RunReturnObj);
        }
        
         System.out.println("task submitted: " + System.currentTimeMillis());
 
         } catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	@Override
	public boolean isDone(){
		if(recievedObj){
			return true;
		}else{
			return false;
		}
	}
	*/

	
	@Override
	public Object get(){
		Object result = null;
		try { 
			
		Registry registry = LocateRegistry.getRegistry(host);
        Get stub = (Get) registry.lookup(node);
        result = stub.executeGet(myID);
        System.out.println("get finished: " + System.currentTimeMillis());
 
         } catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Obj = result;
		recievedObj = true;
		return result;
	}


	



}
