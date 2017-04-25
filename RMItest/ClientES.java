//a test executor service, starts up two distributedfuturetasks and executes them


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.PriorityQueue;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class ClientES {

    private ClientES() {}

    public static void main(String[] args) {
    	PriorityQueue<Node> NodeQ = new PriorityQueue<Node>();
    	
        String host = (args.length < 1) ? null : args[0];
        System.out.println("Host: " + host);
        try {
        	Registry registry = LocateRegistry.getRegistry();
        	//get all the servers(nodes) and place in a priority queue
        	String[] Nodes = registry.list();
        	for (String node : Nodes){
        		NodeQ.add(new Node(node));
        	}
        	System.out.println(NodeQ);
        	//give them a simple callable that waits 8sec and returns a string
        	HelloWorld task = new HelloWorld();
        	//first distribtask
        	DistributedFutureTask f1 = new DistributedFutureTask(task);
        	UID UniqueID1 = new UID();
        	String DistribTaskID1 = UniqueID1.toString();
        	
        	Node n1 = NodeQ.poll();
        	n1.Queue.add(new Task(task));
        	System.out.println("Using node " + n1.UID);
        	f1.Initialize(host, n1.UID, DistribTaskID1);
        	NodeQ.add(n1);
            f1.Execute();  
            System.out.println("f1 sent to execute");
            System.out.flush();
            
            //second distribtask
            DistributedFutureTask f2 = new DistributedFutureTask(task);
        	UID UniqueID2 = new UID();
        	String DistribTaskID2 = UniqueID2.toString();
            
        	Node n2 = NodeQ.poll();
        	n2.Queue.add(new Task(task));
        	System.out.println("Using node " + n2.UID);
        	f2.Initialize(host, n2.UID, DistribTaskID2);
        	NodeQ.add(n2);
            f2.Execute();  
            System.out.println("f2 sent to execute");
            System.out.flush();
            
            //get results
        	String result1 = (String) f1.get();
        	String result2 = (String) f2.get();
        	System.out.println("get f1 recieved: " + result1 + " get f2 recieved: " + result2);

        	} catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}