import java.util.*;
public class LamportMutex {
	int clock;
	int numAcks;
    int myId;
    Linker link;
    Server server;
	Queue<Timestamp> q; // request queue
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
	}
	public synchronized void requestCS() {
		clock++;
		q.add(new Timestamp(clock, myId));
		link.sendMsgAll("request", clock);
		numAcks = 0;
		while ((q.peek().pid != myId) || (numAcks < (link.n-1))) {
            try {
                wait();
            } catch(Exception e){
            }
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
            server.setInventory((Inventory) m.getMsgBuf().getLast());
		} else if (tag.equals("ack")) {
			numAcks++;
        }
		notifyAll();
	}
}
