package messages;

import java.util.BitSet;

public class Bitfield extends Message {

    Bitfield (byte[] bitfield) {
        super (MessageType.BitField, bitfield);
    }

    public Bitfield (BitSet bitset) {
        super (MessageType.BitField, bitset.toByteArray());
    }

    public BitSet getBitSet() {
        return BitSet.valueOf (actual);
    }
}
