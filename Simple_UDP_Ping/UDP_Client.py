import sys, time, re
from socket import *

# Get the server hostname and port as command line arguments
numArg = len(sys.argv)
argv = sys.argv        
host = str(sys.argv[1]) 
port = str(sys.argv[2]) 
timeout = 2 # in second, change as needed
pings = 10

# Create UDP client socket
# Note the use of SOCK_DGRAM for UDP datagram packet
clientsocket = socket(AF_INET, SOCK_DGRAM)
# Set socket timeout as 1 second
clientsocket.settimeout(timeout)
# Command line argument is a string, change the port into integer
port = int(port)  
# Sequence number of the ping message
ptime = 0  
counter = 0
minRTT = 0
maxRTT = 0
totalRTT = 0
# Ping for 10 times
while ptime < pings: 
    ptime += 1
    # Format the message to be sent. 
    # use time.asctime() for currTime
    data = ' '.join((str(x) for x in ("PING", ptime, time.asctime(), "CRLF")))
    try:
        # Sent time. from time.time()
        RTTb = time.time()
        # Send the UDP packet with the ping message
        clientsocket.sendto(data.encode(),(host, port))
        # Receive the server response
        message, address = clientsocket.recvfrom(1024)  
        # Received time. use time.time()
        RTTa = time.time()
        # Display the server response as an output
        
        #split the received message into a dictionary
        splitMessage = message.split()

        ipAddress =  str(address)
        ipAddress = ipAddress.split("'")
        # Round trip time is the difference between sent and received time
        rtt = RTTa - RTTb
        receivedMessage = splitMessage[0]
        sequence = splitMessage[1]        
        print(receivedMessage + " received from " + ipAddress[1] + ": seq#= " + sequence + " time= " + str(rtt*1000) + " ms")
        counter += 1
        if ptime == 1:
            minRTT = rtt
            maxRTT = rtt
        else: 
            if rtt < minRTT:
                minRTT = rtt
            if rtt > maxRTT:
                maxRTT = rtt
        totalRTT += rtt              
    except:
        # Server does not response
        # Assume the packet is lost
        print ("Request timed out.")
    continue

packetLoss = (float(pings - counter) / pings) * 100
averageRTT = float(totalRTT) / counter
print "--- ping statistics ---"
print str(pings) + " packets transmitted, " + str(counter) + " received, " + str(packetLoss) + "% packet loss"
print "rtt  min/avg/max = " + str(minRTT*1000) + " " + str(averageRTT*1000) + " " + str(maxRTT*1000) 
# Close the client socket
clientsocket.close()
 




