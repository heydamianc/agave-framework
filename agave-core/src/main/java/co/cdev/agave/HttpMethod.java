package co.cdev.agave;

import java.io.Serializable;

public enum HttpMethod implements Serializable {

    GET,
    PUT,
    POST,
    DELETE,
    HEAD,
    OPTIONS,
    ANY;

    public boolean matches(HttpMethod method) {
        return this == ANY || method == ANY || this == method;
    }
}
