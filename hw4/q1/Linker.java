import java.util.*; import java.io.*;
import java.net.ServerSocket;
public class Linker {
	public int myId;	
	public int n; // number of neighbors including myself
    public LamportMutex lMutex;
	Connector connector = null;
	public List<NameEntry> neighbors;
	public Linker (int pid, ServerSocket s ,List<NameEntry> neighbors) throws Exception { 
        myId = pid;
		connector = new Connector(myId, s);
		connector.Connect(neighbors);
	}

    public void init(LamportMutex lm){
        lMutex = lm;
		for (NameEntry name : neighbors)
			(new ListenerThread(name.getPid(), this, lMutex)).start();		    	
	}

	public void sendMsg(int pid, Object ... objects) {	
			try {
                    ObjectOutputStream os = connector.dataOut[pid-1];
                    os.writeObject(Integer.valueOf(pid));
                    for (Object object : objects) 
                        os.writeObject(object);
                    os.flush();

            } catch (IOException e) {System.out.println(e);close();	}
	}

	public void sendMsgAll(Object ... objects) {	
			try {
                for (int i = 0; i < n; i++) {
                    if (i == myId - 1) continue; 

                    ObjectOutputStream os = connector.dataOut[i];
                    os.writeObject(Integer.valueOf(objects.length));
                    for (Object object : objects) 
                        os.writeObject(object);
                    os.flush();

                }
            } catch (IOException e) {System.out.println(e);close();	}
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
		} catch (Exception e) { System.out.println(e);
			close(); return null;		
		}
	}
    
	public synchronized void handleMsg(Msg m, int src, String tag) {
    
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
