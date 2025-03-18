package org.connection.connectionserviceutils;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface IConnection {
    JsonObject read() throws IOException;
    void write(String message, Status status) throws IOException;
}
