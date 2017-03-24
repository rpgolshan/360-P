public class ListenerThread extends Thread {
	int channel;
	Linker comm = null;
    LamportMutex lMutex;

	public ListenerThread(int channel, Linker comm, LamportMutex lm) {
		this.channel = channel;
		this.comm = comm;
        this.lMutex = lm;
	}
	public void run() {
		while (true) {
            Msg m = comm.receiveMsg(channel);
            if (m!=null)
                lMutex.handleMsg(m, m.src, m.tag);
            else return;
		}
	}
}
