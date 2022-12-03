package messages;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import processes.CustomSerializable;


public class Message implements CustomSerializable  {

    private int len;
    private final MessageType mtype;
    byte[] actual;

    Message (MessageType type) {
        this (type, null);
    }

    Message (MessageType type, byte[] content) {
        len = (content == null ? 0 : content.length)+ 1;
        mtype = type;
        actual = content;
    }

    public MessageType getType() {
        return mtype;
    }

    @Override
    public void readFromStream (DataInputStream dstream_in) throws IOException {
        if ((actual != null) && (actual.length) > 0) {
            dstream_in.readFully(actual, 0, actual.length);
        }
    }

    @Override
    public void writeToStream (DataOutputStream dstream_out) throws IOException {
        dstream_out.writeInt (len);
        dstream_out.writeByte (mtype.getValue());
        if ((actual != null) && (actual.length > 0)) {
            dstream_out.write (actual, 0, actual.length);
        }
    }
}
