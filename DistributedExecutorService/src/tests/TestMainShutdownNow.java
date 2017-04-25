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

public class TestMainShutdownNow {
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
		System.out.println("Tasks Have been submitted! i'm going to shutdownNOW(with interrupts) the executor in 3 seconds..");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<Runnable> l = e.shutdownNow();
		System.out.println("ShutdownNOW initiated, currently executing trheads should cancel");
		
		System.out.println("Returned list: " + l.toString());
		
		System.out.println("For fun, lets try to get all the cancelled futures!");
		
		System.out.println("f1: "+f1.get()+" f2: "+f2.get()+" f3: "+f3.get()+" f4: "+f4.get());
		
		
		
		System.out.println("\nDone!");

	}

}


