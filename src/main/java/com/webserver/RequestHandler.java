package com.webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private static final String WEB_DIRECTORY = "/Coding-Challenges-Web-Server/src/www";
    private static final String DEFAULT_PATH = "/";
    private static final String ROOT = "/";

    private Socket socket;

    public RequestHandler(final Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            final BufferedReader clientInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final BufferedWriter serverResponseWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Read the HTTP request (e.g., GET / HTTP/1.1)
            final String requestLine = clientInputReader.readLine();
            System.out.println("Request: " + requestLine);
            System.out.println("Connection Id: " + Thread.currentThread().getId());
            Thread.sleep(20000);
            final String[] requestData = requestLine.split(" ");
            final String requestPath = ROOT.equals(getRequestPath(requestData)) ? "/index.html" : getRequestPath(requestData);

            final File file = new File(WEB_DIRECTORY + requestPath);

            if (file.exists() && file.isFile()) {
                buildServerResponse(serverResponseWriter, ResponseCode.OK);
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        serverResponseWriter.write(line);
                        serverResponseWriter.newLine();
                    }
                } catch (IOException e) {
                    throw e;
                }
            } else {
                buildServerResponse(serverResponseWriter, ResponseCode.NOT_FOUND);
            }

            serverResponseWriter.flush();

            clientInputReader.close();
            serverResponseWriter.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to build HTTP response message.
     *
     * @param responseCode response code to send to client
     */
    private void buildServerResponse(final BufferedWriter serverResponseWriter, final ResponseCode responseCode) throws IOException {
        serverResponseWriter.write(String.format("HTTP/1.1 %s %s\r\n", responseCode.getCode(), responseCode.getDescription()));
        serverResponseWriter.write("\r\n");  // End of headers
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
