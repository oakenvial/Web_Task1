package ru.netology;


public class Main {
    public static void main(String[] args) {
        final Server server = new Server(9999);

        server.addHandler("GET", "/messages", (request, responseStream) -> responseStream.write(("""
                HTTP/1.1 200 OK\r
                Content-Length: 0\r
                Connection: close\r
                \r
                """).getBytes()));
        server.addHandler("POST", "/messages", (request, responseStream) -> responseStream.write(("""
                HTTP/1.1 200 OK\r
                Content-Length: 0\r
                Connection: close\r
                \r
                """).getBytes()));

        server.run();
    }
}
