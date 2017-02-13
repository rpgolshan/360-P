import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Garden {
    
    private static final Lock shovel = new ReentrantLock();
    private static final Lock lock = new ReentrantLock();
    private static final Condition emptyHole = lock.newCondition();  
    private static final Condition plantedHole = lock.newCondition();  
    private static final Condition holeLimit = lock.newCondition();  
    private int holesDug;
    private int holesFilled;
    private int holesSeeded;


    private final int MAX_HOLES = 100;
    private final LinkedList<Boolean> q = new LinkedList<Boolean>();

    // index of next hole to seed
    private int nextSeed;

	public Garden(){
        nextSeed = -1;
	}
    
	public void startDigging() throws InterruptedException{
        lock.lock();

        try {
            // dug more than MAX away from last filled
           while (q.size() >= MAX_HOLES)
               holeLimit.await();
           shovel.lock();
           //create new node, add to tail
           q.add(new Boolean(false));
        }
        catch (InterruptedException e) {
            shovel.unlock();
            lock.unlock();
        }
	}
	public void doneDigging(){
        holesDug++;
        if (nextSeed < 0) {
            nextSeed = q.size() - 1;
        }
        emptyHole.signal();
        shovel.unlock();
        lock.unlock();
        // a hole can now be seeded
	} 
	public void startSeeding() throws InterruptedException{
        // lock last unseeded hole
        lock.lock();
        try {
            // if there are no empty dug holes
            while (nextSeed == -1)
                emptyHole.await();
        }
        catch  (InterruptedException e){
            lock.unlock();
        }
	}
	public void doneSeeding(){
        // move pointer to next node
        q.set(nextSeed, new Boolean(true));
        nextSeed++;
        holesSeeded++;
        if (nextSeed == q.size()) {
            nextSeed =  -1;
        }
        plantedHole.signal();
        lock.unlock();
	} 
	public void startFilling() throws InterruptedException{
        lock.lock();
        try {
            //must be a hole with a seed in it
            while (q.size() == 0 || q.getFirst().booleanValue() == false)
               plantedHole.await(); 
            shovel.lock();
        }
        catch  (InterruptedException e){
            shovel.unlock();
            lock.unlock();
        }
	}
	public void doneFilling(){
        q.removeFirst();
        holesFilled++;
        if (nextSeed >= 0) {
            nextSeed--;
        }
        holeLimit.signal();
        shovel.unlock();
        lock.unlock();
	}
 
    /*
    * The following methods return the total number of holes dug, seeded or 
    * filled by Newton, Benjamin or Mary at the time the methods' are 
    * invoked on the garden class. */
   public int totalHolesDugByNewton() {return holesDug;} 
   public int totalHolesSeededByBenjamin() { return holesSeeded;} 
   public int totalHolesFilledByMary() { return holesFilled; } 

}
