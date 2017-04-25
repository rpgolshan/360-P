package Demo;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import distributedES.DistributedExecutorService;

public class CallablesRNGInvokeAll {
	static ExecutorService e;
	
	public static void main(String[] args) {
		//assuming args are hostname,port number, and number of callables
				String hostname = args[0];
				Integer port = Integer.parseInt(args[1]);
				Integer numcalls = Integer.parseInt(args[2]);
				
				ExecutorService es = new DistributedExecutorService(hostname,port);
				
				ArrayList<Future<Integer>> futures = new ArrayList<Future<Integer>>();
				
				ArrayList<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
				
				for(int i=0; i<numcalls; i++){
					Callable<Integer> c = new RandomNumber();
					callables.add(c);
				}
				
				System.out.println("Results: ");
				
				try {
					ArrayList<Future<Integer>> l= (ArrayList<Future<Integer>>) es.invokeAll(callables);
					for(Future<Integer> f : l){
						System.out.println(f.get());
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				

				es.shutdown();
		
		System.out.println("\nDone!");

	}
	

}


