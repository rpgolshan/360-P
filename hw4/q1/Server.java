import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server extends Thread{
    private int port;
    public Inventory inventory;
    public int pid;
    public int numNeighbors;
    public ArrayList<NameEntry> neighbors;
    public Linker link;
    public ServerSocket serverSocket;
    public LamportMutex lMutex;

    public Server() {
        inventory = new Inventory();
        neighbors = new ArrayList<NameEntry>();
        try {
            Scanner sc = new Scanner(System.in);
            pid = sc.nextInt();
            numNeighbors = sc.nextInt();
            String invenFileName = sc.nextLine();
            invenFileName = invenFileName.replace(" ", "");

            /* read neighbors */
            int i = 1;
            for (int j = 0; j < numNeighbors; j++) {
                String line = sc.nextLine();
                String[] s = line.split(":");
                NameEntry neighbor = new NameEntry(i, s[0], Integer.parseInt(s[1]));
                i++;
                neighbors.add (neighbor);
            }
            sc.close();
            port = neighbors.get(pid - 1).getPort(); //pid starts at 1

            createInventory(invenFileName);
            
            // time to calibrate servers
            serverSocket = new ServerSocket(port);
            link = new Linker(pid, serverSocket, neighbors);
            lMutex = new LamportMutex(link, pid, this);
            link.init(lMutex);
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
         try {
            while (true) {
                new ServerMultiThread(serverSocket.accept(), inventory, lMutex).start();  
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1); 
        }
    }

    public void setInventory(Inventory inv) {
        inventory = inv;
        ServerMultiThread.inventory = inv;
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
//    String fileName = args[0];

    Server serTcp = new Server();
//    System.out.println(ser.toString());
    serTcp.start();
  }
}
