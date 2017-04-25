package Demo;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import distributedES.DistributedExecutorService;

public class RunablesRNGShutdown {
	static ExecutorService e;
	
	public static void main(String[] args) {
		//assuming args are hostname,port number, and number of callables
				String hostname = args[0];
				Integer port = Integer.parseInt(args[1]);
				Integer numcalls = Integer.parseInt(args[2]);
				
				ExecutorService es = new DistributedExecutorService(hostname,port);
				
				for(int i=0; i<numcalls; i++){
					Runnable r = new RandomNumberRun();
					es.submit(r);
				}
				
				ArrayList<Runnable> returnList = new ArrayList<Runnable>();
				
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("SHUTTING DOWN");

				returnList = (ArrayList<Runnable>) es.shutdownNow();
				
				
				
				System.out.println("We missed " + returnList.size() +" random numbers");
				
				
		
		System.out.println("\nDone!");

	}
	

}


