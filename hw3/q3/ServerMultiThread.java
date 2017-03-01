import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ServerMultiThread extends Thread {
    private Socket socket = null;
    private DatagramSocket dsocket = null;
    private DatagramPacket packet = null;
    private byte[] buff = null;
    private boolean isTcp = true;
    public static Server server = null;
    public ServerMultiThread(Socket s) {
        socket = s; 
    }    

    public ServerMultiThread(DatagramSocket s, DatagramPacket p, byte[] b) {
        isTcp = false;
        packet = p;
        buff = b;
        dsocket = s;
    }

    public void run() {
        if (isTcp) {
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
        else {
            InetAddress address;
            int port;
            try {
                address = packet.getAddress(); 
                port = packet.getPort();
                String output = server.parseInput(new String(packet.getData()));
                buff = output.getBytes();
                packet = new DatagramPacket(buff, buff.length, address, port);
                dsocket.send(packet);
            } catch(Exception e) {
            }
        }
    }
}
