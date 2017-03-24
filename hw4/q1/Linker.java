import java.util.*; import java.io.*;
import java.net.ServerSocket;
public class Linker {
    private boolean debug = false;
	public int myId;	
	public volatile int n; // number of neighbors including myself
    public LamportMutex lMutex;
	Connector connector = null;
	public List<NameEntry> neighbors;
	public Linker (int pid, ServerSocket s ,List<NameEntry> ne) throws Exception { 
        myId = pid;
		connector = new Connector(myId, s);
        neighbors = ne;
        n = neighbors.size();
		connector.Connect(neighbors);
	}

    public void init(LamportMutex lm){
        lMutex = lm;
		for (NameEntry name : neighbors) {
            if (name.getPid() != myId)
			(new ListenerThread(name.getPid(), this, lMutex)).start();		    	
        }
	}

	public void sendMsg(int pid, Object ... objects) {	
			try {
                    if (!connector.link[pid-1].isConnected()){
                     neighbors.remove(pid - 1);
                     n = neighbors.size();
                     return;
                    }
                    ObjectOutputStream os = connector.dataOut[pid-1];
                    os.writeObject(Integer.valueOf(objects.length));
                    for (Object object : objects) 
                        os.writeObject(object);
                    os.flush();

            } catch (IOException e) {
                if (debug)System.out.println(e);
            }
	}

	public void sendMsgAll(Object ... objects) {	
        for (NameEntry name : neighbors) {
                int i = 0;
                i = name.getPid();
                if (i == myId) continue; 

            try {
                ObjectOutputStream os = connector.dataOut[i - 1];
                os.writeObject(Integer.valueOf(objects.length));
                for (Object object : objects) 
                    os.writeObject(object);
                os.flush();
            } catch (IOException e) {
                if (debug) System.out.println(e);	
                 neighbors.remove(name);
                 n = neighbors.size();
            }
        }
	}

    public void setTimeoutAll() {
        for (int i = 1; i <= n; i++) {
            if (i == myId) continue;
            try {
                connector.link[i - 1].setSoTimeout(100);
            }catch(Exception e) {}
        } 
    }

    public void unsetTimeoutAll() {
         for (int i = 1; i <= n; i++) {
            if (i == myId) continue;
            try {
                connector.link[i - 1].setSoTimeout(0);
            }catch(Exception e) {}
        } 
    }

	public Msg receiveMsg(int fromId) {
		int i = fromId - 1;
		try {
			ObjectInputStream oi = connector.dataIn[i];
			int numItems = ((Integer) oi.readObject()).intValue();
			LinkedList<Object> recvdMessage = new LinkedList<Object>();
            for (int j = 0; j < numItems; j++) 
                recvdMessage.add(oi.readObject());
			String tag = (String) recvdMessage.removeFirst();
			return new Msg(fromId, myId, tag, recvdMessage);
		} catch (Exception e) { 
           // System.out.println(e);
			return null;		
		}
	}

    public void removeNeighbors(ArrayList<Integer> comp) {
        for (NameEntry name: neighbors) {
            Integer i = name.getPid();
            if (i == myId) continue;
            if (!comp.contains(i))
                neighbors.remove(name);
        } 
        n = neighbors.size();
    }
    
//	public synchronized void executeMsg(Msg m) {	
//		handleMsg(m, m.src, m.tag);
//		notifyAll();
//	}
    
	public synchronized int getMyId() { return myId; }
	public List<NameEntry> getNeighbors() { return neighbors; }
	public void close() {connector.closeSockets(); }
	public void turnPassive() {	}
}
