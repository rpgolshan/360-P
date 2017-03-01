import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UDPServer extends Server implements Runnable {
	
	static Socket UDPsocket;
	
	public UDPServer(int udpPort){
		try {
			UDPSocket = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		boolean connected = true;
		byte[] sendbuf = new byte[1000];
		byte[] recbuf = new byte[1000];
		
		while(connected){
			try {	
				String inMessage = ""; String outMessage = "";
    			DatagramPacket inPacket = new DatagramPacket(recbuf, recbuf.length);
    			UDPSocket.receive(inPacket);//udp blocking call
    			inMessage = new String(inPacket.getData(), inPacket.getOffset(), inPacket.getLength());
    			outMessage = execute(inMessage);
    			
    			outMessage += "\n";
    			sendbuf = outMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendbuf, sendbuf.length, inPacket.getAddress(), inPacket.getPort());
                UDPSocket.send(sendPacket);
				System.out.println(outMessage);
			} catch (IOException e) {
	            connected =false;
			}		
		}
		UDPSocket.close();
	}
	

}
