package co.cdev.agave.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import co.cdev.agave.Part;

public class FilesystemMultipartParser extends AbstractMultipartParser {

    public FilesystemMultipartParser(HttpServletRequest request) throws IOException {
        super(request);
    }

    protected boolean readPart(String prefix, String suffix, Part part) throws IOException {
        boolean end = false;
    
        File temporaryFile = File.createTempFile(prefix, suffix);
        temporaryFile.deleteOnExit();
        
        FileOutputStream out = new FileOutputStream(temporaryFile);
        
        CoupledLine line = null;
        while ((line = readCoupledLine(in)) != null) {
            String text = line.characters.toString().trim();
            
            if (eos.equals(text)) {
                end = true;
                break;
            }
            
            if (boundary.equals(text)) {
                break;
            }
            
            for (Byte b : line.bytes) {
                out.write(b);
            }
        }
        
    	out.flush();
        out.close();
        part.setContents(temporaryFile);
        parts.put(part.getName(), part);
        
        temporaryFile = null;
        
        return end;
    }

}
