package agave.samples;

import java.io.File;

import org.apache.commons.lang.RandomStringUtils;

import agave.AbstractHandler;
import agave.Destination;
import agave.HandlesRequestsTo;

public class StegoHandler extends AbstractHandler {

    @HandlesRequestsTo("/")
    public Destination welcome() throws Exception {
        return new Destination("/WEB-INF/jsp/index.jsp");
    }
    
    @HandlesRequestsTo("/obscure")
    public Destination obscure(StegoForm form) throws Exception {
        String filename = RandomStringUtils.randomAlphabetic(7);
        File targetLocation = new File(servletContext.getRealPath("/img/submitted/" + filename));
        form.getCarrier().renameTo(targetLocation);
        
        return new Destination("/", true);
    }
    
    @HandlesRequestsTo("/extract")
    public Destination extract(StegoForm form) throws Exception {
        return new Destination("/", true);
    }
    
}
