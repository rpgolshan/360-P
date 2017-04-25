package Demo;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import distributedES.DistributedExecutorService;

public class Callables {
	static ExecutorService e;
	
	
	public static void main(String[] args) {
		e = new DistributedExecutorService(8050);
		Callable<String> t = new SleepyHelloWorldCall();
		
		Future<String> f1 = e.submit(t);
		Future<String> f2 = e.submit(t);
		Future<String> f3 = e.submit(t);
		Future<String> f4 = e.submit(t);
		Future<String> f5 = e.submit(t);
		Future<String> f6 = e.submit(t);
		
		try {
			System.out.println("On return we got " + f1.get() + f2.get() + f3.get()+ f4.get() + f5.get() + f6.get());
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


