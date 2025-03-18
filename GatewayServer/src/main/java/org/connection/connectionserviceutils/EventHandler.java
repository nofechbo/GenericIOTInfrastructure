package org.connection.connectionserviceutils;

public interface EventHandler {
    boolean onAccept(IConnection connection);
    void onRead(IConnection connection);
    void onWrite(IConnection connection, String message, Status status);
}
