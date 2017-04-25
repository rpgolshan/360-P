package tests;
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

public class TestMainShutdown {
	static ExecutorService e;
	
	
	public static void main(String[] args) {
		e = new DistributedExecutorService();
		WaitHelloWorld t1 = new WaitHelloWorld();
		WaitHelloWorld t2 = new WaitHelloWorld();
		WaitHelloWorld t3 = new WaitHelloWorld();
		WaitHelloWorld t4 = new WaitHelloWorld();
		DistributedFutureTask f1 = (DistributedFutureTask) e.submit(t1);
		DistributedFutureTask f2 = (DistributedFutureTask) e.submit(t2);
		DistributedFutureTask f3 = (DistributedFutureTask) e.submit(t3);
		DistributedFutureTask f4 = (DistributedFutureTask) e.submit(t4);
		System.out.println("Tasks Have been submitted! i'm going to cshutdown the executor in 3 seconds..");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		e.shutdown();
		System.out.println("Shutdown initiated");
		
		System.out.println("f1: "+f1.get()+" f2: "+f2.get()+" f3: "+f3.get()+" f4: "+f4.get());
		
		System.out.println("submitting one more task...");
		
		WaitHelloWorld t5 = new WaitHelloWorld();
		DistributedFutureTask f5 = (DistributedFutureTask) e.submit(t5);
		
		System.out.println("attempted to submit the task, getting results");
		
		System.out.println(f5.get());
		
		System.out.println("\nDone!");

	}

}


