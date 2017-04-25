/**
 * Created by Robert Golshan
 * Example of Parallel Prefix Sum using a NAIVE algorithm
 * Demonstrates using ExecutorService that work on the same array
 * */

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelPrefixSum implements Runnable {
    public int stage;
    public int section;
    public static int array[];
    public static int result[];
    public ExecutorService threadPool;

    public ParallelPrefixSum() {
        stage = 0;
        section = 0;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public ParallelPrefixSum(int stage, int section) {
        this.stage = stage;
        this.section = section;
    }

    public static void setArray(int arr[]) {
        ParallelPrefixSum.array = arr; 
    }

    @Override
    public void run() {
        if (stage == 0) { //the master. Only runs once
            int stages = (int)(Math.log(array.length)/Math.log(2));
            for (int s = 1; s <= stages; s++) {
                result = new int[array.length];
                List<Future<?>> list = new LinkedList<Future<?>>();
                for (int i = 0; i < array.length; i++) {
                    ParallelPrefixSum ps = new ParallelPrefixSum(s, i); 
                    list.add(threadPool.submit(ps));
                } 
                for (Future<?> f: list) {
                    try {
                        f.get(); 
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
                array = result;
            }
        } 
        else {
            if (section >= ((int)Math.pow(2, stage-1))) {
                result[section] = array[section - (int)Math.pow(2,stage-1)] + array[section];
            } 
            else {
                result[section] = array[section];
            }
        }
    }

    public static void main(String[] args) {
        final int ARRAY_SIZE = 16; // Must be a power of 2
        final int ARRAY_MAX_VALUE = 25;
        int array[] = new int[ARRAY_SIZE];
        int seqRes = 0;
        for (int i = 0; i < ARRAY_SIZE; i++) { //filling the array with values up to ARRAY_MAX_VALUE
            array[i] = (int) Math.ceil(Math.random()*ARRAY_MAX_VALUE); 
            seqRes += array[i];
        }


        System.out.println("Expected Output: " + seqRes); 

        ParallelPrefixSum.setArray(array);

        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<?> f = es.submit(new ParallelPrefixSum());
        try {
            f.get();
            System.out.println("Parallel Output: " + ParallelPrefixSum.result[ARRAY_SIZE - 1]);
        } catch(Exception e) {
            System.exit(1);
        }

        System.exit(0);
    }
}
