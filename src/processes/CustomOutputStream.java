package processes;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

import messages.Handshake;
import messages.Message;

public class CustomOutputStream extends DataOutputStream implements ObjectOutput {

    public CustomOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void writeObject (Object obj) throws IOException {
        if (obj instanceof Handshake) {
            ((Handshake) obj).writeToStream(this);
        }
        else if (obj instanceof Message) {
            ((Message) obj).writeToStream (this);
        }
    }
}
