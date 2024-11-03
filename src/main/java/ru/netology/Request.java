package ru.netology;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;


public class Request {
    private final String method;
    private final String fullPath;
    private final List<String> headers;
    private final BufferedReader body;

    public Request(String method, String fullPath, List<String> headers, BufferedReader body) {
        this.method = method;
        this.fullPath = fullPath;
        this.headers = headers;
        this.body = body;
    }

    public List<NameValuePair> getQueryParams() {
        if (!fullPath.contains("?")) return Collections.emptyList();
        return URLEncodedUtils.parse(fullPath.split(Pattern.quote("?"))[1], Charset.defaultCharset());
    }

    public String getQueryParam(String name) {
        List<NameValuePair> queryParams = this.getQueryParams();
        if (queryParams.isEmpty()) return null;
        return queryParams.stream().filter(a -> a.getName().equals(name)).toList().getFirst().getValue();
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }
}
