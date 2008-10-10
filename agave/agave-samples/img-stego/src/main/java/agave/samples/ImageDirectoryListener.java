package agave.samples;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.File;

public class ImageDirectoryListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        File dir = new File(event.getServletContext().getRealPath("/img/submitted/"));
        
        if (!dir.exists()) {
                dir.mkdirs();
        }
    }
    
    public void contextDestroyed(ServletContextEvent event) {
        
    }
    
}
