package co.cdev.agave.web;

public enum StatusCode {

    // All 1xx (informational), 204 (no content), and 304 (not modified) responses MUST 
    // NOT include a message-body. All other responses do include a message-body, although 
    // it MAY be of zero length.
    //
    // See http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.3
    
    
    // 1xx: Informational - Request received, continuing process
    
    _100_Continue(100),
    _101_SwitchingProtocols(101),
    
    // 2xx: Success - The action was successfully received, understood, and accepted
    
    _200_Ok(200),
    _201_Created(201),
    _202_Accepted(200),
    _203_NonAuthoritativeInformation(203),
    _204_NoContent(204),
    _205_ResetContent(205),
    _206_PartialContent(206),
    
    // 3xx: Further action must be taken in order to complete the request
    
    _300_MultipleChoices(300),
    _301_MovedPermanently(301),
    _302_Found(302),
    _303_SeeOther(303),
    _304_NotModified(304),
    _305_UseProxy(305),
    _307_TemporaryRedirect(307),
    
    // 4xx: Client Error - The request contains bad syntax or cannot be fulfilled
    
    _400_BadRequest(400),
    _401_Unauthorized(401),
    _402_PaymentRequired(402),
    _403_Forbidden(403),
    _404_NotFound(404),
    _405_MethodNotAllowed(405),
    _406_NotAcceptable(406),
    _407_ProxyAuthenticationRequired(407),
    _408_RequestTimeout(408),
    _409_Conflict(409),
    _410_Gone(410),
    _411_LengthRequired(411),
    _412_PreconditionFailed(412),
    _413_RequestEntityTooLarge(413),
    _414_RequestURITooLong(414),
    _415_UnsupportedMediaType(415),
    _416_RequestedRangeNotSatisfiable(416),
    _417_ExpectationFailed(417),
    
    // 5xx: Server Error - The server failed to fulfill an apparently valid request
    
    _500_InternalServerError(500),
    _501_NotImplemented(501),
    _502_BadGateway(502),
    _503_ServiceUnavailable(503),
    _504_GatewayTimeout(504),
    _505_HTTPVersionNotSupported(505);

    
    private int numericCode;
    
    StatusCode(int numericCode) {
        this.numericCode = numericCode;
    }
    
    public int getNumericCode() {
        return numericCode;
    }
}
