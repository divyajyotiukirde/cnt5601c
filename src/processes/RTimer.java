/*package processes;


import java.io.IOException;
import java.util.TimerTask;

import loggers.LogWriter;
import messages.Message;
import messages.Request;


public class RTimer extends TimerTask {
    
	private final Request request;
    private final FileHandler fileHandler;
    private final  CustomOutputStream out;
    private final int externalpeerId;
    private final Message message;

    RTimer(Request request, FileHandler filehandler, CustomOutputStream out, Message message, int externalpeerId) {
        super();
         this.request = request;
        this.fileHandler = filehandler;
        this.out = out;
        this.externalpeerId = externalpeerId;
        this.message = message;
    }

    @Override
    public void run() {
        if (!fileHandler.hasPiece(request.getPieceIndex())) {
            try {
                out.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}*/
