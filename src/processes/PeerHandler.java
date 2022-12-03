package processes;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import loggers.EventLogger;
import loggers.LogWriter;
import utils.CommonProperties;



public class PeerHandler implements Runnable {
	    private final CommonProperties properties;
	    private final int numberOfPieces;
	    private final int unchokeDuration;
	    private final int nprefPeers;
	    private final EventLogger evt;
	    private final List<Peer> peers = new ArrayList<>();
	    private final Collection<Peer> preferredPeers = new HashSet<>();
	    private final OptimisticUnchoker unchoker;
	    private final List<PeerObserver> observers = new ArrayList<>();
	    private final AtomicBoolean beginRandom = new AtomicBoolean(false);

	    PeerHandler(Peer current, List<Peer> peers, int numberOfPieces, CommonProperties properties) {
	        this.peers.addAll(peers);
	        this.properties = properties;
	        this.numberOfPieces = numberOfPieces;
	        this.unchokeDuration = properties.getOptimisticUnchokingInterval()*1000;
	        this.nprefPeers = properties.getNumberOfPreferredNeighbors();
	        this.evt = new EventLogger(current.peerId);
	        this.unchoker = new OptimisticUnchoker(properties,observers,evt);
	    }

	    
	    
	    public int getUnchokeDuration() {
			return unchokeDuration;
		}



		synchronized Peer findPeerById(int peerId) {
	        for (Peer peer : peers) {
	            if (peer.getPeerId() == peerId) {
	                return peer;
	            }
	        }
	        LogWriter.getLogWriterInstance().warning("Peer " + peerId + " not found");
	        return null;
	    }
	    
	    
	    synchronized void markInterested(int peerId) {
	        Peer peer = findPeerById(peerId);
	        if (peer != null) {
	            peer.setInterested(true);
	        }
	    }

	    synchronized void markUnInterested(int peerId) {
	        Peer peer = findPeerById(peerId);
	        if (peer != null) {
	            peer.setInterested(false);
	        }
	    }

	    synchronized List<Peer> getInterestedPeers() {
	        List<Peer> interestedPeers = new ArrayList<>();
	        for (Peer peer : peers){
	            if(peer.isInterested()){
	                interestedPeers.add(peer);
	            }
	        }
	        return interestedPeers;
	    }

	    synchronized boolean isInterested(int peerId, BitSet bitset) {
	        Peer peer  = findPeerById(peerId);
	        if (peer != null) {
	            BitSet peerbits = (BitSet) peer.recievedSegments.clone();
	            peerbits.andNot(bitset);
	            return ! peerbits.isEmpty();
	        }
	        return false;
	    }

	    synchronized void receivedPart(int peerId, int size) {
	        Peer peer  = findPeerById(peerId);
	        if (peer != null) {
	            peer.bytesDownloaded.addAndGet(size);
	        }
	    }

	    synchronized boolean isAbleToUpload(int peerId) {
	        Peer peer = findPeerById(peerId);
	        return (preferredPeers.contains(peer) || unchoker.unchoked_optimistic_peers.contains(peer));
	    }

	    synchronized void fileCompleted() {
	        beginRandom.set (true);
	    }

	    synchronized void arrived_bitfield(int peerId, BitSet bitfield) {
	        Peer peer  = findPeerById(peerId);
	        if (peer != null) {
	            peer.setRecievedSegments(bitfield);
	        }
	        peerDownloadFinished();
	    }

	    synchronized void checkArrived(int peerId, int partId) {
	        Peer peer  = findPeerById(peerId);
	        if (peer != null) {
	            peer.recievedSegments.set(partId);
	        }
	        peerDownloadFinished();
	    }

	    synchronized BitSet getReceivedParts(int peerId) {
	        Peer peer  = findPeerById(peerId);
	        if (peer != null) {
	            return (BitSet) peer.getRecievedSegments().clone();
	        }
	        return new BitSet(); 
	    }


	    synchronized private void peerDownloadFinished() {
	        for (Peer peer : peers) {
	            if (peer.getRecievedSegments().cardinality() < numberOfPieces) {
	                // at least one neighbor has not completed
	                LogWriter.getLogWriterInstance().debug("Peer " + peer.getPeerId() + " has not completed yet");
	                return;
	            }
	        }
	        observers.forEach(observer -> observer.updateOnPeerDownloadFinished());
	        
	    }

	    public synchronized void attach(PeerObserver observer) {
	        observers.add(observer);
	    }

	    @Override
	    public void run() {

	        unchoker.start();

	        while (true) {
	            try {
	                Thread.sleep(unchokeDuration);
	            } catch (InterruptedException ex) {
	            }

	            //Get Interested peer and sort them randomly or by order of preference

	            List<Peer> interestedPeers = getInterestedPeers();
	            if (beginRandom.get()) {
	                LogWriter.getLogWriterInstance().debug("selecting preferred peers randomly");
	                Collections.shuffle(interestedPeers);
	            }
	            else {
	            	Comparator<Peer> cmp = (a,b)-> a.getBytesDownloaded().get() - b.getBytesDownloaded().get();
	                Collections.sort(interestedPeers, cmp.reversed());
	            }

	            Collection<Peer> optUnchokablePeers = null;
	            Collection<Integer> chokedPeersIDs = new HashSet<>();
	            Collection<Integer> preferredNeighborsIDs = new HashSet<>();

	            synchronized (this) {                 
	                peers.forEach(peer -> peer.bytesDownloaded.set(0));
	                // Select preferred peers
	                preferredPeers.clear();
	                preferredPeers.addAll(interestedPeers.subList(0, Math.min(nprefPeers, interestedPeers.size())));
	                if (preferredPeers.size() > 0) {
	                	List<Integer> peerIds = new ArrayList<Integer>();
	                	preferredPeers.forEach(p -> peerIds.add(p.peerId));
	                    evt.changeOfPreferedNeighbours(peerIds);
	                }

	                Collection<Peer> chokedPeers = new ArrayList<>(peers);
	                chokedPeers.removeAll(preferredPeers);
	                chokedPeers.forEach(peer -> chokedPeersIDs.add(peer.peerId));

	                if (nprefPeers >= interestedPeers.size()) {
	                    optUnchokablePeers = new ArrayList<>();
	                }
	                else {
	                    optUnchokablePeers = interestedPeers.subList(properties.getNumberOfPreferredNeighbors(), interestedPeers.size());
	                }
	                preferredPeers.forEach(peer -> preferredNeighborsIDs.add(peer.peerId));
	            }

	            for (PeerObserver observer : observers) {
	            	observer.updateOnPeersChoke(chokedPeersIDs);
	            	observer.updateOnPeersUnchoke(preferredNeighborsIDs);
	            }
	         
	            if (optUnchokablePeers != null) {
	                unchoker.addToChokedList(optUnchokablePeers);
	            }
	        }
	        
	    }
	}

