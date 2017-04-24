import java.util.LinkedList;

public class Node implements Comparable<Node> {

	String UID;
	LinkedList<DistributedFutureTask> Queue;
	
	Node(String UniqueID){
		UID = UniqueID;
		Queue = new LinkedList<DistributedFutureTask>();
	}
	
	@Override
	public int compareTo(Node n) {
		if(n.Queue.size() > this.Queue.size()){
			return -1;
		}else if(n.Queue.size() < this.Queue.size()){
			return 1;
		}
		return 0;
	}


}
