package com.webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {

    private static final int DEFAULT_PORT = 80;
    private static final int MAX_CONN = 100;
    private final int port;

    public WebServer(int port) {
        this.port = port;
    }

    public WebServer() {
        this.port = DEFAULT_PORT;
    }

    /**
     * Method to start web server.
     */
    public void start() {
        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CONN); // Thread pool to handle clients
        try(final ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server Started.");
            System.out.println("Listening to Connections..");

            while(true) {
                Socket socket = serverSocket.accept();
                threadPool.submit(new RequestHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
