import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestMain {
	static ExecutorService e;
	
	
	public static void main(String[] args) {
		e = new DistributedExecutorService(8050);
		Callable t1 = new WaitHelloWorld();
		Callable t2 = new WaitHelloWorld();
		Callable t3 = new WaitHelloWorld();
		
		Future f1 = e.submit(t1);
		Future f2 = e.submit(t2);
		Future f3 = e.submit(t3);
		
		try {
			System.out.println("On return we got " + f1.get() + f2.get() + f3.get());
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


