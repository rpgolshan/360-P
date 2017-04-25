package distributedES;
import java.util.ArrayList;

public class Node implements Comparable<Node> {

	String UID;
	//LinkedList<DistributedFutureTask> Queue;
	int numTasks;
	ArrayList<String>DFTs;
	
	Node(String UniqueID){
		UID = UniqueID;
		numTasks = 0;
		DFTs = new ArrayList<String>();
	}
	
	Node(String UniqueID, int numT){
		UID = UniqueID;
		numTasks = numT;
		DFTs = new ArrayList<String>();
	}
	
	void append(String s){
		DFTs.add(s);
	}
	
	@Override
	public int compareTo(Node n) {
		if(n.numTasks > this.numTasks){
			return -1;
		}else if(n.numTasks < this.numTasks){
			return 1;
		}
		return 0;
	}


}
