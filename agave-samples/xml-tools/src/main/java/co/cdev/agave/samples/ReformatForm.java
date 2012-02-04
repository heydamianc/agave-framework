package co.cdev.agave.samples;

import java.io.File;

import co.cdev.agave.Converter;
import co.cdev.agave.Part;

public class ReformatForm {
    
    private Part<File> document;
    private String indentation;
    private boolean newlines;


    public Part<File> getDocument() {
        return document;
    }

    public void setDocument(Part<File> document) {
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
