package HW3;
import java.io.* ;
import java.net.* ;
import java.util.* ;


public final class WebServer {

    public static void main(String argv[]) throws Exception {
    	// 	Get the port number from the command line.
    	int port = 15002;
	
    	// Establish the listen socket at port
    	@SuppressWarnings("resource")
		ServerSocket socket = new ServerSocket(port);  
    	
    	System.out.println("Server is live at: " + port);
	
    	// Process HTTP service requests in an infinite loop.
    	while (true) {
    		// Listen for a TCP connection request.
    		Socket connection = socket.accept(); 
    		System.out.println("Connection Established");
    		
    		// 	Construct an object to process the HTTP request message.
    		HttpRequest request = new HttpRequest(connection);
	    
    		// Create a new thread to process the request.
    		Thread thread = new Thread(request);
	    
    		// Start the thread.
    		thread.start();
    		System.out.println("Thread Started");
    		thread.run();
    		System.out.println("Thread Run");    			
    	}
    }
}

final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;
    
    // Constructor
    public HttpRequest(Socket socket) throws Exception {
        // store the socket
        this.socket = socket;
    }
    
    // Implement the run() method of the Runnable interface.
    public void run() {
    	try {
    		processRequest();
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    }
    
    private void processRequest() throws Exception {
    	// Get a reference to the socket's input and output streams.
    	
    	InputStream is = socket.getInputStream();			// get input stream of socket 
    	OutputStream oss = socket.getOutputStream();		// get output stream of socket
    	DataOutputStream os = new DataOutputStream(oss);	// get dataoutput stream of socket
    	ObjectOutputStream oos = null;						// create objectoutputstream object
    	ObjectInputStream in = null;						// creae objectinputstream object
    	Node[] object1 = new Node[1000];					// create Node array 
    	
    	
    	// deserialization  
    	try {
    		in = new ObjectInputStream(is);					// initialize the object input stream
        	object1 = (Node[])in.readObject();        		// reconstitute the deserealized Node array
        	System.out.println("Object 1: " + object1);     // confirmation consolse message
    	
        	// How many searches to perform
        	final int SEARCHES = 50;					
    		Map<Node, Map<Node, Integer>> results = new HashMap<Node, Map<Node, Integer>>();	// create Map to return to Clinet 

    		results = searchBenchmark(SEARCHES, object1);	// perform searching function
                		    	
    		oos = new ObjectOutputStream(oss);					// initialize object output stream
			System.out.println("Sending response to Client");	// console message
    		oos.writeObject(results);							// send result map back to client
    		
    	
    	} catch (SocketException se) {							// exception catching
    		System.exit(0);
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (ClassNotFoundException cn) {
    		cn.printStackTrace();
    	}

        // Close streams and socket.
		in.close();
        os.close();
        socket.close();
    }
    
	public static Map<Node, Map<Node, Integer>> searchBenchmark(int howMany,Node[] nodes) {
		Random random = new Random();
		Searcher searcher = new SearcherImpl();		
		
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