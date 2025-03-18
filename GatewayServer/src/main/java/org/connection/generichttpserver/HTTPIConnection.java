package org.connection.generichttpserver;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import org.connection.connectionserviceutils.IConnection;
import org.connection.connectionserviceutils.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.parser.ByteArrToJson.convertByteArrToJson;

public class HTTPIConnection implements IConnection {
    private final HttpExchange exchange;

    public HTTPIConnection(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public JsonObject read() throws IOException {
        InputStream input = exchange.getRequestBody();
        int inputLen = input.available();

        byte[] request = new byte[inputLen];

        int bytesRead = input.read(request, 0, inputLen);

        if (bytesRead != inputLen){
            throw new IOException("OH NO!");
        }

        return convertByteArrToJson(request);
    }

    @Override
    public void write(String message, Status status) throws IOException {
        exchange.sendResponseHeaders(status.getStatusCode(), message.length());
        OutputStream response = exchange.getResponseBody();
        response.write(message.getBytes());
        response.close();
    }
}
