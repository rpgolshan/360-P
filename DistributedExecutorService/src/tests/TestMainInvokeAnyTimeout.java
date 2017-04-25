package tests;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import distributedES.DistributedExecutorService;

public class TestMainInvokeAnyTimeout {
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
		System.out.println("Tasks Have been Created, next going to invoke ANY, but i'll give it 9 seconds");
		String rec = null;
		try {
			rec = e.invokeAny(tasks, 9, TimeUnit.SECONDS);
			System.out.println("We got " + rec + " from the invokeANY");
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			System.out.println("didn't get it!");
		}
		
		
		
		
		
		System.out.println("\nDone!");

	}

}


