//UT-EID=


import java.util.*;
import java.util.concurrent.*;


public class PSearch implements Callable{
  int start;
  int end;
  int[] array;
  int target;
  public PSearch(int[] A,int s, int e, int k){
	  this.array = A;
	  this.start = s;
	  this.end = e;
	  this.target = k;
  }
	
  public static int parallelSearch(int k, int[] A, int numThreads){
	ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
	List<Future<Integer>> futures = new ArrayList<Future<Integer>>(); 
	int div = (A.length/numThreads) + ((A.length % numThreads == 0)?0:1);
	int index = 0;
    for(int i = 0;i<numThreads;i++){
    	
    	futures.add(threadPool.submit(new PSearch(A,index,Math.min(index+div,A.length),k)));
    	index += div;
    }
    for( Future<Integer> x : futures){
    	try {
    		int y = x.get();
			if(y!=-1){
				return y;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    return -1; // if not found
  }

  public int search(){
	  for(int i = start; i<end;i++){
		  if(array[i]==target){
			  return i;
		  }
	  } 
	  return -1;
  }
  
	@Override
	public Integer call() throws Exception {
		return search();
	}
}
