package HW3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class ClientMain {
	// How many nodes and how many edges to create.
	private static final int GRAPH_NODES = 1000;
	private static final int GRAPH_EDGES = 2000;

	// How many searches to perform
	private static final int SEARCHES = 50;

	private static Node[] nodes;

	private static Random random = new Random();
	private static Searcher searcher = new SearcherImpl();

	/**
	 * Creates nodes of a graph.
	 * 
	 * @param howMany
	 */
	public static Node[] createNodes(int howMany) {
		nodes = new Node[howMany];

		for (int i = 0; i < howMany; i++) {
			nodes[i] = new NodeImpl();
		}
		return nodes;
	}

	/**
	 * Creates a fully connected graph.
	 */
	public void connectAllNodes() {
		for (int idxFrom = 0; idxFrom < nodes.length; idxFrom++) {
			for (int idxTo = idxFrom + 1; idxTo < nodes.length; idxTo++) {
				nodes[idxFrom].addNeighbor(nodes[idxTo]);
				nodes[idxTo].addNeighbor(nodes[idxFrom]);
			}
		}
	}

	/**
	 * Creates a randomly connected graph.
	 * 
	 * @param howMany
	 */
	public static void connectSomeNodes(int howMany) {
		for (int i = 0; i < howMany; i++) {
			final int idxFrom = random.nextInt(nodes.length);
			final int idxTo = random.nextInt(nodes.length);

			nodes[idxFrom].addNeighbor(nodes[idxTo]);
		}
	}

	/**
	 * Runs a quick measurement on the graph.
	 * 
	 * @param howMany
	 */
/*	public static void searchBenchmark(int howMany) {
		// Display measurement header.
		System.out.printf("%7s %8s %13s %13s\n", "Attempt", "Distance", "Time", "TTime");
		for (int i = 0; i < howMany; i++) {
			// Select two random nodes.
			final int idxFrom = random.nextInt(nodes.length);
			final int idxTo = random.nextInt(nodes.length);

			// Calculate distance, measure operation time
			final long startTimeNs = System.nanoTime();
			final int distance = searcher.getDistance(nodes[idxFrom], nodes[idxTo]);
			final long durationNs = System.nanoTime() - startTimeNs;

			// Calculate transitive distance, measure operation time
			final long startTimeTransitiveNs = System.nanoTime();
			final int transitiveDistance = searcher.getTransitiveDistance(4, nodes[idxFrom], nodes[idxTo]);
			final long transitiveDurationNs = System.nanoTime() - startTimeTransitiveNs;

			if (distance != transitiveDistance) {
				System.out.printf("Standard and transitive algorithms inconsistent (%d != %d)\n", distance,
						transitiveDistance);
			} else {
				// Print the measurement result.
				System.out.printf("%7d %8d %13d %13d\n", i, distance, durationNs / 1000, transitiveDurationNs / 1000);
			}
		}
	}
*/
	
	public static Map<Node, Map<Node, Integer>> searchBenchmark(int howMany,Node[] nodes) {
		// Display measurement header.
		Map<Node, Map<Node, Integer>> results = new HashMap<Node, Map<Node, Integer>>();
		Map<Node, Integer> temp = new HashMap<Node, Integer>();;
		
		System.out.printf("%7s %8s %13s %13s\n", "Attempt", "Distance", "Time", "TTime");
		for (int i = 0; i < howMany; i++) {
			// Select two random nodes.
			final int idxFrom = random.nextInt(nodes.length);
			final int idxTo = random.nextInt(nodes.length);

			// Calculate distance, measure operation time
			final long startTimeNs = System.nanoTime();
			final int distance = searcher.getDistance(nodes[idxFrom], nodes[idxTo]);
			final long durationNs = System.nanoTime() - startTimeNs;

			// Calculate transitive distance, measure operation time
			final long startTimeTransitiveNs = System.nanoTime();
			final int transitiveDistance = searcher.getTransitiveDistance(4, nodes[idxFrom], nodes[idxTo]);
			final long transitiveDurationNs = System.nanoTime() - startTimeTransitiveNs;

			if (distance != transitiveDistance) {
				System.out.printf("Standard and transitive algorithms inconsistent (%d != %d)\n", distance,
						transitiveDistance);
			} else {
				// Print the measurement result.
				System.out.printf("%7d %8d %13d %13d\n", i, distance, durationNs / 1000, transitiveDurationNs / 1000);
				temp.put(nodes[idxTo], distance);
				results.put(nodes[idxFrom], temp);
			}
		}
		return results;
	}

	
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// Create a randomly connected graph and do a quick measurement.
		// Consider replacing connectSomeNodes with connectAllNodes to verify that all distances are equal to one.
		Node[] nodes = createNodes(GRAPH_NODES);
		connectSomeNodes(GRAPH_EDGES);

		// get the localhost IP address
		InetAddress host = InetAddress.getLocalHost();
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			socket = new Socket(host.getHostName(), 15002);
			oos = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Sending request to Socket Server");
			
			oos.writeObject(nodes);
			oos.flush();
			
			System.out.println(nodes);
			
			// read the server message
			ois = new ObjectInputStream(socket.getInputStream());
			@SuppressWarnings("unchecked")
			Map<Node, Map<Node, Integer>> message = (Map<Node, Map<Node, Integer>>)ois.readObject();	// receive Map back from Server
			if (message != null) System.out.println("Received something");								// console confirmation message
			printMessage(message, 50);																	// print results from Server
			

			//close resources
			ois.close();
			oos.close();
		} catch (Exception e){
			System.out.println("Could not connect to Server");
		}		
		}
	
	
	public static void printMessage(Map<Node, Map<Node, Integer>> results, int howMany) {
		System.out.printf("%7s %8s %13s %13s\n", "Attempt", "Distance", "Time", "TTime");
		for (int i = 0; i < howMany; i++) {
			// Select two random nodes.
			final int idxFrom = random.nextInt(nodes.length);
			final int idxTo = random.nextInt(nodes.length);

			// Calculate distance, measure operation time
			final long startTimeNs = System.nanoTime();
			final int distance = searcher.getDistance(nodes[idxFrom], nodes[idxTo]);
			final long durationNs = System.nanoTime() - startTimeNs;

			// Calculate transitive distance, measure operation time
			final long startTimeTransitiveNs = System.nanoTime();
			final int transitiveDistance = searcher.getTransitiveDistance(4, nodes[idxFrom], nodes[idxTo]);
			final long transitiveDurationNs = System.nanoTime() - startTimeTransitiveNs;

			if (distance != transitiveDistance) {
				System.out.printf("Standard and transitive algorithms inconsistent (%d != %d)\n", distance,
						transitiveDistance);
			} else {
				// Print the measurement result.
				System.out.printf("%7d %8d %13d %13d\n", i, distance, durationNs / 1000, transitiveDurationNs / 1000);
			}
		}
	}
	

	
	
}
