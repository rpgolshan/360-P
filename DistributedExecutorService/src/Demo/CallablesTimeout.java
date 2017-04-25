package Demo;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import distributedES.DistributedExecutorService;

public class CallablesTimeout {
	static ExecutorService e;
	
	
	public static void main(String[] args) {
		e = new DistributedExecutorService(8050);
		Callable<String> t = new SleepyHelloWorldCall();
		
		Future<String> f1 = e.submit(t);
		Future<String> f2 = e.submit(t);
		Future<String> f3 = e.submit(t);
		Future<String> f4 = e.submit(t);
		
		System.out.println("Submitted tasks, timeout for the first task is THREE seconds");
		
		System.out.print("First task: ");
		
		try {
			System.out.print(f1.get(3, TimeUnit.SECONDS) + "\n");
		} catch (Exception e){
			
		}
		
		
		try {
			System.out.println("Final Results: " + f1.get() + f2.get() + f3.get()+ f4.get());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		System.out.println("\nDone!");

	}

}


