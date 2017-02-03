//UT-EID=	Rob Golshan: rpg499
//			Jonathan Friesen: jtf698


import java.util.*;
import java.util.concurrent.*;

public class PSort implements Runnable{
	public static ExecutorService threadPool = Executors.newCachedThreadPool();  
	
	int[] A;
	int Begin;
	int End;
	int index; //will be index of actual pivot
	
	public PSort(int[] a, int begin, int end) {
		this.A = a;
		this.Begin = begin;
		this.End = end;
		this.index = partition(a,begin,end-1);
	}
	
	public int partition (int[] a, int begin, int end){
		int i = begin;
		int j = end;
		int x;
		int pivot = a[(begin+end)/2];
		while(i<=j){
			while(a[i]<pivot){
				i++;
			}
			while(a[j]>pivot){
				j--;
			}
			if(i<=j){
				x = a[i];
				a[i] = a[j];
				a[j] = x;
				i++;
				j--;
			}
		}
		return i;
	}

	public static void parallelSort(int[] A, int begin, int end){
		try{
			PSort s = new PSort(A, begin, end);
			Future<?> s1 = threadPool.submit(s);
			s1.get();		
		} catch(Exception e){
			//System.err.println(e);
		}
	}
  
	public void insertSort(int[] A, int begin, int end){
		int length = end - begin;
		if(length <= 4){
			for(int i = begin+1; i<length; i++){
				int j = i;
				while((j>0) && (A[j-1]>A[j])){
					int x = A[j];
					A[j] = A[j-1];
					A[j-1]= x;
				}
			}	
		}
	}

	@Override
	public void run() {
		int length = End - Begin;
		try{
			if(length <= 4){
				insertSort(A,Begin,End);	
			}else{
                Future<?> f1 = null;
				if(Begin<index-1){
                    f1 =  threadPool.submit(new PSort(A,Begin,index-1));
				}
                Future<?> f2 = null;
				if(index<End){
				    f2 =  threadPool.submit(new PSort(A,index,End));
				}
				if(f1 != null)
                    f1.get();
				if(f2 != null)
                    f2.get();
			}
		} catch (Exception e){
			//System.out.println(e);
		}
	}
}
