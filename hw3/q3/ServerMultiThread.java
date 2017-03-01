import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;

public class ServerMultiThread extends Thread {
    private Socket socket = null;
    private boolean isTcp = true;
    public static Server server = null;
    public ServerMultiThread(Socket s) {
        socket = s; 
    }    

    public static void setInventory(Server s) {
        server = s;
    }
    public void run() {
       try( 
               PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));)
       {
           String input;
           while ((input = reader.readLine()) != null) {
             String output = server.parseInput(input);
             out.println(output);
             out.println("DONE");
           }
       
         socket.close();
       } catch(Exception e){
           e.printStackTrace();
       }  
    }
}
