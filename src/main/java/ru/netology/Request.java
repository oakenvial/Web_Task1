package ru.netology;

import java.io.BufferedReader;
import java.util.List;

public class Request {
    private String method;
    private List<String> headers;
    private BufferedReader body;


    public Request(String method, List<String> headers, BufferedReader body) {
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public BufferedReader getBody() {
        return body;
    }

    public void setBody(BufferedReader body) {
        this.body = body;
    }
}
