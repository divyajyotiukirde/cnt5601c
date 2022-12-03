package processes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import loggers.EventLogger;
import utils.CommonProperties;

class OptimisticUnchoker extends Thread {
	
			
			// Number of peers to unchoke
	        private final int unchokeInterval;
	        //Choked peers
	        private final List<Peer> chokedPeers = new ArrayList<>();
	        // Unchoked peers 
	        final Collection<Peer> unchoked_optimistic_peers =
	                Collections.newSetFromMap(new ConcurrentHashMap<Peer, Boolean>());
	        
	        private final List<PeerObserver> observers;
	        
	        private final EventLogger eventLogger;

	        OptimisticUnchoker(CommonProperties properties, List<PeerObserver> observers, EventLogger eventLogger) {
	            //super("OptimisticUnchoker");
	            this.observers = observers;
	            this.eventLogger = eventLogger;
	            unchokeInterval = properties.getOptimisticUnchokingInterval()*1000;
	        }

	        synchronized void addToChokedList(Collection<Peer> chokedPeers) {
	            this.chokedPeers.clear();
	            this.chokedPeers.addAll(chokedPeers);
	        }

	        @Override
	        public void run() {
	           
	        	while (true) {
	                try {
	                    Thread.sleep(unchokeInterval);
	                } catch (Exception e) {
	                }

	                synchronized (this) {
	                   
	                    if (chokedPeers.size() != 0) {
	                        
	                    	Collections.shuffle(chokedPeers);
	                        unchoked_optimistic_peers.clear();
	                        unchoked_optimistic_peers.addAll(chokedPeers.subList(0,
	                                Math.min(1, chokedPeers.size())));
	                    }
	                }
                	
	                List<Integer> ids = new ArrayList<>();
                	unchoked_optimistic_peers.forEach(peer-> ids.add(peer.peerId));
	               if (unchoked_optimistic_peers.size() > 0) {
	                   eventLogger.changeOfOptimisticallyUnchokedNeighbour(ids.get(0));
	                }	                
	                observers.forEach(observer -> observer.updateOnPeersUnchoke(ids));
	            }
	        }
	    }