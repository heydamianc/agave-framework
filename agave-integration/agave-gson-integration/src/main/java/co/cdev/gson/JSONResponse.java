package co.cdev.gson;

import co.cdev.agave.web.HTTPResponse;
import co.cdev.agave.web.StatusCode;

public class JSONResponse extends HTTPResponse {

    public JSONResponse() {
        super();
    }

    public JSONResponse(StatusCode statusCode, Object messageBody) {
        super(statusCode, "application/json", messageBody);
    }

}
