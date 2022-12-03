package utils;

import java.util.List;

import processes.Peer;

public class PropertiesLoaderTest {
  
	public static void main(String[] args) {
	  
	  CommonProperties cp =  new CommonProperties(Constants.COMMON_CONFIG_FILE);
	  
	  System.out.println(cp.getFileName());
	  
	  System.out.println(cp.getFileSize());
	  
	  System.out.println(cp.getNumberOfPreferredNeighbors());
	  
	  /******************/
	  
	  PeerInfoLoader ploader = new PeerInfoLoader();
	  
	  List<Peer> pList = ploader.load(Constants.PEER_INFO_FILE);
	  
	  for(Peer p :pList) {
		  System.out.println(p);
	  }
   }
}
