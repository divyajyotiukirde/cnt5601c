package processes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loggers.LogWriter;
import utils.CommonProperties;
import utils.Constants;
import utils.PeerInfoLoader;

/**This class is entry point to our project.
 * It loads the properties from CoomonInfo.cfg 
 * and PeerInfo.cfg, intializes logger and then 
 * starts the P2P Process
 * 
 * @author himan
 *
 */

public class Startup {

	public static void main(String[] args) throws SecurityException, IOException {
		 
		if(args.length != 1 ) {
			throw new RuntimeException("Cannot process the peerID, Correct way to start:  java Startup <peerId> \n"
					+ "Here <PeerId> is the Id with which the process should be started");
		}
		
		final int peerId = Integer.parseInt(args[0]); 
		
		// Initialize the Logger
		LogWriter.initialize(peerId);

		// Read Properties from Common.cfg
        CommonProperties properties = new CommonProperties(Constants.COMMON_CONFIG_FILE);
        
        //Read Peer Info
        PeerInfoLoader pInfo = new PeerInfoLoader();
        
        // This is whole list of Peers
        List<Peer> peerList = pInfo.load(Constants.PEER_INFO_FILE);
       
        
        // This is List of Peer to connect to
        List<Peer> peersToConnect = new ArrayList<>();
        
        Peer current = null;  // Store current peer info
        
        for(Peer p : peerList )
        {
        	if(p.getPeerId() < peerId )
        		peersToConnect.add(p);
        	
        	else if(p.getPeerId() == peerId)
        			{current =  p;
        			}
        }	
		
        peerList.remove(current);
     /*   System.out.println(current);
        for(Peer p :peersToConnect) {
        	System.out.println(p);
        }*/
  
        for(Peer p :peerList) {
        	System.out.println(p);
        }
        
        //Delegate Call
        Process process =  new Process(current, peerList, properties);
        process.setup();
        // Run the PeerProcess Thread
        Thread t = new Thread(process);
        t.setName("PEER PROCEESS : "+ current.peerId);
        t.start();
        
        LogWriter.getLogWriterInstance().debug("Connecting to "+peersToConnect.size()+"  peers");
        process.makePeerConncetions(peersToConnect);
        try {
            Thread.sleep(5);
            System.out.println(Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}
