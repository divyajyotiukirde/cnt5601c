package processes;



import java.util.BitSet;

import loggers.EventLogger;
import loggers.LogWriter;
import messages.Bitfield;
import messages.Handshake;
import messages.Have;
import messages.Interested;
import messages.Message;
import messages.NotInterested;
import messages.Piece;
import messages.Request;


public class MessageHandler {

    private boolean isChoked;
    private final int otherPeer;
    private final FileHandler fileHandler;
    private final PeerHandler peerHandler;
    private final EventLogger eventLogger;

    MessageHandler(int otherPeerId, FileHandler fileHandler, PeerHandler peerHandler, EventLogger evtLogger) {
        this.isChoked = true;
        this.fileHandler = fileHandler;
        this.peerHandler = peerHandler;
        this.otherPeer = otherPeerId;
        this.eventLogger = evtLogger;
    }

    public Message handle(Handshake handshake) {
        BitSet bitset = fileHandler.getDownloadedParts();
        if (!bitset.isEmpty()) {
            return (new Bitfield(bitset));
        }
        return null;
    }

    public Message handle(Message msg) {
        switch (msg.getType()) {
            case Choke: {
                isChoked = true;
                eventLogger.choked(otherPeer);
                return null;
            }
            case Unchoke: {
                isChoked = false;
                eventLogger.unChoked(otherPeer);                
                return requestPiece();
            }
            case Interested: {
                eventLogger.interestedMessage(otherPeer);
                peerHandler.markInterested(otherPeer);
                return null;
            }
            case NotInterested: {
                eventLogger.notInterestedMessage(otherPeer);
                peerHandler.markUnInterested(otherPeer);
                return null;
            }
            case Have: {
                Have have = (Have) msg;
                final int pieceId = have.getPieceIndex();
                eventLogger.haveMessage(otherPeer, pieceId);
                peerHandler.checkArrived(otherPeer, pieceId);

                if (fileHandler.getDownloadedParts().get(pieceId)) {
                    return new NotInterested();
                } else {
                    return new Interested();
                }
            }
            case BitField: {
                Bitfield bitfield = (Bitfield) msg;
                BitSet bitset = bitfield.getBitSet();
                peerHandler.arrived_bitfield(otherPeer, bitset);

                bitset.andNot(fileHandler.getDownloadedParts());
                if (bitset.isEmpty()) {
                    return new NotInterested();
                } else {
                    // the peer has parts that this peer does not have
                    return new Interested();
                }
            }
            case Request: {
                Request request = (Request) msg;
                if (peerHandler.isAbleToUpload(otherPeer)) {
                    byte[] piece = fileHandler.getPiece(request.getPieceIndex());
                    if (piece != null) {
                        return new Piece(request.getPieceIndex(), piece);
                    }
                }
                return null;
            }
            case Piece: {
                Piece piece = (Piece) msg;
                fileHandler.writePiece(piece.getPieceIndex(), piece.getContent());
                peerHandler.receivedPart(otherPeer, piece.getContent().length);
                eventLogger.pieceDownloadedMessage(otherPeer, piece.getPieceIndex(), fileHandler.getNumberOfReceivedParts());
                return requestPiece();
            }
        }

        return null;
    }

    private Message requestPiece() {
        if (!isChoked) {
            int partId = fileHandler.getPartToRequest(peerHandler.getReceivedParts(otherPeer));
            if (partId >= 0) {
                LogWriter.getLogWriterInstance().info("Requesting part " + partId + " to " + otherPeer);
                return new Request (partId);
            }
            else {
                LogWriter.getLogWriterInstance().info("No parts can be requested to " + otherPeer);
            }
        } 
        return null;
    }
}

