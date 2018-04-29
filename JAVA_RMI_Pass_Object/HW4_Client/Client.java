package HW4_Client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Random;
import HW4_Compute.Node;
import HW4_Compute.NodeImpl;
import HW4_Compute.GraphSearcher;

public class Client {

	// How many nodes and how many edges to create.
		private static final int GRAPH_NODES = 1000;
		private static final int GRAPH_EDGES = 2000;


		private static Node[] nodes;

		private static Random random = new Random();

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
		
		
		public static void printMessage(Map<Node, Map<Node, Integer>> results) {
			int[][] distance = new int[100][100];											// create a 2D array to reinstantiate the vales from the Map
			int i = 0, m = 0;
			for (Map.Entry<Node, Map<Node, Integer>> entry : results.entrySet()) {			// iterate through the map
				Map<Node, Integer> newMap = entry.getValue();								// get the sub-key/value from the map
				for (Map.Entry<Node, Integer> values : newMap.entrySet()) {
					int dist = values.getValue();											// get the distance value
					distance[i][m] = dist;													// and add it to the array
					m++;
				}			
				i++;
				m = 0;
			}			
			for (int q = 0; q < results.size(); q++) {
				for (int r = 0; r < results.size(); r++) {									// lastly, print the results
					System.out.println("Distance from Node: " + q + "\t to Node:  " + r + "\t = " + distance[q][r]);
				}
			}	
		}
		
		
	
	
	
	
	
	
	
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {											// install security manage if not already installed
			System.setSecurityManager(new SecurityManager());
		}
		
		// Create a randomly connected graph and do a quick measurement.
		// Consider replacing connectSomeNodes with connectAllNodes to verify that all distances are equal to one.
		Node[] nodes = createNodes(GRAPH_NODES);
		connectSomeNodes(GRAPH_EDGES);
		
		
		try {
			String name = "Compute";														// construct the name to look up the remote object			
			Registry registry = LocateRegistry.getRegistry(args[0]);						// get remote reference to registry
			GraphSearcher comp = (GraphSearcher) registry.lookup(name);						// look up the remote object by name
			
			int howMany = (Integer.parseInt(args[1]));										// accept user input for the number of searches
			
			Map<Node, Map<Node, Integer>> message = comp.searchBenchmark(howMany, nodes);	// pass object to the server, received the output from the search method

			printMessage(message);
		}	catch (Exception e) {
			System.err.println("Client exception");
			e.printStackTrace();
		}

	}

}
