package loggers;

import java.util.List;

/**A class for Logging all the events 
 * 
 * 
 * @author himan
 *
 */
public class EventLogger {

	private final LogWriter writer;
	private final int peerId;
	private final String frontAppender;
	
	/**Construct an Event Logger
	 * 
	 * @param peerId
	 */
	public EventLogger(int peerId) {
		this.peerId = peerId;
		this.writer = LogWriter.getLogWriterInstance();
		this.frontAppender=": Peer "+ peerId;
	}
	
	/*
	 * Logging for event "Initiating TCP Connection" 
	 */
	public void initiateConnection(int peerId) {
		String message = this.frontAppender +" makes a connection to Peer "+ peerId;
		writer.info(message);
	}
	
	/*
	 * Logging for Event  TCP Connection Established 
	 */
	public void establishedConnection(int peerId) {
		String message = this.frontAppender +" is connected from Peer "+ peerId;
		writer.info(message);
	}
	
	/*
	 * Logging for event Change in List of Perferred Neigbours
	 */
    public void changeOfPreferedNeighbours(List<Integer> neigbourIds) {
    	  String pref = "";
    	  for(int i :neigbourIds) pref = pref + i+", ";
    	  pref = pref.substring(0,pref.length()-2); //Remove the last comma and space
          String message = this.frontAppender + " has preferred neighbors "+pref;
          writer.info(message);
    }
    
    /*
     * Logging for event: Change of OptimisticallyUnchoked Neighbour
     */
    public void changeOfOptimisticallyUnchokedNeighbour(int peerId) {
    	String message = this.frontAppender+" has the optimistically unchoked neighbor "+peerId;
    	writer.info(message);
    }
	
    /*
     * Logging for event: Peer is unchoked
     */
    public void unChoked(int peerId) {
    	String message = this.frontAppender+" is unchoked by "+ peerId;
    	writer.info(message);
    }
    
    /*
     * Logging for event: Peer is choked 
     */
    public void choked(int peerId) {
    	String message = this.frontAppender+" is choked by "+ peerId;
    	writer.info(message);
    }
    
    /*
     * Logging for event: Recieving Have Message
     */
    public void haveMessage (int peerId, int pieceId) {
        String message = this.frontAppender + " received the 'have' message from "+peerId +" for the piece "+pieceId;
        writer.info(message);
    }
	
    /*
     * Logging for event: Interested Message Recieved
     */
    public void interestedMessage (int peerId) {
         String message = this.frontAppender + " received the 'interested' message from "+ peerId;
         writer.info(message);
    }
    
    /*
     * Logging for event: Uniterested message recieved 
     */
    public void notInterestedMessage (int peerId) {
        String message = this.frontAppender + " received the 'not interested' message from "+ peerId;
        writer.info(message);
    }

    /*
     * Logging for event: When a piece is downloaded 
     */
    public void pieceDownloadedMessage (int peerId, int pieceId, int numPieces) {
        String message = this.frontAppender+ " has downloaded the piece "+ pieceId+" from peer "+ peerId
        					+" Now the number of pieces it has is "+ numPieces;
        writer.info(message);
    }


    /*
     * Logging for event: File Download is finished
     */
    public void downloadCompleteMessage () {
        String message = this.frontAppender+ " has downloaded the complete file";
        writer.info(message);
    }
}
