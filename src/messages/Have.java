package messages;

public class Have extends MessageWithPayload {

    Have (byte[] pieceIdx) {
        super (MessageType.Have, pieceIdx);
    }

    public Have (int pieceIdx) {
        this (getPieceIndexBytes (pieceIdx));
    }
}
