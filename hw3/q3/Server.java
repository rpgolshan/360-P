import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;

public class Server {
    private Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
    public Server() {
    } 

    public int increment(String product) {
        int current = ht.get(product); 
        current++;
        return ht.put(product, current);
    }
    public int set(String product, int amount) {
        return ht.put(product, amount); 
    }

    public int get(String product) {
        return ht.get(product);  
    }

  public static void main (String[] args) {
    int tcpPort;
    int udpPort;
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <udpPort>: the port number for UDP connection");
      System.out.println("\t(3) <file>: the file of inventory");

      System.exit(-1);
    }
    tcpPort = Integer.parseInt(args[0]);
    udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];

    Server ser = new Server();
    // parse the inventory file

    try {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] s = line.split(" ");
            if (s.length < 2) { 
                System.out.println("Incorrect ammount of variables");
                System.exit(1);
            }
            ser.set(s[0], Integer.parseInt(s[1]));
        }
        reader.close();
    } catch(Exception e){
        e.printStackTrace();
    }
    // TODO: handle request from clients
    
  }
}
