package Demo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import distributedES.DistributedExecutorService;
import distributedES.DistributedFutureTask;

public class CallablesAwaitTermination {
	static ExecutorService e;
	
	
	public static void main(String[] args) {
		e = new DistributedExecutorService(args[0], Integer.parseInt(args[1]));
		SleepyHelloWorldCall t = new SleepyHelloWorldCall();
		Future<String> f1 = e.submit(t);
		Future<String> f2 = e.submit(t);
		Future<String> f3 = e.submit(t);
		Future<String> f4 = e.submit(t);
		System.out.println("Tasks Have been submitted! i'm going to shutdown and await termination for 4 seconds..");
		boolean b = false;
		e.shutdown();
		while(!b){
		try {
				b = e.awaitTermination(4, TimeUnit.SECONDS);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			System.out.println("4 MORE Seconds have passed... Have we terminated? " +  b);
		}
		
		
		System.out.println("\nDone!");

	}

}


