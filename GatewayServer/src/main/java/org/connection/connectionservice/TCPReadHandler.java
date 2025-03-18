package org.connection.connectionservice;

import java.nio.channels.SocketChannel;

import org.connection.connectionserviceutils.EventHandler;

class TCPReadHandler implements Handler {
    private final EventHandler eventHandler;
    private final SocketChannel channel;

    public TCPReadHandler(EventHandler eventHandler, SocketChannel channel) {
        this.eventHandler = eventHandler;
        this.channel = channel;
    }

    @Override
    public void handle() {
        TCPIConnection connection = new TCPIConnection(channel);
        eventHandler.onRead(connection);
    }
}
