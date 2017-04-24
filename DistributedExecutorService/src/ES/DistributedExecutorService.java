/* DISTRIBUTED EXECUTOR SERVICE
 * 
 * 
 */


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DistributedExecutorService implements ExecutorService {

	PriorityQueue<Node> NodeQ;
	boolean shutdown;
	boolean terminated;
	Registry registry;
	String host;
	int port;
	//TODO: change to have a custom registry name for host
	//TODO: make the node work based on both how much work it was given how how much it completed?
    
	DistributedExecutorService() {
		this.host = null;
		this.port = 0;
		try {
			registry = LocateRegistry.getRegistry();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    	Restart();
    }
    
    DistributedExecutorService(String host) {
    	this.host = host;
    	this.port = 0;
    	try {
			registry = LocateRegistry.getRegistry(host);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    	Restart();
    }
    
    DistributedExecutorService(int port) {
    	this.host = null;
    	this.port = port;
    	try {
			registry = LocateRegistry.getRegistry(port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    	Restart();
    }
    
    DistributedExecutorService(String host, int port) {
    	this.host = host;
    	this.port = port;
    	try {
			registry = LocateRegistry.getRegistry(host,port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    	Restart();
    }
    
    //reallocates the registry and the NodeQ
    public boolean Restart(){
    	boolean result = false;
    	NodeQ = new PriorityQueue<Node>();
		try {
	    	String[] Nodes = registry.list();
	    	for (String node : Nodes){
	    		NodeQ.add(new Node(node));
	    	}
	    	result = true;
    	} catch (RemoteException e) {
    		 System.err.println("Executor Service Exception: " + e.toString() + " Please Restart Service");
             e.printStackTrace();
		} finally{
			shutdown = false;
			terminated = false;
		}
		return result;
    }

	@Override
	public void execute(Runnable arg0) {
		// TODO Auto-generated method stub
		arg0.run();
	}

	//Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, or the current thread is interrupted, whichever happens first.
	/*
	if shutdown==false, wait for it to be shutdown, then go to next step
	if shutdown == true, just repeatedly run isterminated until it is true, or timeout has gone by
	timeout applies to both steps
	*/
	@Override
	public boolean awaitTermination(long time, TimeUnit tunit) throws InterruptedException {	
		boolean result = false;
		if(!shutdown){
			return result;
		}
		if(terminated){
			return true;
		}
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Callable<String> f = new Callable<String>(){
			public String call(){
				boolean b = false;
				while(!b){
					b = isTerminated();
				};	
				return "complete";
			}
		};
		Future<String> resultFuture = executor.submit(f);
		String Sresult = "incomplete";
		try{
			Sresult = resultFuture.get(time, tunit);
		} catch(TimeoutException e){
			result = false;
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally{
			if(Sresult.equals("complete")){
				result = true;
			}
		}
		return result;
	}

	//Executes the given tasks, returning a list of Futures holding their status and results when all complete.
	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		if(shutdown) return null;
		List<Future<T>> list = new ArrayList<Future<T>>();
		for(Callable<T> c : tasks){
			list.add(this.submit(c));
		}
		for(Future<T> f : list){
			try {
				f.get();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	//Executes the given tasks, returning a list of Futures holding their status and results when all complete or the timeout expires, whichever happens first.
	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long time, TimeUnit tunit)
			throws InterruptedException {
		if(shutdown) return null;
		ExecutorService executor = Executors.newSingleThreadExecutor();
		final List<Future<T>> list = new ArrayList<Future<T>>();
		for(Callable<T> c : tasks){
			list.add(this.submit(c));
		}
		Callable<List<Future<T>>> listCall = new Callable<List<Future<T>>>(){
			public List<Future<T>> call(){
				for(Future<T> f : list){
					try {
						f.get();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return list;
			}
		};
		Future<List<Future<T>>> listFuture = executor.submit(listCall);
		try{
			listFuture.get(time, tunit);
		} catch(TimeoutException e){
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 
		return list;
	}
	
	
	//Executes the given tasks, returning the result of one that has completed successfully (i.e., without throwing an exception), if any do
	/*
	 invokeany on a list of callables that each call invokeany on each node
	 separate into smaller lists of tasks
	 */
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		if(shutdown) return null;	
		ArrayList<Callable<T>> originalList = new ArrayList<Callable<T>>(tasks);
		//System.out.println("MaxLevel Invoke list: " + originalList.toString());
		//System.out.println("NodeQ: " + NodeQ.toString());
		double d = (double) originalList.size()/ (double) NodeQ.size();
		int partitionSize = (int) Math.ceil(d);
		List<List<Callable<T>>> partitions = new LinkedList<List<Callable<T>>>();
		for (int i = 0; i < originalList.size(); i += partitionSize) {
		    partitions.add(originalList.subList(i,
		            Math.min(i + partitionSize, originalList.size())));
		}
		ExecutorService executor = Executors.newCachedThreadPool();//start up executor service
		ArrayList<Callable<T>> nodeCallList = new ArrayList<Callable<T>>();//create callables that calls invokeAny remotely on each node
		int index = 0;
		for(Node node : NodeQ){
			nodeCallList.add(new partitionInvoker<T>(partitions.get(index), node));
			index++;
		}
		return executor.invokeAny(nodeCallList);//call invokeany on the nodeCallList
	}
	
	public 	class partitionInvoker<T> implements Callable<T>{
		Node node;
		List<Callable<T>> partition;
		partitionInvoker(List<Callable<T>> l, Node n){
			node = n;
			partition = l;
		}
		@Override
		public T call() throws Exception {
			T result = null;
			ArrayList<Callable<T>> list = new ArrayList<Callable<T>>(partition);
			//System.out.println("INVOKING " + list.toString() + " ON " + node.UID);
			InvokeAny stub = (InvokeAny) registry.lookup(node.UID);
			result = stub.executeInvokeAny(list);
			return result;
		}
	}

	//Executes the given tasks, returning the result of one that has completed successfully (i.e., without throwing an exception), if any do before the given timeout elapses.
	/*
	same thing as invoke all but at the end wait for one to complete
	*/
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long time, TimeUnit tunit)
			throws InterruptedException, ExecutionException, TimeoutException {
		if(shutdown) return null;
		ExecutorService executor = Executors.newSingleThreadExecutor();
		T result = null;
		Callable<T> listCall = new Callable<T>(){
			public T call(){
				T t = null;
				try {
					t = invokeAny(tasks);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				return t;
			}
		};
		Future<T> listFuture = executor.submit(listCall);
		try{
			result = listFuture.get(time, tunit);
		} catch(TimeoutException e){
			//RAN OUT OF TIME
			//System.out.println("out of time");
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 
		return result;
	}

	//Returns true if this executor has been shut down.
	@Override
	public boolean isShutdown() {
		return shutdown;
	}

	//Returns true if all tasks have completed following shut down.
	/*
	call a new method isTerminated for each node in Q
	this (isTerminated) method sees if the executor service on each node is done
	if any of these return false, return false. else return true and set terminated=true
	 */
	@Override
	public boolean isTerminated() {
		if(terminated){
			return true;
		}
		for (Node node : NodeQ){
			try{
				isTerminated stub = (isTerminated) registry.lookup(node.UID);
				boolean result = stub.executeIsTerminated();
				if(result==false){
					return false;
				}
			}catch(RemoteException e1){
				//Node is gone
			} catch (NotBoundException e2) {
				//Node might be gone
			}
    	}
		terminated = true;
		return true;
	}

	//PASSEDCHECK
	//Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.
	@Override
	public void shutdown() {
		shutdown = true;
		//spawn a thread that sends a shutdown message to the ES on each machine in nodeQ
		Thread ShutdownThread = new Thread(new Runnable(){
			@Override
			public void run(){
				for (Node node : NodeQ){
					try{
						Shutdown stub = (Shutdown) registry.lookup(node.UID);
						stub.executeShutdown();
					}catch(RemoteException e1){
						//Node is gone
					} catch (NotBoundException e2) {
						//Node might be gone
					}
		    	}
			}
		});
		ShutdownThread.start();
	}

	//NOTFULLYTESTED: if it returns correct list
	//Attempts to stop all actively executing tasks, halts the processing of waiting tasks, and returns a list of the tasks that were awaiting execution.
	/*
	send a shutdownNow to each machine in NodeQ
	shutdownNow on the node side will return a list of runnables back to this side
	return the Union of all the List<Runnable>s from each node
	 */
	@Override
	public List<Runnable> shutdownNow() {
		shutdown = true;
		ArrayList<Runnable> resultList = new ArrayList<Runnable>();
		for (Node node : NodeQ){
			try{
				//inList = null;
				ShutdownNow stub = (ShutdownNow) registry.lookup(node.UID);
				List<Runnable> inList = stub.executeShutdownNow();
				resultList.addAll(inList);
			}catch(RemoteException e1){
				//Node is gone
			} catch (NotBoundException e2) {
				//Node might be gone
			}
    	}
		return resultList;
	}

	//PASSEDCHECK
	//Submits a value-returning task for execution and returns a Future representing the pending results of the task.
	@Override
	public <T> Future<T> submit(Callable<T> task) {
		if(shutdown) return null;
		UID UniqueID = new UID();
    	String DistribTaskID = UniqueID.toString();
    	Node n = NodeQ.poll();//get the top of the queue of nodes = the node with the fewest tasks
    	DistributedFutureTask<T> f = new DistributedFutureTask<T>(task);//create a new DFTask
    	f.Initialize(null, n.UID, DistribTaskID,registry);//initialize the task with the node it is connected to
    	n.Queue.add(f);//add the DFTask to that node's queue
    	NodeQ.add(n);//re-add the node with the new DFTask back into the queue of nodes
        f.Execute();//run the DFTask  
        return (Future<T>) f;
	}

	//Submits a Runnable task for execution and returns a Future representing that task.
	@Override
	public Future<?> submit(Runnable task) {
		if(shutdown) return null;
		UID UniqueID = new UID();
    	String DistribTaskID = UniqueID.toString();
    	Node n = NodeQ.poll();//get the top of the queue of nodes = the node with the fewest tasks
    	DistributedFutureTask f = new DistributedFutureTask(task);//create a new DFTask
    	f.Initialize(null, n.UID, DistribTaskID,registry);//initialize the task with the node it is connected to
    	n.Queue.add(f);//add the DFTask to that node's queue
    	NodeQ.add(n);//re-add the node with the new DFTask back into the queue of nodes
        f.Execute();//run the DFTask  
        return f;
	}

	//Submits a Runnable task for execution and returns a Future representing that task.
	@Override
	public <T> Future<T> submit(Runnable task, T retValue) {
		if(shutdown) return null;
		UID UniqueID = new UID();
    	String DistribTaskID = UniqueID.toString();
    	Node n = NodeQ.poll();//get the top of the queue of nodes = the node with the fewest tasks
    	DistributedFutureTask<T> f = new DistributedFutureTask<T>(task, retValue);//create a new DFTask
    	f.Initialize(null, n.UID, DistribTaskID,registry);//initialize the task with the node it is conencted to
    	n.Queue.add(f);//add the DFTask to that node's queue
    	NodeQ.add(n);//re-add the node with the new DFTask back into the queue of nodes
        f.Execute();//run the DFTask  
        return (Future<T>) f;
	}
	
}