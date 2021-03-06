import java.util.*;
public class LamportMutex {
	int clock;
	int numAcks;
    int myId;
    Linker link;
    Server server;
	Queue<Timestamp> q; // request queue
    ArrayList<Integer> ackList;
	public LamportMutex(Linker initComm, int pid, Server s) {
        link = initComm;    
        myId = pid;
		clock = 1;
        server = s;
		q = new PriorityQueue<Timestamp>(link.n, 	
				new Comparator<Timestamp>() {
			public int compare(Timestamp a, Timestamp b) {
				return Timestamp.compare(a, b);
			}
		});
		numAcks = 0;
        ackList = new ArrayList<>();
	}
	public synchronized void requestCS() {
		clock++;
		q.add(new Timestamp(clock, myId));
        ackList = new ArrayList<>();
		numAcks = 0;
		link.sendMsgAll("request", clock);
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
		while (((q.peek().pid != myId) || (numAcks < (link.n-1))) && (end - start < 100)) {
            try {
                wait(100);
                end = System.currentTimeMillis();
            } catch(Exception e){
            }
        }
        if (numAcks < link.n -1) { /* a server died */
            link.removeNeighbors(ackList);     
        }
	}
	public synchronized void releaseCS(Inventory inv) {
		q.remove();
		link.sendMsgAll("release", clock, inv);
	}
	public synchronized void handleMsg(Msg m, int src, String tag) {
		int timeStamp = m.getMessageInt();
        clock = Math.max(clock, timeStamp) + 1;
		if (tag.equals("request")) {
			q.add(new Timestamp(timeStamp, src));
			link.sendMsg(src, "ack",clock);
		} else if (tag.equals("release")) {
			Iterator<Timestamp> it =  q.iterator();			    
			while (it.hasNext()){
				if (it.next().getPid() == src) it.remove();
			}
            Inventory inv = (Inventory) m.getMsgBuf().getLast();
            server.setInventory(inv);
            
		} else if (tag.equals("ack")) {
			numAcks++;
            ackList.add(src);
        }
		notifyAll();
	}
}
