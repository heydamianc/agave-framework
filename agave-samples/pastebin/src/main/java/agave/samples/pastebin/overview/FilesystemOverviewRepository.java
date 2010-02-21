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
package agave.samples.pastebin.overview;

import agave.samples.pastebin.repository.RepositoryException;
import agave.samples.pastebin.repository.RetrievalException;
import agave.samples.pastebin.repository.StorageException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class FilesystemOverviewRepository implements OverviewRepository {

    public static final String OVERVIEW_FILENAME = "overview";

    private final File overviewFile;
    
    public FilesystemOverviewRepository(final File repositoryDir) throws RepositoryException {
        this.overviewFile = new File(repositoryDir, OVERVIEW_FILENAME);
        if (!overviewFile.exists()) {
            try {
                overviewFile.createNewFile();
                Overview overview = new Overview();
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(overviewFile));
                out.writeObject(overview);
            } catch (IOException ex) {
                throw new RepositoryException(ex);
            }
        }
    }
    
    public synchronized Overview retrieveOverview() throws RetrievalException {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(overviewFile));
            return (Overview)in.readObject();
        } catch (IOException ex) {
            throw new RetrievalException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RetrievalException(ex);
        }
    }

    public synchronized void storeOverview(Overview overview) throws StorageException {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(overviewFile));
            out.writeObject(overview);
        } catch (IOException ex) {
            throw new StorageException(ex);
        }
    }

}