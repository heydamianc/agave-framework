package org.damiancarrillo;

import agave.ConvertWith;
import agave.Part;

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

    public void setIndentation(@ConvertWith(IndentationConverter.class) String indentation) {
        this.indentation = indentation;
    }

    public boolean isNewlines() {
        return newlines;
    }

    public void setNewlines(boolean newlines) {
        this.newlines = newlines;
    }
    
}
