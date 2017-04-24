import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestMainInvokeTimeout {
	static ExecutorService e;
	
	
	public static void main(String[] args) {
		e = new DistributedExecutorService();
		WaitHelloWorld t1 = new WaitHelloWorld();
		WaitHelloWorld t2 = new WaitHelloWorld();
		WaitHelloWorld t3 = new WaitHelloWorld();
		WaitHelloWorld t4 = new WaitHelloWorld();
		ArrayList<WaitHelloWorld> list = new ArrayList();
		list.add(t1);
		list.add(t2);
		list.add(t3);
		list.add(t4);
		System.out.println("tasks created and added to list");
		/*DistributedFutureTask f1 = (DistributedFutureTask) e.submit(t1);
		DistributedFutureTask f2 = (DistributedFutureTask) e.submit(t2);
		DistributedFutureTask f3 = (DistributedFutureTask) e.submit(t3);
		DistributedFutureTask f4 = (DistributedFutureTask) e.submit(t4);*/
		System.out.println("invoking tasks...I'll give it 2 seconds");
		List<Future<String>> recieveList = null;
		try {
			recieveList = e.invokeAll(list, 2, TimeUnit.SECONDS);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		System.out.println("Two seconds should be up! printing results...");
		int i = 1;
		for(Future<String> o : recieveList){
			try {
				System.out.print("f"+i+": "+o.get()+", ");
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			i++;
		}
		System.out.println("\n Done!");

	}

}


