package co.cdev.agave.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.HashSet;

public final class FileSystem {

    private FileSystem() {
        
    }
    
    public static Collection<File> filterFiles(File rootDir, FileFilter fileFilter) {
        Collection<File> filteredFiles = new HashSet<File>();
        
        if (rootDir != null && fileFilter != null) {
            filterFiles(rootDir, fileFilter, filteredFiles);
        }
        
        return filteredFiles;
    }
    
    private static void filterFiles(File node, FileFilter fileFilter, Collection<File> filteredFiles) {
        if (node.isDirectory()) {
            for (File subNode : node.listFiles()) {
                filterFiles(subNode, fileFilter, filteredFiles);
            }
        } else if (node.isFile() && fileFilter.accept(node)) {
            filteredFiles.add(node);
        }
    }
    
}
