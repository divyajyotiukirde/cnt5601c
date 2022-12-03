package messages;

import java.io.IOException;

public class MessageInstanceGetter {

	  public static Message getInstance (int length, MessageType type) throws ClassNotFoundException, IOException {
	        switch (type) {
	            case Choke:
	                return new Choke();

	            case Unchoke:
	                return new Unchoke();

	            case Interested:
	                return new Interested();

	            case NotInterested:
	                return new NotInterested();

	            case Have:
	                return new Have (new byte[length]);

	            case BitField:
	                return new Bitfield (new byte[length]);

	            case Request:
	                return new Request (new byte[length]);

	            case Piece:
	                return new Piece (new byte[length]);

	            default:
	                throw new ClassNotFoundException ("message type not handled: " + type.toString());
	        }
	    }
}
