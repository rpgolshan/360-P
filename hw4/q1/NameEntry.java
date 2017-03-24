import java.net.InetSocketAddress;

class NameEntry {
    public int pid;
    public InetSocketAddress addr ;

    public NameEntry (int proc, String host , int port ){
        pid = proc;
        addr = new InetSocketAddress (host , port);
    }

    public InetSocketAddress getAddress() {
        return addr;
    }

    public int getPort() {
        return addr.getPort(); 
    }

    public int getPid() {
        return pid;
    }

    public String toString() {
        return "PID = " + pid + "\tAddress = " + addr.toString() + "\n"; 
    }
}


