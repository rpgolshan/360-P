import java.util.*; import java.io.*;
public class Linker {
	public int myId;	
	public int n; // number of neighbors including myself
	Connector connector = null;
	public List<NameEntry> neighbors;
	public Linker (int pid, int port,List<NameEntry> neighbors) throws Exception { 
        myId = pid;
		connector = new Connector(myId, port);
		connector.Connect(neighbors);
        close();
	}

	public void sendMsg(int destId, Object ... objects) {	
			int j = neighbors.indexOf(destId);
//			try {
//				LinkedList<Object> objectList = Util.getLinkedList(objects);
//				ObjectOutputStream os = connector.dataOut[j];
//				os.writeObject(Integer.valueOf(objectList.size()));
//				for (Object object : objectList) 
//					os.writeObject(object);
//				os.flush();
//			} catch (IOException e) {System.out.println(e);close();	}
	}
	public Msg receiveMsg(int fromId) {
		int i = neighbors.indexOf(fromId);
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
    /*
	public synchronized void handleMsg(Msg m, int src, String tag) { }
	public synchronized void executeMsg(Msg m) {	
		handleMsg(m, m.src, m.tag);
		notifyAll();
		if (app != null) app.executeMsg(m);		
	}
    */
	public synchronized int getMyId() { return myId; }
	public List<NameEntry> getNeighbors() { return neighbors; }
	public void close() {connector.closeSockets(); }
	public void turnPassive() {	}
}
