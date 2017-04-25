package Demo;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import distributedES.DistributedExecutorService;

public class CallablesHelloWorldInvokeAny {
	static ExecutorService e;
	
	public static void main(String[] args) {
		//assuming args are hostname,port number, and number of callables
				String hostname = args[0];
				Integer port = Integer.parseInt(args[1]);
				Integer numcalls = Integer.parseInt(args[2]);
				
				ExecutorService es = new DistributedExecutorService(hostname,port);
				
				
				ArrayList<Callable<String>> callables = new ArrayList<Callable<String>>();
				
				for(int i=0; i<numcalls; i++){
					Callable<String> c = new SleepyHelloWorldCall();
					callables.add(c);
				}
				
				callables.add(new SleepyHelloWorldCall2());
				
				String result = "";
				
				try {
					result = (String) es.invokeAny(callables);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("Result: " + result);
				
				
				es.shutdown();
		
		System.out.println("\nDone!");

	}
	

}


