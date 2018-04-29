package HW4_engine;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import HW4_Compute.GraphSearcher;
import HW4_Compute.SearcherImpl;

public abstract class ComputeEngine implements GraphSearcher {

    public ComputeEngine() throws RemoteException{
        super();
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {							// install security manager
            System.setSecurityManager(new SecurityManager());
        }
        try {                       
            SearcherImpl engine = new SearcherImpl();						// create a new Searcher object
            String name = "Compute";
            GraphSearcher stub = (GraphSearcher) UnicastRemoteObject.exportObject(engine, 0);	// exports the remote object to receive remote invocations
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);									// make a remote call to the RMI registry
            
            System.out.println("ComputeEngine bound");
            
            
            System.out.println("Trying to do some stuff the client sent");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception (error provided by user generated source code):");
            e.printStackTrace();
        }
    }
}


