package processes;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;

import messages.Handshake;
import messages.Message;
import messages.MessageInstanceGetter;
import messages.MessageType;


public class CustomInputStream extends DataInputStream implements ObjectInput {

    private boolean handShakeRcvd = false;

    public CustomInputStream(InputStream in) {
        super(in);
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException{ 
        if (!handShakeRcvd) {
            Handshake handshake = new Handshake();
            handshake.readFromStream(this);
            handShakeRcvd = true;
            return handshake;
        }
        else {
       
        	 int messageLength = readInt();
             int payloadLength = messageLength - 1;
             byte idenitity  = readByte();
             Message message = MessageInstanceGetter.getInstance(payloadLength, MessageType.valueOf (idenitity));
             message.readFromStream(this);
             return message;
        }
    }
}
