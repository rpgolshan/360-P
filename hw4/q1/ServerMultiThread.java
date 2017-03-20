import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ServerMultiThread extends Thread {
    private Socket socket = null;
    public static Server server = null;
    public ServerMultiThread(Socket s) {
        socket = s; 
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
