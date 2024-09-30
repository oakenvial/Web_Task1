package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class Server {
    private final int THREADS_NUM = 64;
    private final int port;
    private final Logger logger;
    private final Map<String, Handler> getHandlers;
    private final Map<String, Handler> postHandlers;

    public Server(int port) {
        this.port = port;
        this.logger = Logger.getLogger("ServerLog");
        this.getHandlers = new HashMap<>();
        this.postHandlers = new HashMap<>();
    }

    public void run() {
        try (final ServerSocket serverSocket = new ServerSocket(9999)) {
            logger.info("Server is running on port " + port);
            try (ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_NUM)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    logger.info("New connection received");
                    threadPool.submit(processConnection(socket));
                }
            } catch (Exception e) {
                logger.severe("Error while executing task in a thread pool: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.severe("Error while running server: " + e.getMessage());
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        switch (method) {
            case "GET":
                getHandlers.put(path, handler);
                break;
            case "POST":
                postHandlers.put(path, handler);
                break;
            default:
                logger.severe("Method " + method + " is not supported for handlers");
        }
    }

    private Runnable processConnection(Socket socket) {
        return () -> {
            try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream())); final var out = new BufferedOutputStream(socket.getOutputStream())) {

                // request line must be in form GET /path HTTP/1.1
                final var requestLine = in.readLine();
                logger.info("Request line received: " + requestLine);
                final var parts = requestLine.split(" ");
                if (parts.length != 3) {
                    logger.warning("Request line must have 3 parts. Incorrect request line: " + requestLine);
                    return;
                }
                final String method = parts[0];

                // process headers
                final List<String> headers = new ArrayList<>();
                String line = in.readLine();
                while (line != null && !line.trim().isEmpty()) {
                    headers.add(line);
                    logger.info(line);
                    line = in.readLine();
                }

                // process body
                final BufferedReader body = in;

                final String path = parts[1];
                Request request = new Request(method, headers, body);

                switch (method) {
                    case "GET":
                        if (getHandlers.containsKey(path)) {
                            getHandlers.get(path).handle(request, out);
                            out.flush();
                        } else {
                            logger.severe("Unknown path: " + path);
                        }
                        break;
                    case "POST":
                        if (postHandlers.containsKey(path)) {
                            postHandlers.get(path).handle(request, out);
                            out.flush();
                        } else {
                            logger.severe("Unknown path: " + path);
                        }
                        break;
                    default:
                        logger.severe("Method not supported for handlers: " + method);
                }
            } catch (Exception e) {
                logger.severe("Error while processing tasks: " + e.getMessage());
            }
        };
    }
}
