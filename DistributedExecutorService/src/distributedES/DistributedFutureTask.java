package distributedES;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DistributedFutureTask<T> implements Serializable, Future<Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean isCallable;
	private boolean cancelled;
	private Object RunReturnObj;
	private boolean isDone;
	String host;
	Callable<?> Ctask;
	Runnable Rtask;
	String node;
	String myID;
	Registry registry;
	
	public DistributedFutureTask(Runnable runnable, Object result) {
		isCallable = false;
		RunReturnObj = result;
		Rtask = runnable;
	}
	
	public DistributedFutureTask(Runnable runnable) {
		isCallable = false;
		RunReturnObj = null;
		Rtask = runnable;
	}
	
	
	public <T> DistributedFutureTask(Callable<T> callable) {
		Ctask = callable;
		isCallable = true;
	}
	
	public void Initialize(String host, String NodeID, String myID, Registry reg){
		this.host = host;
		this.node = NodeID;
		this.myID = myID;
		this.registry = reg;
		cancelled = false;
		isDone = false;
		//System.out.println("My ID is " + NodeID + "And I'm connecting to node " + NodeID);
	}

	public void Execute() {
		try { 		
			RemoteMethods stub = (RemoteMethods) registry.lookup(node);
			if(isCallable){
				stub.executeCallable(Ctask, myID);
			}else{
				stub.executeRunnable(Rtask, myID, RunReturnObj);
			}
         } catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object get(){
		Object result = null;
		try { 
			RemoteMethods stub = (RemoteMethods) registry.lookup(node);
			result = stub.executeGet(myID);
			isDone = true;
         } catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean cancel(boolean interruptable) {
		boolean result = true;
		try{
			RemoteMethods stub = (RemoteMethods) registry.lookup(node);
	        result = stub.executeCancel(myID, interruptable);
	        cancelled = true;
	        isDone = true;
		}catch(RemoteException e1){
			
		}catch(NotBoundException e2){

		}
		return result;
	}

	@Override
	public Object get(long time, TimeUnit tunit) throws InterruptedException, ExecutionException, TimeoutException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Object result = null;
		Callable<Object> listCall = new Callable<Object>(){
			public Object call(){
				Object o = null;
				o = get();
				return o;
			}
		};
		Future<Object> f = executor.submit(listCall);
		try{
			result = f.get(time, tunit);
			isDone = true;
		} catch(TimeoutException e){
			//RAN OUT OF TIME
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 
		return result;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isDone() {
		if(isDone){
			return true;
		}
		boolean result = false;
		try{
			RemoteMethods stub = (RemoteMethods) registry.lookup(node);
			result = stub.executeisDone(myID);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return result;
	}

}
