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

    public Server(String fileName) {
        inventory = new Inventory();
        neighbors = new ArrayList<NameEntry>();
        try {
            Scanner sc = new Scanner(new FileReader(fileName));
            pid = sc.nextInt();
            numNeighbors = sc.nextInt();
            String invenFileName = sc.nextLine();
            invenFileName = invenFileName.replace(" ", "");

            /* read neighbors */
            int i = 1;
            while ( sc.hasNext ()) {
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
            Linker link = new Linker(pid, port, neighbors);
        } catch(Exception e){
            e.printStackTrace();
        }
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
    if (args.length != 1) {
      System.out.println("ERROR: Provide an argument");
      System.out.println("\t<file>: the file of the server config");
      System.exit(-1);
    }
    String fileName = args[0];

    Server serTcp = new Server(fileName);
//    System.out.println(ser.toString());
    serTcp.start();
  }
}
