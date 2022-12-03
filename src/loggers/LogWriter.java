package loggers;

import java.io.IOException;

import java.util.logging.FileHandler;

import java.util.logging.Formatter;

import java.util.logging.Handler;

import java.util.logging.Level;

import java.util.logging.Logger;

import java.util.logging.SimpleFormatter;


/**
 * A Singleton Class For writing logs to file
 * 
 * 
 */

public class LogWriter {
	
	private static final LogWriter logWriter = new LogWriter (Logger.getLogger("P2P"));
	
	private final Logger logger;
	
	static {
		 System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %5$s%6$s%n");
		}
    
	private LogWriter (Logger logger) {
        this.logger = logger;
    }
	
	/*Invoke this method when the  process starts */
	/* It will set logger properties*/
    public static void initialize(int peerId) throws SecurityException, IOException {
        
    	Handler handler = new FileHandler ("log_peer_" + peerId + ".log");
        
    	Formatter formatter = new SimpleFormatter();

        handler.setFormatter(formatter);
        handler.setLevel(Level.ALL);
        logWriter.logger.addHandler(handler);
    }

    public static LogWriter getLogWriterInstance () {
        return logWriter;
    }
    
    public synchronized void debug (String msg) {
        logger.log(Level.FINE, msg);
    }

    public synchronized void info (String msg) {
        logger.log (Level.INFO, msg);
    }
    
    public synchronized void warning (String msg) {
        logger.log(Level.WARNING, msg);
    }
    
    public synchronized void warning (Throwable e) {
        logger.log(Level.WARNING, e.getMessage());
    }

    public synchronized void severe (Throwable e) {
        logger.log(Level.SEVERE, e.getMessage());
    }

}

