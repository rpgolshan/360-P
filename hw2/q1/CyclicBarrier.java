/*
 * Robert Golshan, rpg499
 * Jonathan Friesen, jtf698 
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores
import java.util.concurrent.atomic.AtomicInteger;

public class CyclicBarrier {
    private int parties;
    private AtomicInteger numWaiting = new AtomicInteger(0);
    Semaphore s = new Semaphore(0, true);
	
	public CyclicBarrier(int parties) {
        this.parties = parties;
	}
	
	public int await() throws InterruptedException {
        int index = parties -1 - numWaiting.getAndIncrement();
        if (index == 0) {
            s.release(numWaiting.getAndSet(0) - 1);
        }
        else {
            s.acquire();
        }
        return index;
	}
}
