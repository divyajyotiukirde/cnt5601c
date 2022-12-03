package processes;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import loggers.EventLogger;
import loggers.LogWriter;
import messages.Choke;
import messages.Have;
import messages.NotInterested;
import messages.Unchoke;
import utils.CommonProperties;


public class Process implements Runnable, FileObserver, PeerObserver {
    
	private final Peer current;
    private final CommonProperties properties;
    private final FileHandler fileHandler;
    private final PeerHandler peerHandler;
    private final EventLogger eventLogger;
    private final AtomicBoolean downloadCompleted = new AtomicBoolean(false);
    private final AtomicBoolean peersDownloadFinished = new AtomicBoolean(false);
    private final AtomicBoolean finished = new AtomicBoolean(false);
    
    // Concurrent HashSet 
    private final Collection<ConnectionHandler> connectionHandlers =Collections.newSetFromMap(new ConcurrentHashMap<ConnectionHandler, Boolean>());

    public Process(Peer current, List<Peer> peers, CommonProperties commonprop) {
    	 this.current = current;
         this.properties = commonprop; 
         System.out.println(Thread.currentThread().getName() +"  "+peers.size());
         fileHandler = new FileHandler(current, commonprop);
         List<Peer> temp = new ArrayList<>(peers);
         peerHandler = new PeerHandler(current, temp, fileHandler.getNumberOfPieces(), commonprop);
         eventLogger = new EventLogger(current.peerId);
         downloadCompleted.set(current.hasFile);       
    }

    void setup() {
    	attachObservers();
        splitFile();
        runPeerHandler();
    }
    
    void attachObservers() {
        fileHandler.attach(this);
        peerHandler.attach(this);
    }
    
    
    
    void runPeerHandler() {
        Thread t = new Thread(peerHandler);
        t.setName(peerHandler.getClass().getName());
        t.start();
    }
    
  
    
    void splitFile() {
    	if(current.hasFile) {
            fileHandler.splitFile();
            fileHandler.setAllParts();
    	}
    }
    
    
    
    @Override
    public void run() {
        try(ServerSocket server = new ServerSocket(current.port)){
            while (!finished.get()) {
                try {
                    attachConnectionHandler(new ConnectionHandler(current, server.accept(), fileHandler, peerHandler));

                } catch (Exception e) {
                   e.printStackTrace();
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } 
    }

    void makePeerConncetions(Collection<Peer> peersToConnectTo) {
        Iterator<Peer> iter = peersToConnectTo.iterator();
        while (iter.hasNext()) {
            do {
                Socket socket = null;
                Peer peer = iter.next();
                try {
                    LogWriter.getLogWriterInstance().info(" Connecting to peer: " + peer.getPeerId()
                            + " (" + peer.IP + ":" + peer.getPort() + ")");
                    socket = new Socket(peer.IP, peer.getPort());
                    if (attachConnectionHandler(new ConnectionHandler(current, true, peer.getPeerId(),
                            socket, fileHandler, peerHandler))) {
                        iter.remove();
                        LogWriter.getLogWriterInstance().info(" Connected to peer: " + peer.getPeerId()
                                + " (" + peer.IP + ":" + peer.getPort() + ")");

                    }
                }
                catch (ConnectException ex) {
                	LogWriter.getLogWriterInstance().warning("could not connect to peer " + peer.getPeerId()
                            + " at address " + peer.IP + ":" + peer.getPort());
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException ex1)
                        {}
                    }
                }
                catch (IOException ex) {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException ex1)
                        {}
                    }
                    LogWriter.getLogWriterInstance().warning(ex);
                }
            }
            while (iter.hasNext());

            // Keep trying until they all connect
            iter = peersToConnectTo.iterator();
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
            }
        }
    }

    
    
    private synchronized boolean attachConnectionHandler(ConnectionHandler connHandler) {
        if (!connectionHandlers.contains(connHandler)) {
            connectionHandlers.add(connHandler);
            new Thread(connHandler).start();
            try {
                wait(10);
            } catch (InterruptedException e) {
                LogWriter.getLogWriterInstance().warning(e);
            }
        }
        else {
            LogWriter.getLogWriterInstance().debug("Peer " + connHandler.getRemotePeerId() + " is trying to connect but a connection already exists");
        }
        return true;
    }
    
    
    
    
    
    /**Observer Update Methods
     * 
    */
    @Override
    public void updateOnPeerDownloadFinished() {
        LogWriter.getLogWriterInstance().info("all peers completed download");
        peersDownloadFinished.set(true);
        if (downloadCompleted.get() && peersDownloadFinished.get()) {
            finished.set(true);
            System.exit(0);
        }
    }

    @Override
    public synchronized void updateOnPeersChoke(Collection<Integer> chokedPeersIds) {
        for (ConnectionHandler ch : connectionHandlers) {
            if (chokedPeersIds.contains(ch.getRemotePeerId())) {
                LogWriter.getLogWriterInstance().info("Choking " + ch.getRemotePeerId());
                ch.send(new Choke());
            }
        }
    }

    @Override
    public synchronized void updateOnPeersUnchoke(Collection<Integer> unchokedPeersIds) {
        for (ConnectionHandler ch : connectionHandlers) {
            if (unchokedPeersIds.contains(ch.getRemotePeerId())) {
                LogWriter.getLogWriterInstance().info("Unchoking " + ch.getRemotePeerId());
                ch.send(new Unchoke());
            }
        }
    }

    /* When Download is finished across all Peers */
    /* We can shutdown the program */
	@Override
	public void updateFileDownloadFinished() {
        eventLogger.downloadCompleteMessage();
        downloadCompleted.set(true);
        if (downloadCompleted.get() && peersDownloadFinished.get()) {
            finished.set(true);
            System.exit(0);
        }
	}

	@Override
	public void updateFilePartArrived(int pieceId) {
		eventLogger.pieceDownloadedMessage(current.peerId, pieceId, fileHandler.getNumberOfReceivedParts());
        for (ConnectionHandler conn : connectionHandlers) {
            conn.send(new Have(pieceId));
            if (! peerHandler.isInterested(conn.getRemotePeerId(), fileHandler.getDownloadedParts())) {
            	conn.send(new NotInterested());
            }
        }
		
	}
}
