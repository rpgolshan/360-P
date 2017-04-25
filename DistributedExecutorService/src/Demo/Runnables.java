package Demo;

import java.util.concurrent.ExecutorService;
import distributedES.DistributedExecutorService;

public class Runnables {
	
	public static void main(String[] args){
		//assuming args are hostname,port number, and number of runnables
		String hostname = args[0];
		Integer port = Integer.parseInt(args[1]);
		Integer numRunnables = Integer.parseInt(args[2]);
		
		ExecutorService es = new DistributedExecutorService(hostname,port);
		
		for(int i=0; i<numRunnables; i++){
			Runnable r = new SleepyHelloWorld();
			es.submit(r);
		}
		
		System.out.println("Finished!");
		
		es.shutdown();
		
	}
	
}
