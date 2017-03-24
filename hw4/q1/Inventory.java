import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

public class Inventory implements Serializable {

    public Hashtable<String, Integer> ht;
    public Hashtable<String, ArrayList<Order>> userList;
    public ArrayList<Order> orderList;
    public int orderId;
    public static final long serialVersionUID = 50L;

    public Inventory() {
        ht = new Hashtable<String, Integer>();
        userList = new Hashtable<String, ArrayList<Order>>();
        orderList = new ArrayList<Order>();
        orderId = 1;
    }

    public class Order implements Serializable {
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

    public void populate(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] s = line.split(" ");
                if (s.length >= 2) { 
                    set(s[0], Integer.parseInt(s[1]));
                }
        }
        reader.close();
        } catch(Exception e){
            e.printStackTrace();
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

    public synchronized String toString() {
        return ht.toString(); 
    }

    public synchronized String purchase(String user, String product, int quantity) {
        Integer current = ht.get(product);
        if (current == null) {
            return "Not Available - We do not sell this product"; 
        }
        if (current < quantity) {
            return "Not Available - Not enough items"; 
        }  
        ht.put(product, current - quantity);

        Order order = new Order(user, product, quantity, orderId);
        orderList.add(order);
        String s = orderId + " " + user + " " + product + " " + quantity;
        ArrayList<Order> perUserList = userList.get(user);
        if (perUserList == null) {
            perUserList = new ArrayList<Order>();
        }
        perUserList.add(order);
        userList.put(user, perUserList);
        orderId++;
        return "Your order has been placed, " + s;
    }

    public synchronized String cancel(int orderNum) {
        if (orderNum > orderList.size()) {
            return orderNum + " not found, no such order"; 
        } 
        Order o = orderList.get(orderNum - 1); 
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


}
