/*
 * Copyright (c) 2011, Damian Carrillo
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of 
 *     conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *     conditions and the following disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *   * Neither the name of the copyright holder's organization nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software without specific 
 *     prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package co.cdev.agave.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SingleLineFormatter extends SimpleFormatter {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    private DateFormat dateFormat;
    
    public SingleLineFormatter() {
        String format = System.getProperty("java.util.logging.dateFormat", "yyyy-MM-dd HH:mm:ss");
        dateFormat = new SimpleDateFormat(format);
    }

    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder formattedRecord = new StringBuilder();

        formatDate(record, formattedRecord);
        formatClassName(record, formattedRecord);
        formatThreadID(record, formattedRecord);
        formatMethodName(record, formattedRecord);
        formatLevel(record, formattedRecord);
        formatMessage(record, formattedRecord);
        formatThrown(record, formattedRecord);
        
        return formattedRecord.toString();
    }
    
    private void formatDate(LogRecord record, StringBuilder formattedRecord) {
        formattedRecord.append(dateFormat.format(record.getMillis()));
    }
    
    private void formatClassName(LogRecord record, StringBuilder formattedRecord) {
        if (record.getSourceMethodName() != null) {
            formattedRecord.append(" ");
            formattedRecord.append(record.getSourceClassName());
        }
    }
    
    private void formatThreadID(LogRecord record, StringBuilder formattedRecord) {
        formattedRecord.append("[");
        formattedRecord.append(record.getThreadID());
        formattedRecord.append("]");
    }
    
    private void formatMethodName(LogRecord record, StringBuilder formattedRecord) {
        if (record.getSourceMethodName() != null) {
            formattedRecord.append(".");
            formattedRecord.append(record.getSourceMethodName());
        }
    }
    
    private void formatLevel(LogRecord record, StringBuilder formattedRecord) {
        formattedRecord.append(" ");
        formattedRecord.append(record.getLevel().getLocalizedName());
        formattedRecord.append(" - ");
    }
    
    private void formatMessage(LogRecord record, StringBuilder formattedRecord) {
        formattedRecord.append(formatMessage(record));
        formattedRecord.append(LINE_SEPARATOR);
    }
    
    private void formatThrown(LogRecord record, StringBuilder formattedRecord) {
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                formattedRecord.append(sw.toString());
            } catch (Exception ex) {
                // do nothing
            }
        }
    }
    
    public static void applyToRootLogger() {
        Logger rootLogger = Logger.getLogger("");
        
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
        
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SingleLineFormatter());
        
        rootLogger.addHandler(consoleHandler);
    }
    
}