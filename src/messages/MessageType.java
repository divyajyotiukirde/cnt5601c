package messages;


public enum MessageType {
    Choke ((byte) 0),
    Unchoke ((byte) 1),
    Interested ((byte) 2),
    NotInterested ((byte) 3),
    Have ((byte) 4),
    BitField ((byte) 5),
    Request ((byte) 6),
    Piece ((byte) 7);

    private final byte identifier;
    
    MessageType (byte type) {
        identifier = type;
    }

    public byte getValue() {
        return identifier;
    }

    public static MessageType valueOf (byte b) {
        for (MessageType t : MessageType.values()) {
            if (t.identifier == b) {
                return t;
            }
        }
        throw new IllegalArgumentException();
    }
}
