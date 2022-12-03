package processes;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import fileUtils.FileUtil;
import utils.CommonProperties;


/**A class for managing the Files 
 * Downloaded on Local Peers
 * 
 * This is observable class.  Process 
 * class is it's observer
 * 
 * @author Himanshu 
 *
 */
public class FileHandler {

	// A BitSet for Maintaining the Parts
	private BitSet downloadedParts;
	
	// FileObservers(Process in our case) 
	private final List<FileObserver> observers = new ArrayList<>();
	
	// The CommonProperties
	private CommonProperties properties;
	
	// The current Peer
	private Peer peer;
	
	//FileUtil Object
	private FileUtil fileUtil;
	
	
	// Total Number of Pieces to be downloaded
	// This is also the size of BitVector
	private int numberOfPieces;

	
	FileHandler(Peer peer, CommonProperties properties){
		this.properties = properties;
		this.peer = peer;
		this.numberOfPieces = (int) Math.ceil(properties.getFileSize()*1.0/ properties.getPieceSize());
		System.out.println("From FileHandler"+numberOfPieces);
		downloadedParts = new BitSet(numberOfPieces);
		fileUtil = new FileUtil(peer.peerId, properties.getFileName());
	}
	
	
	    // Write a part to file
	 	// Synchronized as many have access to same fileHandler Object
		// Only 1 fileHandler Object is created
	   public synchronized void writePiece (int part_id, byte[] part) {
	        
		    //Check if index bitset is already set.
		   //If yes the file exists
		   //If no write the file
		    final boolean isNew = !downloadedParts.get(part_id);
	        downloadedParts.set (part_id);
	        
	        //Check if all bits are recieved
	        // Notify the observers
	        if (isNew) {
	            fileUtil.writeFilePiece(part, part_id);
	            observers.forEach(observer -> observer.updateFilePartArrived(part_id));
	            }
	        
	        //Check if wholeFile is complete and 
	        //notify the observer
	        if(hasrecievedAllParts()) {
	            fileUtil.mergeFile(downloadedParts.cardinality());
	            observers.forEach(observer -> observer.updateFileDownloadFinished());
	        }
	    }
			
	

	 /*** Returns whether all parts are recieved or not
	  * 
	  * @return true if finished
	  */
	   public boolean hasrecievedAllParts() {
		   for(int i=0;i<numberOfPieces;i++) {
			   if(!downloadedParts.get(i)) return false;
		   }
		   return true;
	   }
	   
	   /**Get parts to request from a Peer
	    * Select A random Index from parts not recieved
	    * 
	    * @param bitsetAvailableFromPeer
	    * @return
	    */
	   public synchronized int getPartToRequest(BitSet bitsetAvailableFromPeer ) {
		   	bitsetAvailableFromPeer.andNot((BitSet) downloadedParts.clone());
	        return pickRandomIndex (bitsetAvailableFromPeer);
	    }
	   
	    /**
	     *  Return if requested peice is present locally
	     */
	    public synchronized boolean hasPiece(int pieceIndex) {
	        return downloadedParts.get(pieceIndex);
	    }
	   
	   /**Get a random Index from set elements in Bitset
	    * 
	    * @param bs
	    * @return
	    */
	    public int pickRandomIndex(BitSet bs) {
	        String set = bs.toString(); 
	        String[] indices = set.substring(1, set.length()-1).split(",");
	        return Integer.parseInt(indices[(int)(Math.random()*(indices.length-1))].trim());
	    }
	
	    /**Setting All Parts to True
	     * 
	     */
	    public synchronized void setAllParts()
	    {
	    	downloadedParts.set(0, numberOfPieces,true);
	    }
	    /**Gets the currently set element in Bitset
	     * This is equal to number of pieces recieved
	     * 
	     * @return
	     */
	    public synchronized int getNumberOfReceivedParts() {
	        return downloadedParts.cardinality();
	    }
	    
	    /**  
	     *
	     * @return copy of  downloaded biset
	     */
	    public synchronized BitSet getDownloadedParts() {
	        return (BitSet) downloadedParts.clone();
	    }

        /**Get the a piece of file of given partId
         * 
         * @param partId
         * @return
         */
	    byte[] getPiece (int partId) {
	        byte[] piece = fileUtil.getPieceAsBytes(partId);
	        return piece;
	    }

	    public void attach (FileObserver fileObserver) {
	        observers.add (fileObserver);
	    }
	    
	    /**
	     * If has file. Then split the file into pieces
	     */
	    public void splitFile(){
	        fileUtil.splitFile(properties.getPieceSize());
	    }
	    
	    /**
	     * 
	     * @return The Number of Pieces of File
	     */
		public int getNumberOfPieces() {
			return numberOfPieces;
		}

}
