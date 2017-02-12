/*
 * Robert Golshan, rpg499
 * Jonathan Friesen, jtf698 
 */
import java.util.concurrent.atomic.AtomicInteger;

public class MonitorCyclicBarrier {
    private int parties;
    private AtomicInteger numWaiting = new AtomicInteger(0);
	
	public MonitorCyclicBarrier(int parties) {
        this.parties = parties;
	}
	
	public synchronized int await() throws InterruptedException {
        int index = parties -1 - numWaiting.getAndIncrement();
        if (index == 0) {
            notifyAll();
            numWaiting.set(0);
        } else {
            wait();
        }

        return index;
	}
}

