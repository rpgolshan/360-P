import java.util.*;import java.net.*;import java.io.*;
public class Connector {
	ServerSocket listener; Socket[] link;
	public ObjectInputStream[] dataIn;
	public ObjectOutputStream[] dataOut;
    public int localport; 
    public int myId;
    public Connector(int pid, int port) {
        localport = port; 
        myId = pid;
    }

	public void Connect(List<NameEntry> neighbors)
			throws Exception {
		int numNeigh = neighbors.size();
		link = new Socket[numNeigh];
		dataIn = new ObjectInputStream[numNeigh];
		dataOut = new ObjectOutputStream[numNeigh];
//		int localport = getLocalPort(myId);
		listener = new ServerSocket(localport);
		
		/* accept connections from all the smaller processes */
		for (NameEntry name : neighbors) {
            int pid = name.getPid();
			if (pid  < myId) {
				Socket s = listener.accept();
				InputStream is = s.getInputStream();
				ObjectInputStream din = new ObjectInputStream(is);
				Integer hisId = (Integer) din.readObject();
				int i = hisId - 1;
				String tag = (String) din.readObject();
				if (tag.equals("hello")) {
					link[i] = s;
					dataIn[i] = din;
					dataOut[i] = new ObjectOutputStream(
							s.getOutputStream()); }
			}
		}
		/* contact all the bigger processes */
		for (NameEntry name : neighbors) {
            int pid = name.getPid();
			if (pid > myId) {
                InetSocketAddress addr = name.getAddress();
				int i = pid - 1;
                boolean connected = false;
                while (!connected) {
                    try {
                        link[i] = new Socket(addr.getHostName(), addr.getPort());
                        connected = true;
                    } catch (Exception e) {
                    }
                }
                dataOut[i] = new 
                    ObjectOutputStream(link[i].getOutputStream());
                /* send a hello message to P_i */
                dataOut[i].writeObject(new Integer(myId));
                dataOut[i].writeObject(new String("hello"));
                dataOut[i].flush();
                dataIn[i] = new ObjectInputStream(link[i].getInputStream()); 	

            }
        }

	}
	public void closeSockets() {
		try {
			listener.close();
			for (Socket s : link) {
                if (s != null) {
                    s.close();
                }
            }

		} catch (Exception e) { 
            System.err.println(e); }
	}
}
