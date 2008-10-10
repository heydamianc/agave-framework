package agave.samples;

import java.io.File;

public class StegoForm {
    
    private String payload;
    private File carrier;
    
    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
    
    public void setCarrier(File carrier) {
        this.carrier = carrier;
    }

    public File getCarrier() {
        return carrier;
    }
    
}
