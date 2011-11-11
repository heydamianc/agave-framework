package co.cdev.agave.samples;

import co.cdev.agave.Converter;
import co.cdev.agave.Part;

public class ReformatForm {
    
    private Part document;
    private String indentation;
    private boolean newlines;


    public Part getDocument() {
        return document;
    }

    public void setDocument(Part document) {
        this.document = document;
    }

    public String getIndentation() {
        return indentation;
    }

    public void setIndentation(@Converter(IndentationParamConverter.class) String indentation) {
        this.indentation = indentation;
    }

    public boolean isNewlines() {
        return newlines;
    }

    public void setNewlines(boolean newlines) {
        this.newlines = newlines;
    }
    
}
