/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.austunner.log;

import java.io.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
/**
 * This class allows writes to logs without prepended file/date metadata info like in log4j.
 * e.g. One can use this to log data in json form and process it later.
 * @author Austunner
 */
public class Logster {
    
    // yes, a log4j for a logger utility!
    private Logger log = Logger.getLogger(Logster.class);
    private Writer writer;
    private OutputStream fos;
    private String filePath;
    public final String NEWLINE;
    
    public Logster(String filePath) {
        if (StringUtils.isBlank(System.getProperty("line.separator"))) {
            NEWLINE = "\n";
        } else {
            NEWLINE = System.getProperty("line.separator");
        }
        
        if (StringUtils.isBlank(filePath)) {
            this.filePath = "/logs/logwriter.log";
        } else {
            this.filePath = filePath;
        }
        
        try {
            fos = new FileOutputStream(this.filePath, true);
            writer = new OutputStreamWriter(fos, "UTF-8");
        } catch (IOException ioe) {
            log.error("Exception initializing LogWriter", ioe);
            shutdown();
        } 
    }
    
    /**
     *
     * @param msg
     * @throws IOException
     */
    public void logIt(String msg){
        try {
            if (writer != null ) {
                writer.append(msg).append(NEWLINE);
                writer.flush();
            }
            
        } catch (IOException ioe) {
            try {
                // if we get ioe, try to reopen stream and append again, just one time
                fos = new FileOutputStream(filePath, true);
                writer = new OutputStreamWriter(fos, "UTF-8");
                writer.append(ioe.getLocalizedMessage());
                writer.append(msg).append(NEWLINE);
                writer.flush();
            } catch (IOException e) {
                log.error("Second attempt to log failed", e);
                shutdown();
            }
            
        }
    }
    
    public synchronized void shutdown() {
        if (writer != null) {
            try {
                log.debug("Shutting down logster");
                writer.flush();
                writer.close();
            } catch (Exception e) {
                log.error("Can't shutdown logster", e);
            }
        }
    }
    
    public static void main(String[] argv) {
        Logster logger = new Logster("");
        logger.logIt("LINE 1");
        logger.logIt("LINE 2");
    }
}
