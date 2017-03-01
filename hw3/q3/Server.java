import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends Thread{
    public static ReentrantLock lock = new ReentrantLock();
    private boolean isTcp = true;
    private int port;
    private static Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
    private static Hashtable<String, ArrayList<Order>> userList = new Hashtable<String, ArrayList<Order>>();
    private static ArrayList<Order> orderList = new ArrayList<Order>();
    private static int orderId = 1;

    public class Order {
        public String user;
        public String product; 
        public int quantity;
        public int orderNum;
        public boolean canceled;
        
        public Order(String u, String p, int q, int oNum) {
            user = u;
            product = p;
            quantity = q;
            orderNum = oNum;
            canceled = false;
        }
         
    }
    public Server(boolean tcp, int p) {
        isTcp = tcp; 
        port = p;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            while (true) {
                new ServerMultiThread(serverSocket.accept());  
            }
            
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1); 
        }
    }

    public int increment(String product) {
        int current = ht.get(product); 
        current++;
        return ht.put(product, current);
    }
    public void set(String product, int amount) {
        ht.put(product, amount); 
    }

    public int get(String product) {
        return ht.get(product);  
    }

    public String toString() {
        return ht.toString(); 
    }

    public synchronized String purchase(String user, String product, int quantity) {
        Integer current = ht.get(product);
        if (current == null) {
            return "Not Available - We do not sell this product"; 
        }
        if (current < quantity) {
            return "Not Available - Not Enough " + product; 
        }  
        ht.put(product, current - quantity);

        Order order = new Order(user, product, quantity, orderId);
        orderId++;
        String s = orderId + " " + user + " " + product + " " + quantity;
        ArrayList<Order> perUserList = userList.get(user);
        if (perUserList == null) {
            perUserList = new ArrayList<Order>();
        }
        perUserList.add(order);
        userList.put(user, perUserList);
        return s;
    }

    public synchronized String cancel(int orderNum) {
        if (orderNum >= orderList.size()) {
            return orderNum + " not found, no such order"; 
        } 
        Order o = orderList.get(orderNum); 
        o.canceled = true;

        int current = ht.get(o.product);
        ht.put(o.product, current + o.quantity);

        return "Order " + orderNum + " is canceled";
    }

    public synchronized String search(String user) {
        ArrayList<Order> perUserList = userList.get(user);
        String error = "No order found for " + user;
        if (perUserList == null) 
            return error;
        String total = "";
        for (Order o : perUserList) {
            if (!o.canceled) {
               total += o.orderNum + ", " + o.product + ", " + o.quantity + "\n";
            }
        }
        if (total == "") return error;
        return total;
    }

    public synchronized String list() {
        String s = ""; 
        for (String p : ht.keySet()) {
            s += p + " " + ht.get(p) + "\n"; 
        }
        return s;
    }

  public String parseInput(String inMessage){
	  //process client request
	  String[] request = inMessage.split("\\s+");
	  String outMessage = "";
	  if(request[0].equals("Purchase")){
		  try{
			String username = request[1];
		  	String product = request[2];
		  	int quantity = Integer.parseInt(request[3]);
		  	outMessage = purchase(username, product, quantity);
		  } catch(NullPointerException | NumberFormatException f){
			  //purchase message didn't have all the fields defined or defined correctly
		  }
	  }else if(request[0].equals("cancel")){
		  try{
			  int order = Integer.parseInt(request[1]);
			  outMessage = cancel(order);
		  } catch(NullPointerException | NumberFormatException f){
			  //cancel message didn't specify an order or didn't give an integer
		  }
	  }else if(request[0].equals("search")){
		  try{
			  String username = request[1];
			  outMessage = search(username);
		  }catch(NullPointerException | NumberFormatException f){
			  //search message did not specify a username
		  }
	  }else if(request[0].equals("list")){
		  outMessage = list();
	  }

	return outMessage;

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

    Server serTcp = new Server(true, tcpPort);
    Server serUdp = new Server(false, udpPort);
    // parse the inventory file

    try {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] s = line.split(" ");
            if (s.length >= 2) { 
                serTcp.set(s[0], Integer.parseInt(s[1]));
            }
        }
        reader.close();
    } catch(Exception e){
        e.printStackTrace();
    }


    // TODO: handle request from clients
    
//    System.out.println(ser.toString());
    serTcp.start();
    serUdp.start();
  }
}
