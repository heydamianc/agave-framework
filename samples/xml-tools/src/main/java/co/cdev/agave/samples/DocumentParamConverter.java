package co.cdev.agave.samples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import co.cdev.agave.Part;
import co.cdev.agave.conversion.AgaveConversionException;
import co.cdev.agave.conversion.PartParamConverter;

public class DocumentParamConverter implements PartParamConverter<Document, File> {
    
    @Override
    public Document convert(Part<File> part, Locale locale) throws AgaveConversionException {
        Document document = null;

        File documentFile = ((Part<File>) part).getContents();
        if (documentFile.canRead() && documentFile.exists()) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(documentFile));
                StringBuilder documentContents = new StringBuilder();
                String line = null;
                while ((line = in.readLine()) != null) {
                    documentContents.append(line);
                }
                document = DocumentHelper.parseText(documentContents.toString());
            }
            catch (DocumentException ex) {
                throw new AgaveConversionException("Unable to parse XML file", ex);
            }
            catch (IOException ex) {
                throw new AgaveConversionException("Unable to read XML file", ex);
            }
        }

        return document;
    }
    
}
