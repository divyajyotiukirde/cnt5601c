package processes;

import java.util.BitSet;

/**
 *  A Model for a PEER in the P2P System
 */


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A POJO Class for Modelling the  Peer
 * @author himan
 *
 */
public class Peer {
    
	public final int peerId;
    
	public final String IP;
    
	public final int port;
    
	public final boolean hasFile;
    
	public AtomicInteger bytesDownloaded;
    
	public BitSet recievedSegments;
    
	private final AtomicBoolean interested;

	// Constructor
	
    public Peer(int peerId , String IP, int port, boolean hasFile) {
         
    	 this.peerId = peerId;
         
    	 this.IP = IP;
         
    	 this.port = port;
         
    	 this.hasFile = hasFile;
         
    	 this.bytesDownloaded = new AtomicInteger (0);
         
    	 this.recievedSegments = new BitSet();
         
    	 this.interested = new AtomicBoolean (false);
    }
    
    // Getters and Setters
	
    public AtomicInteger getBytesDownloaded() {
		return bytesDownloaded;
	}

	public void setBytesDownloaded(int bytes) {
		this.bytesDownloaded.set(bytes);
	}

	public BitSet getRecievedSegments() {
		return recievedSegments;
	}

	public void setRecievedSegments(BitSet recievedSegments) {
		this.recievedSegments = recievedSegments;
	}

	public int getPeerId() {
		return peerId;
	}

	public String getIP() {
		return IP;
	}

	public int getPort() {
		return port;
	}

	public boolean HasFile() {
		return hasFile;
	}

	public Boolean isInterested() {
		return interested.get();
	}
	

    public void setInterested(Boolean val) {
        interested.set (val);
    }

		@Override
    	public boolean equals(Object obj) {
    		if (!(obj instanceof Peer)) return false;
    		return  ((Peer) obj).getPeerId() == this.getPeerId();
    	}
		
		@Override
		public int hashCode() {
			//Can use peer ID as hashcode
			return this.peerId;
		}
		
	    @Override
	    public String toString() {
	    	return "Peer : "+this.peerId +"  Address : "+ this.IP +"  Port : "+ this.port;
	    }
}
