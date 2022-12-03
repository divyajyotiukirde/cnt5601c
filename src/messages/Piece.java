package messages;

import java.util.Arrays;

public class Piece extends MessageWithPayload {

    Piece (byte[] payload) {
        super (MessageType.Piece, payload);
    }

    public Piece (int pieceIdx, byte[] content) {
        super (MessageType.Piece, join (pieceIdx, content));
    }

    public byte[] getContent() {
        if ((actual == null) || (actual.length <= 4)) {
            return null;
        }
        return Arrays.copyOfRange(actual, 4, actual.length);
    }

    private static byte[] join (int pieceIdx, byte[] second) { 
        byte[] concat = new byte[4 + (second == null ? 0 : second.length)];
        System.arraycopy(getPieceIndexBytes (pieceIdx), 0, concat, 0, 4);
        System.arraycopy(second, 0, concat, 4, second.length);
        return concat;
    }
}
