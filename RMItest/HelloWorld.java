import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class HelloWorld implements Callable, Serializable{

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

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		int x = 0;
		TimeUnit.SECONDS.sleep(8);
		
		System.out.println("Callable has been called: " + System.currentTimeMillis());
		return "Jon";
	}

}
