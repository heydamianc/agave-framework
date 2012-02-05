package co.cdev.agave.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class FileSystemTest {

    private FileFilter fileFilter;
    private File rootDir;
    
    @Before
    public void setUp() throws URISyntaxException {
        fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".txt");
            }
        };
        
        rootDir = new File(getClass().getResource("/").toURI());
    }
    
    @Test
    public void testFilterFiles_withNullRootDir() throws Exception {
        Collection<File> filteredFiles = FileSystem.filterFiles(null, fileFilter);
        
        assertNotNull(filteredFiles);
        assertTrue(filteredFiles.isEmpty());
    }
    
    @Test
    public void testFilterFiles_withNullFileFilter() throws Exception {
        Collection<File> filteredFiles = FileSystem.filterFiles(rootDir, null);
        
        assertNotNull(filteredFiles);
        assertTrue(filteredFiles.isEmpty());
    }
    
    @Test
    public void testFilterFiles() throws Exception {
        Collection<File> filteredFiles = FileSystem.filterFiles(rootDir, fileFilter);
        
        assertTrue(filteredFiles.size() >= 3);
        assertTrue(filteredFiles.contains(new File(rootDir, "1.txt")));
        assertTrue(filteredFiles.contains(new File(new File(rootDir, "a"), "2.txt")));
        assertTrue(filteredFiles.contains(new File(new File(new File(rootDir, "a"), "b"), "3.txt")));
    }
    
}
