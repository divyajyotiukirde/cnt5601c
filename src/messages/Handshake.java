package messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

import processes.CustomSerializable;


public class Handshake implements CustomSerializable  {
    private final static String _protocolId = "P2PFILESHARINGPROJ";
    private final byte[] _zeroBits = new byte[10];
    private final byte[] _peerId = new byte[4];

    public Handshake() {
    }

    public Handshake (int peerId) {
        this (ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(peerId).array());
    }

    private Handshake (byte[] peerId) {
        if (peerId.length > 4) {
            throw new ArrayIndexOutOfBoundsException("peerId max length is 4, while "
                    + Arrays.toString (peerId) + "'s length is "+ peerId.length);
        }
        int i = 0;
        for (byte b : peerId) {
            _peerId[i++] = b;    
        }
    }

    @Override
    public void writeToStream(DataOutputStream oos) throws IOException {
        byte[] peerId = _protocolId.getBytes(Charset.forName("US-ASCII"));
        if (peerId.length > _protocolId.length()) {
            throw new IOException("protocol id length is " + peerId.length + " instead of " + _protocolId.length());
        }
        oos.write (peerId, 0, peerId.length);
        oos.write(_zeroBits, 0, _zeroBits.length);
        oos.write(_peerId, 0, _peerId.length);
    }

    @Override
    public void readFromStream (DataInputStream ois) throws IOException {
        // Read and check protocol Id
        byte[] protocolId = new byte[_protocolId.length()];
        if (ois.read(protocolId, 0, _protocolId.length()) < _protocolId.length()) {
            throw new ProtocolException ("protocol id is " + Arrays.toString (protocolId) + " instead of " + _protocolId);
        }
        if (!_protocolId.equals (new String(protocolId, "US-ASCII"))) {
            throw new ProtocolException ("protocol id is " + Arrays.toString (protocolId) + " instead of " + _protocolId);
        }

        // Read and check zero bits
        if (ois.read(_zeroBits, 0, _zeroBits.length) <  _zeroBits.length) {
            throw new ProtocolException ("zero bit bytes read are less than " + _zeroBits.length);
        }

        // Read and check peer id
        if (ois.read(_peerId, 0, _peerId.length) <  _peerId.length) {
            throw new ProtocolException ("peer id bytes read are less than " + _peerId.length);
        }
    }

    public int getPeerId() {
        return ByteBuffer.wrap(_peerId).order(ByteOrder.BIG_ENDIAN).getInt();
    }
}
