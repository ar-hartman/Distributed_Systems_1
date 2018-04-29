package task3_compute;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import task3_compute.Node;


public interface GraphSearcher extends Serializable{
	public Map<Node,Map<Node,Integer>> searchBenchmark(int howMany,Node[] nodes) throws RemoteException;
	public static final int DISTANCE_INFINITE = -1;
	public int getDistance(Node from, Node to) throws RemoteException;
	public int getTransitiveDistance(int distance, Node from, Node to)throws RemoteException ;}
