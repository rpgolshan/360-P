import java.util.concurrent.Callable;

public class Task {
	private Runnable runnable;
	private Object returnObj;
	private Callable<Object> callable;
	public boolean isCallable;
	Task(Runnable r){
		runnable = r;
		returnObj = null;
		isCallable = false;
	}
	Task(Runnable r, Object o){
		returnObj = o;
		isCallable = false;
	}
	Task(Callable<Object> c){
		callable = c;
		isCallable = true;
	}
	Runnable getRunnable(){
		return runnable;
	}
	Callable<Object> getCallable(){
		return callable;
	}
	Object getReturnValue(){
		return returnObj;
	}
}
