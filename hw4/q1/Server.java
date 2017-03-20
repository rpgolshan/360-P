import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;

public class Server extends Thread{
    private int port;
    public Inventory inventory;

    public Server(int p, String fileName) {
        port = p;
        inventory = new Inventory();
        createInventory(fileName);
    }

    public void run() {
         try (ServerSocket serverSocket = new ServerSocket(port)){
            while (true) {
                new ServerMultiThread(serverSocket.accept(), inventory).start();  
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1); 
        }
    }

    public void createInventory(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] s = line.split(" ");
                if (s.length >= 2) { 
                    inventory.set(s[0], Integer.parseInt(s[1]));
                }
            }
            reader.close();
        } catch(Exception e){
            e.printStackTrace();
        }

   
    }

  public static void main (String[] args) {
    int tcpPort;
    if (args.length != 2) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <file>: the file of inventory");

      System.exit(-1);
    }
    tcpPort = Integer.parseInt(args[0]);
    String fileName = args[1];

    Server serTcp = new Server(tcpPort, fileName);
//    System.out.println(ser.toString());
    serTcp.start();
  }
}
