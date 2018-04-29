package HW4_Compute;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;



import java.util.Set;

public class SearcherImpl implements GraphSearcher  {
	
	public SearcherImpl() throws RemoteException {
		super();
	}

	

	/*
	private final int digits;
	private final Node[] nodes;
	
	public SearcherImpl(int digits, Node[] nodes) {
		this.digits = digits;
		this.nodes = nodes;
	}
	*/
	
	
	
	
	/** A trivial distance measurement algorithm
	 * 
	 * Starting from the source node, then a set of visited nodes
	 * is always extended by immediate neighbors of all visited nodes,
	 * until the target node is visited or no node is left.
	 */
	@Override
	public int getDistance(Node from, Node to) throws RemoteException {
		// visited keeps the nodes visited in past steps.
		Set<Node> visited = new HashSet<Node>();
		// boundary keeps the nodes visited in current step.
		Set<Node> boundary = new HashSet<Node>();

		int distance = 0;

		// We start from the source node.
		boundary.add(from);

		// Traverse the graph until finding the target node.
		while (!boundary.contains(to)) {
			// Not having anything to visit means the target node cannot be reached.
			if (boundary.isEmpty())
				return (GraphSearcher.DISTANCE_INFINITE);

			Set<Node> traversing = new HashSet<Node>();

			// Nodes visited in current step become nodes visited in past steps.
			visited.addAll(boundary);

			// Collect a set of immediate neighbors of nodes visited in current step.
			for (Node node : boundary)
				traversing.addAll(node.getNeighbors());

			// Out of immediate neighbors, consider only those not yet visited.
			for (Iterator<Node> node = traversing.iterator(); node.hasNext();) {
				if (visited.contains(node.next()))
					node.remove();
			}

			// Make these nodes the new nodes to be visited in current step.
			boundary = traversing;

			distance++;
		}

		return (distance);
	}

	/**
	 * A transitive distance measurement algorithm.
	 * 
	 * Starting from the source node, a set of visited nodes
	 * is always extended by transitive neighbors of all visited
	 * nodes, until the target node is visited or no node is left.
	 */
	@Override
	public int getTransitiveDistance(int distance, Node from, Node to) throws RemoteException {
		// visited keeps the nodes visited in past steps.
		Set<Node> visited = new HashSet<Node>();
		// boundary keeps the nodes visited in current step.
		Map<Node, Integer> boundary = new HashMap<Node, Integer>();

		// We start from the source node.
		boundary.put(from, 0);

		// Traverse the graph until finding the target node.
		while (true) {
			// Not having anything to visit means the target node cannot be reached.
			if (boundary.isEmpty()) {
				return (GraphSearcher.DISTANCE_INFINITE);
			}

			Map<Node, Integer> traversing = new HashMap<Node, Integer>();

			// Collect transitive neighbors of nodes visited in current step
			for (Entry<Node, Integer> currentTuple : boundary.entrySet()) {
				final Node currentNode = currentTuple.getKey();
				final int currentDistance = currentTuple.getValue();
				if (visited.contains(currentNode)) {
					continue;
				}

				Map<Node, Integer> partialGraph = currentNode.getTransitiveNeighbors(distance);

				for (Entry<Node, Integer> searchedTuple : partialGraph.entrySet()) {
					// Check whether the node is already traversed
					final Node searchedNode = searchedTuple.getKey();
					final int dist = currentDistance + searchedTuple.getValue();

					if (traversing.containsKey(searchedNode)) {
						if (dist < traversing.get(searchedNode))
							traversing.put(searchedNode, dist);
					} else {
						traversing.put(searchedNode, dist);
					}
				}
				// Nodes visited in current step become nodes visited in past steps
				visited.add(currentNode);
			}
			for (Entry<Node, Integer> entry : traversing.entrySet()) {
				if (entry.getKey().equals(to)) {
					return entry.getValue();
				}
			}
			boundary = traversing;
		}
	}

	@Override
	public Map<Node, Map<Node, Integer>> searchBenchmark(int howMany, Node[] nodes) throws RemoteException  {
		Random random = new Random();
		GraphSearcher searcher = (GraphSearcher) new SearcherImpl();		
		
		// Display measurement header.
		Map<Node, Map<Node, Integer>> results = new HashMap<Node, Map<Node, Integer>>();
		Map<Node, Integer> temp = new HashMap<Node, Integer>();;		
		
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
				// record the measurement result.
				temp.put(nodes[idxTo], distance);
				results.put(nodes[idxFrom], temp);
			}
		}
		return results;
	}
}
