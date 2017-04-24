import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class HelloWorld implements Runnable, Serializable{

	//private static final long serialVersionUID = 227L;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Integer execute() {
		// TODO Auto-generated method stub
		System.out.println("Hello World!");
		return 5;
	}

	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Hello World!!!");
		//return 4;
		
	}

	
}
