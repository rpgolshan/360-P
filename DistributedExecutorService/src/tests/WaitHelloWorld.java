import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class WaitHelloWorld implements Callable<String>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String call() {
		System.out.println("Feeling Sleepy..");
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("I've been interrupted! :(");
			return null;
		}
		System.out.println(".. Hello World!");
		return("I'm Done!");
	}
	
}