package task3_engine;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import task3_compute.GraphSearcher;
import task3_compute.SearcherImpl;
import task3_compute.GetSearcher;

public class ComputeEngine implements GetSearcher {

    public ComputeEngine() throws RemoteException{
        super();
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {					// install security manager
            System.setSecurityManager(new SecurityManager());
        }
        try {                       
            ComputeEngine engine = new ComputeEngine();				// create a new Server object
            
            String name = "Compute";								// exports the remote object to receive remote invocations
            GetSearcher stub = (GetSearcher) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();		// 
            registry.rebind(name, stub);							// make a remote call to the RMI registry
            
            System.out.println("ComputeEngine bound");
            
            
            System.out.println("Trying to do some stuff the client sent");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception (error provided by user generated source code):");
            e.printStackTrace();
        }
    }

	@Override
	public GraphSearcher getSearcher() throws RemoteException {	
		return new SearcherImpl();
	}
}


