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

public class TestMainCancel {
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
		System.out.println("Tasks Have been submitted! i'm going to cancel number 3...");

		boolean b = f3.cancel(true);
		
		System.out.println("The result of the cancel was " + b + "\nTime to get results..when we get f3, we should throw a cancelexception");
		
		System.out.println("f1: "+f1.get()+" f2: "+f2.get()+" f3: "+f3.get()+" f4: "+f4.get());
		
		System.out.println("\nDone!");

	}

}


