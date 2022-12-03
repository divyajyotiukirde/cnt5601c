package messages;


public class Request extends MessageWithPayload {

    Request (byte[] pieceIdx) {
        super (MessageType.Request, pieceIdx);
    }

    public Request (int pieceIdx) {
        this (getPieceIndexBytes (pieceIdx));
    }
}
