package Demo;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import distributedES.DistributedExecutorService;

public class Cancels {
	static ExecutorService e;
	
	
	public static void main(String[] args) {
		e = new DistributedExecutorService(8050);
		Callable<String> t1 = new SleepyHelloWorldCall();

		Future<String> f1 = e.submit(t1);
		
		f1.cancel(true);
		
		System.out.println("On return we got ");
		
		try {
			 System.out.println(f1.get());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		} catch (CancellationException e1){
			
			System.out.println("Future Cancelled");
			
		}
		
		
		
		System.out.println("\nDone!");

	}

}


