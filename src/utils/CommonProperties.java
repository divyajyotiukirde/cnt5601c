package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * A POJO class for storing common properties
 * from Common.cfg
 * 
 * Todo - 1)Make the code generic
 *        2) Validate Read Properties
 *      
 */

public class CommonProperties {
	/** Each variable is property 
	 * which is to defined in
	 * Common.cfg
	 * 
	 */
    private int NumberOfPreferredNeighbors;
    private int UnchokingInterval;
    private int OptimisticUnchokingInterval;
    private String FileName;
	private  int FileSize;
    private int PieceSize;
    
    
    private final String[] propertiesName = {"NumberOfPreferredNeighbors","UnchokingInterval","OptimisticUnchokingInterval"
                                             ,"FileName","FileSize","PieceSize"};
    
    public int getNumberOfPreferredNeighbors() {
		return NumberOfPreferredNeighbors;
	}


	public int getUnchokingInterval() {
		return UnchokingInterval;
	}


	public int getOptimisticUnchokingInterval() {
		return OptimisticUnchokingInterval;
	}


	public String getFileName() {
		return FileName;
	}


	public int getFileSize() {
		return FileSize;
	}


	public int getPieceSize() {
		return PieceSize;
	}


	public String[] getPropertiesName() {
		return propertiesName;
	}



    /** Initializes a common property object
     *  by Reading Property from Common.cfg file
     *  
     */
    public CommonProperties(String propertiesFile) {
    	try {
		Map<String,String> pmap = read(propertiesFile);
		this.NumberOfPreferredNeighbors = Integer.parseInt(pmap.get("NumberOfPreferredNeighbors"));
		this.UnchokingInterval = Integer.parseInt(pmap.get("UnchokingInterval"));
		this.OptimisticUnchokingInterval = Integer.parseInt(pmap.get("OptimisticUnchokingInterval"));
		this.FileName = pmap.get("FileName");
		this.FileSize = Integer.parseInt(pmap.get("FileSize"));
		this.PieceSize = Integer.parseInt(pmap.get("PieceSize"));
    	}catch(Exception e) {
    		throw new RuntimeException("Cannot Read From Properties File :\n"+ e);
    	}
    	
    	
    	
    	//validateProperties();
    }
	
    
    /** Reads from Property File and Return Properties 
     * and Values in Form of a Map
     **/
    
    private Map<String,String> read(String filename) throws IOException, FileNotFoundException{
    	Map<String,String> pmap = new HashMap<String,String>();
    	File file = new File(filename);
    	
    	String line;
    	BufferedReader br  = new BufferedReader(new FileReader(file));
    	int i = 0;  // Keeping track of lines
    	while((line = br.readLine()) != null ) {
    		line = line.trim();
    		
    		  if ((line.length() <= 0)) {
                  continue;
              }
              // Seperate tokens by space
              String[] tokens = line.split("\\s+");
              if (tokens.length != 2) {
                  throw new IOException (new ParseException (line, i));
              }
           
              pmap.put(tokens[0].trim(), tokens[1].trim());
    	}
    	
    	br.close();
    	return pmap;
    }
}
