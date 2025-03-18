package org.gatewayserver;

import com.google.gson.JsonObject;
import org.connection.connectionserviceutils.EventHandler;
import org.connection.connectionserviceutils.IConnection;
import org.connection.connectionserviceutils.Status;

public class Message {
    private final EventHandler handler;
    private final IConnection connection;
    private final JsonObject request;

    public Message(JsonObject request, IConnection connection, EventHandler handler) {
        this.request = request;
        this.connection = connection;
        this.handler = handler;
    }

    public JsonObject getRequest() {
        return request;
    }

    public void sendFeedback(String message, Status status){
        handler.onWrite(connection, message, status);
    }

}