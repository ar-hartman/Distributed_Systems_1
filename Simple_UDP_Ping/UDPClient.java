package week5;

import java.net.*;

class UDPClient {

    public static void main(String argv[]) throws Exception
    {
	// socket variables
	DatagramSocket clientSocket;
	DatagramPacket sendPacket;
	DatagramPacket receivePacket;
	byte[] receiveData = new byte[1024];
	byte[] sendData = new byte[1024];
	InetAddress IPAddress;

	// client variables
	String serverSentence;
	// command-line arguments
	int port;
	String server;
	
	// CONSTANTS
	String MESSAGE = "PING";
	int TIMEOUT = 1000;
	int PINGS = 10;
	
	// statistic arithmetic variables
	double counter = 0, rtt = 0, RTTa = 0, RTTb = 0, minRTT = 0, maxRTT = 0, totalRTT = 0, averateRTT = 0;

	// process command-line arguments
	if (argv.length < 2) {
	    System.out.println ("Usage: java UDPServer hostname port\n");
	    System.exit (-1);
	}
	server = argv[0];
	port = Integer.parseInt(argv[1]);

	// Create client socket to destination
	clientSocket = new DatagramSocket();
	IPAddress = InetAddress.getByName (server);
	
	// set socket time-out setting
	clientSocket.setSoTimeout(TIMEOUT);
	
	// Get input from user
    //System.out.println("Client ready for input");
	//clientSentence = inFromUser.readLine();
	

	for (int i = 0; i < PINGS; i++) {
		// begin try-catch (PING)
		try {
			String sendingMessage = MESSAGE + " " + i + " " + System.currentTimeMillis() + " CRLF";
			// Get input from user
			sendData = sendingMessage.getBytes();
			// Create packet and send to server
			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			
			// Sent time. from time.time()
			RTTb = System.currentTimeMillis();
			
			clientSocket.send(sendPacket);
			//System.out.println (sendingMessage);

			// Create receiving packet and receive from server
			receivePacket = new DatagramPacket(receiveData, receiveData.length); 
			clientSocket.receive(receivePacket);
			
			// Received time. use time.time()
	        RTTa = System.currentTimeMillis();
			// Round trip time is the difference between sent and received time
	        rtt = RTTa - RTTb;
			
	        
	        //serverSentence = new String(receivePacket.getData(),0, 100);
			serverSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
			String[] setenceStructures = serverSentence.split(" ");

			System.out.println(setenceStructures[0] + " received from " + receivePacket.getAddress() + 
					": seq#=" + setenceStructures[1] + " time= " + rtt + " ms");
			
			//taking tally of a good PING receipt
			counter++;
			// taking some RTT statistics
			if (i == 0) {
				minRTT = rtt;
			    maxRTT = rtt;
			}
			else { 
	            if (rtt < minRTT)
	                minRTT = rtt;
	            if (rtt > maxRTT)
	                maxRTT = rtt;
			}
	        totalRTT += rtt;   
			
		} catch (Exception e) {
			System.err.println("Request timed out.");
		}
		// end try-catch
	}	
	
	
	//print statistics
	System.out.println();
	System.out.println("--- PING statistics ---");
	System.out.println(PINGS + " packets transmitted, " + counter + " received, " + counter/PINGS*100 + "% packet loss");
	System.out.println("rtt min/avg/max = " + minRTT + " " + totalRTT/counter + " " + maxRTT + " ms" );
	
	
	
	// close the socket
	clientSocket.close();

    } // end main

} // end class