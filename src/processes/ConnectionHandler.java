package processes;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import loggers.EventLogger;
import loggers.LogWriter;
import messages.Handshake;
import messages.Message;



public class ConnectionHandler implements Runnable {
	
	   	private static final int peer_id_init = -1;
	    private final Peer current;
	    private final Socket socket;
	    private final CustomOutputStream out;
	    private final FileHandler fileHandler;
	    private final PeerHandler peerHandler;
	    private final boolean isConnectingPeer;
	    private final int expectedPeerId;
	    private final AtomicInteger peerToConnect;
	    
	    // Thread Safe Queue
	    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

	    public ConnectionHandler(Peer current, Socket socket, FileHandler fileHandler, PeerHandler peerHandler)
	            throws IOException {
	        this(current, false, -1, socket, fileHandler, peerHandler);
	    }

	    public ConnectionHandler(Peer current, boolean isConnectingPeer, int expectedPeerId,
	                             Socket socket, FileHandler fileHandler, PeerHandler peerhandler) throws IOException {
	        this.socket = socket;
	        this.current = current;
	        this.isConnectingPeer = isConnectingPeer;
	        this.expectedPeerId = expectedPeerId;
	        this.fileHandler = fileHandler;
	        this.peerHandler = peerhandler;
	        this.out = new CustomOutputStream(socket.getOutputStream());
	        this.peerToConnect = new AtomicInteger(peer_id_init);
	    }

	    public int getRemotePeerId() {
	        return peerToConnect.get();
	    }

	    @Override
	    public void run() {
	        new Thread() {

	            private boolean peerToConnectIsChoked = true;

	            @Override
	            public void run() {
	                while (true) {
	                    try {
	                        final Message message = queue.take();
	                        if (message == null) {
	                            continue;
	                        }
	                        if (peerToConnect.get() != peer_id_init) {
	                            switch (message.getType()) {
	                                case Choke: {
	                                    if (peerToConnectIsChoked) {
	                                        peerToConnectIsChoked = true;
	                                        sendInternal(message);
	                                    }
	                                    break;
	                                }

	                                case Unchoke: {
	                                    if (peerToConnectIsChoked) {
	                                        peerToConnectIsChoked = false;
	                                        sendInternal(message);
	                                    }
	                                    break;
	                                }

	                                default:
	                                    sendInternal(message);
	                            }
	                        } else {
	                            LogWriter.getLogWriterInstance().debug("cannot send message of type "
	                                    + message.getType() + " because the remote peer has not handshaked yet.");
	                        }
	                    } catch (IOException ex) {
	                        LogWriter.getLogWriterInstance().warning(ex);
	                    } catch (InterruptedException ex) {
	                    }
	                }
	            }
	        }.start();

	        try {
	            final CustomInputStream in = new CustomInputStream(socket.getInputStream());
	            out.writeObject(new Handshake(current.peerId));
	            Handshake rcvdHandshake = (Handshake) in.readObject();
	            peerToConnect.set(rcvdHandshake.getPeerId());
	            Thread.currentThread().setName(getClass().getName() + "-" + peerToConnect.get());
	            final EventLogger eventLogger = new EventLogger(current.peerId);
	            final MessageHandler msgHandler = new MessageHandler(peerToConnect.get(), fileHandler, peerHandler, eventLogger);
	            if (isConnectingPeer && (peerToConnect.get() != expectedPeerId)) {
	                throw new Exception("Remote peer id " + peerToConnect + " does not match with the expected id: " + expectedPeerId);
	            }

	            eventLogger.establishedConnection(peerToConnect.get());

	            sendInternal(msgHandler.handle(rcvdHandshake));
	            while (true) {
	                try {
	                    sendInternal(msgHandler.handle((Message) in.readObject()));
	                } catch (Exception ex) {
	                    LogWriter.getLogWriterInstance().warning(ex);
	                    break;
	                }
	            }
	        } catch (Exception ex) {
	            LogWriter.getLogWriterInstance().warning(ex+ "From ConncetionHanlder Line 128 ");
	        } finally {
	            try {
	                socket.close();
	            } catch (Exception e) {
	            }
	        }
	        LogWriter.getLogWriterInstance().warning(Thread.currentThread().getName()
	                + " terminating, messages will no longer be accepted.");
	    }

	    @Override
	    public boolean equals(Object obj) {
	        if (obj instanceof ConnectionHandler) {
	            return ((ConnectionHandler) obj).peerToConnect == peerToConnect;
	        }
	        return false;
	    }

	    @Override
	    public int hashCode() {
	    	return current.peerId;
	    }

	    public void send(final Message message) {
	        queue.add(message);
	    }

	    private synchronized void sendInternal(Message message) throws IOException {
	        if (message != null) {
	            out.writeObject(message);
	          /*  switch (message.getType()) {
	                case Request: {
	                    new java.util.Timer().schedule(new RTimer((Request) message, fileHandler, out, message, peerToConnect.get()),peerHandler.getUnchokeDuration() * 2);
	                }
	            }*/
	        }
	    }
	}



