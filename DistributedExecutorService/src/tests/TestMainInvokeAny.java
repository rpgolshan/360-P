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

public class TestMainInvokeAny {
	static ExecutorService e;
	
	
	public static void main(String[] args) {
		e = new DistributedExecutorService();
		Callable<String> t1 = new WaitHelloWorld();
		Callable<String> t2 = new WaitHelloWorld();
		Callable<String> t3 = new WaitHelloWorld();
		Callable<String> t4 = new WaitHelloWorld();
		Callable<String> t5 = new WaitHelloWorld2();
		Callable<String> t6 = new WaitHelloWorld();
		ArrayList<Callable<String>> tasks = new ArrayList<Callable<String>>(); 
		tasks.add(t1);
		tasks.add(t2);
		tasks.add(t3);
		tasks.add(t4);
		tasks.add(t5);
		//tasks.add(t6);
		System.out.println("Tasks Have been Created, next going to invoke ANY");
		String rec = null;
		try {
			rec = e.invokeAny(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("We got " + rec + " from the invokeANY");
		
		
		
		
		System.out.println("\nDone!");

	}

}


