package distributedES;
import java.util.LinkedList;

public class Node implements Comparable<Node> {

	String UID;
	//LinkedList<DistributedFutureTask> Queue;
	int numTasks;
	
	Node(String UniqueID){
		UID = UniqueID;
		numTasks = 0;
	}
	
	Node(String UniqueID, int numT){
		UID = UniqueID;
		numTasks = numT;
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
