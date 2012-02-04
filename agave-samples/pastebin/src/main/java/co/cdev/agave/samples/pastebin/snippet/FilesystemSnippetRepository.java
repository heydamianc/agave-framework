/**
 * Copyright (c) 2008, Damian Carrillo
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
package co.cdev.agave.samples.pastebin.snippet;

import co.cdev.agave.samples.pastebin.repository.RetrievalException;
import co.cdev.agave.samples.pastebin.repository.StorageException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides repository support for snippets which is backed by the filesystem.
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class FilesystemSnippetRepository implements SnippetRepository {

    public static final String SNIPPET_DIR_NAME = "snippets";

    private static final int ID_LENGTH = 8;
    private static final String[] domain = new String[] {
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "_", "-"
    };
    
    private final File rootDir;

    public FilesystemSnippetRepository(final File repositoryDir) {
        this.rootDir = new File(repositoryDir, SNIPPET_DIR_NAME);
        rootDir.mkdirs();
    }

    public synchronized String generateUniqueId(final Timeframe expiration) {
        StringBuilder attempt = new StringBuilder();
        do {
            for (int i = 0; i < ID_LENGTH; i++) {
                attempt.append(domain[(int) (Math.random() * domain.length)]);
            }
        } while (Arrays.asList(rootDir.list()).contains(attempt.toString()));
        return attempt.toString();
    }

    public String determineUniqueId(Timeframe expiration) {
        String uniqueId = null;
        uniqueId = generateUniqueId(expiration);
        File snippetDir = new File(rootDir, uniqueId);
        snippetDir.mkdirs();
        return uniqueId;
    }

    public void storeSnippet(Snippet snippet) throws StorageException {
        File snippetDir = new File(rootDir, snippet.getUniqueId());
        
        long currentRevision = getCurrentRevision(snippetDir);
        currentRevision++;
        
        File snippetFile = new File(snippetDir, String.valueOf(currentRevision));
        
        try {
            snippetFile.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(snippetFile));
            out.writeObject(snippet);
            out.close();
        } catch (IOException ex) {
            String errorMsg = String.format("Unable to store version %d of snippet %s",
                    currentRevision, snippet.getUniqueId());
            throw new StorageException(errorMsg, ex);
        }
    }
    
    private long getCurrentRevision(final File snippetDir) {
        long currentRevision = 0l;
        for (String entry : snippetDir.list()) {
            if (Long.parseLong(entry) > currentRevision) {
                currentRevision = Long.parseLong(entry);
            }
        }
        return currentRevision;
    }
    
    public void discardSnippet(final Snippet snippet) {
        File snippetDir = new File(rootDir, snippet.getUniqueId());
        if (snippetDir.exists()) {
            deleteRecursively(snippetDir);
        }
    }

    private void deleteRecursively(final File node) {
        if (node.isFile() && node.canWrite()) {
            node.delete();
        } else if (node.isDirectory()) {
            for (File child : node.listFiles()) {
                deleteRecursively(child);
            }
            node.delete();
        }
    }

    public Set<Snippet> retrieveAllSnippets() throws RetrievalException {
        Set<Snippet> snippets = new HashSet<Snippet>();

        for (String snippetId : rootDir.list()) {
            snippets.add(retrieveSnippet(snippetId));
        }
        
        return snippets;
    }

    public Snippet retrieveSnippet(String snippetId) throws RetrievalException {
        Snippet snippet = null;

        File snippetDir = new File(rootDir, snippetId);
        if (snippetDir.exists() && snippetDir.canRead()) {
            long currentRevision = getCurrentRevision(snippetDir);
            snippet = retrieveSnippet(snippetId, currentRevision);
        }

        return snippet;
    }
    
    public Snippet retrieveSnippet(String snippetId, long revision) throws RetrievalException {
        Snippet snippet = null;

        File snippetDir = new File(rootDir, snippetId);
        if (snippetDir.exists() && snippetDir.canRead()) {
            File snippetFile = new File(snippetDir, String.valueOf(revision));
            if (snippetFile.exists() && snippetFile.canRead()) {
                try {
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(snippetFile));
                    snippet = (Snippet)in.readObject();
                } catch (IOException ex) {
                    throw new RetrievalException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RetrievalException(ex);
                }
            }
        }
        
        return snippet;
    }

}
