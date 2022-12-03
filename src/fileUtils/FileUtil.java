package fileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import loggers.LogWriter;


/**Contains code for Merging and Spitting the Files
 * 
 * 
 * @author himanshu
 *
 */

public class FileUtil {
		
	    // File to be merged to 
    	private final File file;
    	// Directory where files are stored
    	private final File partsDirectory;
    	
    	// Original FileName as string
    	private final String fileName;
    	
    	public FileUtil(int peerId, String fileName){
    		partsDirectory = new File("./PEER_" + peerId + "/files/parts/"+fileName);
    		partsDirectory.mkdirs();
    		this.fileName = fileName;
    		
    		//Make Downloaded File Directory
    		File dir = new File("./PEER_" + peerId + "/files/downloaded/");
    		dir.mkdirs();
            file = new File("./PEER_" + peerId + "/files/downloaded/"+ fileName);
    	}


    /* Split the file */
	   public void split(File inputFile, int partSize){
		   	FileInputStream fis;
	        String outFile;
	        FileOutputStream fos;
	        // Size of file
	        int size = (int) inputFile.length();
	        int peieceNumber = 0, readLength = partSize;
	        byte[] piece;
	        try {
	            fis = new FileInputStream(inputFile);
	            
	            for(;size > 0; peieceNumber++){
	            	
	                if (size <= partSize) {
	                    readLength = size;
	                }
	                piece = new byte[readLength];
	                size  -= fis.read(piece, 0, readLength);
	           
	                
	                outFile = partsDirectory + "/" + Integer.toString(peieceNumber);
	                fos = new FileOutputStream(new File(outFile));
	                fos.write(piece);
	                fos.flush();
	                fos.close();
	                piece = null;
	                fos = null;
	            }
	            fis.close();
	        } catch (IOException e) {
	             e.printStackTrace();
	        }
	    }

	    public byte[] getPieceAsBytes(int partId) {
	        File file = new File(partsDirectory.getAbsolutePath() + "/" + partId);
	        return readPiece(file);
	    }

	    public void writeFilePiece(byte[] part, int partId) {
	        File output = new File(partsDirectory.getAbsolutePath() + "/" + partId);
	        try(FileOutputStream fos= new FileOutputStream(output)) {
	            fos.write(part);
	            fos.flush();
	        } catch (Exception e) {
	            LogWriter.getLogWriterInstance().warning(e);
	        } 
	    }

	    private byte[] readPiece(File peice){
	         //FileInputStream fis = null;
	        try(FileInputStream fis = new FileInputStream(peice);) {
	            //fis = new FileInputStream(peice);
	            byte[] read = new byte[(int) peice.length()];
	            int bytesRead = fis.read(read, 0, (int) peice.length());
	            fis.close();
	            return read;
	        } catch (Exception e) {
	        	LogWriter.getLogWriterInstance().warning(e);
	        }
	        return null;

	    }

	    public void splitFile(int partSize){
	        split(new File(fileName), partSize);
	    }

	    public void mergeFile(int numParts) {
	        File out = file;
	        //FileOutputStream fos;
	        FileInputStream fis;
	        byte[] fileBytes;
	        int bytesRead = 0;
	        
	        // Add to List instead of single Line 
	        // File [] files = partsDirectory.listFiles()
	        // As order  can be random
	        
	        List<File> pieces = new ArrayList<>();
	        
	        for (int i = 0; i < numParts; i++) {
	        	pieces.add(new File(partsDirectory.getPath() + "/" + i));
	        }
	        
	        try(FileOutputStream fos = new FileOutputStream(out)){
	            for (File file : pieces) {
	                fis = new FileInputStream(file);
	                fileBytes = new byte[(int) file.length()];
	                bytesRead = fis.read(fileBytes, 0, (int) file.length());
	                fos.write(fileBytes);
	                fos.flush();
	                fileBytes = null;
	                fis.close();
	                fis = null;
	            }
	        } catch (Exception exception) {
	            exception.printStackTrace();
	        }
	    }	
}
