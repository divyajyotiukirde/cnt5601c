package loggers;

import java.util.ArrayList;
import java.util.List;

/**Tests if Logging Module is working correctly or not
 * 
 * Create a log file with peerId 0.
 * and Logs various messages.
 * 
 * Todo --> Write JUnit Rather than Explicit LoggerTest
 * 
 */
class TestFile{
	public static void main(String [] args) {
		
		LogWriter logWriter = LogWriter.getLogWriterInstance();
		
		try {
			logWriter.initialize(0);
		  } 
		catch(Exception e) 
			{
				throw new RuntimeException();
			}
	
		EventLogger evt = new EventLogger(0);
		
		evt.initiateConnection(1);
		
		evt.establishedConnection(1);
		
		List<Integer> pref = new ArrayList<Integer>();
		
		pref.add(2); pref.add(3); pref.add(4);
		
		evt.changeOfPreferedNeighbours(pref);
		
		evt.changeOfOptimisticallyUnchokedNeighbour(5);
		
		evt.choked(2);
		
		evt.unChoked(4);
		
		evt.haveMessage(1,100);
		
		evt.interestedMessage(3);
		
		evt.notInterestedMessage(4);
		
		evt.pieceDownloadedMessage(1, 100, 4);
		
		evt.downloadCompleteMessage();
  }
	
}
