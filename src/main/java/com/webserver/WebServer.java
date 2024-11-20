package com.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private final int port;
    private static final String DEFAULT_PATH = "/";

    public WebServer(int port) {
        this.port = port;
    }

    public WebServer() {
        this.port = 80; // default port number
    }

    /**
     * Method to start web server.
     */
    public void start() {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server Started.");
            System.out.println("Listening to Connections..");

            while(true) {
                Socket socket = serverSocket.accept(); //blocking call

                final BufferedReader clientInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Read the HTTP request (e.g., GET / HTTP/1.1)
                final String requestLine = clientInputReader.readLine();
                System.out.println("Request: " + requestLine);

                final String[] requestData = requestLine.split(" ");
                final String requestPath = getRequestPath(requestData);

                // Write HTTP response
                final BufferedWriter serverResponseWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                serverResponseWriter.write("HTTP/1.1 200 OK\r\n");
                serverResponseWriter.write("Content-Type: text/plain\r\n");
                serverResponseWriter.write("\r\n");  // End of headers
                serverResponseWriter.write(String.format("Requested path: %s\r\n", requestPath));
                serverResponseWriter.flush();

                // Close connection
                socket.close();
            }
        } catch (final IOException e) {
            System.err.println("Server Error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Method to extract request path from raw request.
     *
     * @param requestData array of request elements
     * @return string of request path, if empty return default path
     */
    private String getRequestPath(final String[] requestData) {
        if(requestData.length > 2) {
            return requestData[1];
        }
        return DEFAULT_PATH;
    }
}
