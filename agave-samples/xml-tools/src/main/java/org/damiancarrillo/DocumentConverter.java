package org.damiancarrillo;

import agave.Part;
import agave.conversion.PartConverter;
import agave.exception.ConversionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

public class DocumentConverter implements PartConverter<Document> {
    public Document convert(Part part, Locale locale) throws ConversionException {
        Document document = null;

        File documentFile = part.getContents();
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
                throw new ConversionException("Unable to parse XML file", ex);
            }
            catch (IOException ex) {
                throw new ConversionException("Unable to read XML file", ex);
            }
        }

        return document;
    }
}
