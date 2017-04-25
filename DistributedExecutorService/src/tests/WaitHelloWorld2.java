package tests;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class WaitHelloWorld2 implements Serializable, Callable<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String call() {
		System.out.println("Feeling a little Sleepy..");
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("I've been interrupted! :(");
			return null;
		}
		System.out.println(".. Hello World!");
		return("I'm Done! That was Quick!");
	}
	
}