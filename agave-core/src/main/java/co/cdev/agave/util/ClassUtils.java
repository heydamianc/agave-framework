package co.cdev.agave.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ClassUtils {

    private ClassUtils() {
        
    }
    
    public static String getClassNameForFile(File file, String suffix, File basisDirectory) {
        List<String> parts = new ArrayList<String>();
        parts.add(file.getName().replace(suffix, ""));
        
        File dir = file.getParentFile();
        
        while (!dir.equals(basisDirectory)) {
            parts.add(0, dir.getName());
            dir = dir.getParentFile();
        }
        
        StringBuilder className = new StringBuilder();
        
        Iterator<String> itr = parts.iterator();
        while (itr.hasNext()) {
            className.append(itr.next());
            
            if (itr.hasNext()) {
                className.append(".");
            }
        }
        
        return className.toString();
    }
    
    public static String getClassNameForClassFile(File classFile, File basisDirectory) {
        return getClassNameForFile(classFile, ".class",  basisDirectory);
    }
    
    public static String getClassNameForJavaFile(File javaFile, File basisDirectory) {
        return getClassNameForFile(javaFile, ".java", basisDirectory);
    }
    
}
