package processes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public interface CustomSerializable {

	public void writeToStream (DataOutputStream out) throws IOException;
    public void readFromStream (DataInputStream in) throws IOException;
    

}
