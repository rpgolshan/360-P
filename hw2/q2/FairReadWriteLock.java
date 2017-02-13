import java.util.PriorityQueue;

public class FairReadWriteLock {
    private int numWriters = 0;
    private int numReaders = 0;

    PriorityQueue<Thread> threads = new PriorityQueue<Thread>();
    public FairReadWriteLock() {
    
    }
                        
	public synchronized void beginRead() throws InterruptedException  {
        threads.add(Thread.currentThread());

        while (threads.peek() != Thread.currentThread()) {
            wait();
        }
        while (numWriters > 0) {
            wait();
        }
        numReaders++;
        threads.poll();
	}
	
	public synchronized void endRead() {
        numReaders--;
        notifyAll();
	}
	
	public synchronized void beginWrite()  throws InterruptedException{
        threads.add(Thread.currentThread());
        while (threads.peek() != Thread.currentThread()) {
            wait();
        }
        
        while (numReaders > 0 || numWriters > 0) {
            wait(); 
        }
        numWriters++;
        threads.poll();
	}
	public synchronized void endWrite() {
        numWriters--;
        notifyAll();
	}
}
	
