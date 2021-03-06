package Demo;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class RandomNumber implements Callable<Integer>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Integer call() {
		System.out.println("Your Random Number Is...");
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			System.out.println("I've been interrupted! :(");
		}
		
		Integer RNG = new Random().nextInt();
		
		System.out.println(RNG+"!!!");
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			System.out.println("I've been interrupted! :(");
		}
		
		return RNG;
	}		

}

