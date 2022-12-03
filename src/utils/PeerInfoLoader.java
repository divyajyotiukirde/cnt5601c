package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


import processes.Peer;

/**A loader class for all loading the Peer Details
 * All Peer details are mapped to a Peer Object.
 * 
 * 
 * @author himan
 *
 */
public class PeerInfoLoader {

	public List<Peer> load(String fileName){
		
		try {
			return read(fileName);
			
		} catch (Exception e) {
			
			throw new RuntimeException("Couldn't Read Remote Peer Info"+ e);
		}
	}
	
	/**Read PeerInfo.cfg, map Peer Data to Peer Objects
	 * and Return a List of Peers.
	 * 
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	
	private List<Peer> read (String fileName) throws FileNotFoundException, IOException, ParseException {
			
			File file = new File(fileName);
	        
			BufferedReader in = new BufferedReader(new FileReader(file));
	        
			List<Peer> peerList = new ArrayList<Peer>();
	        
	        String line;
	        
	        int i = 0;
	        
	        while ((line = in.readLine()) != null) {
	            line = line.trim();
	            
	            if ((line.length() <= 0)) {
	                continue;
	            }
	            String[] tokens = line.split("\\s+");
	            
	            if (tokens.length != 4) {
	                throw new ParseException (line, i);
	            }
	            
	            i++;
	            
	            int peerId = Integer.parseInt(tokens[0].trim());
	            String IP  = tokens[1].trim();
	            int port = Integer.parseInt(tokens[2].trim());
	            boolean hasfile = (tokens[3].trim().compareTo("1") == 0);
	            Peer p = new Peer(peerId,IP,port, hasfile);
	            peerList.add(p);
	        }
	        
	        return peerList;
	    }
	
}
