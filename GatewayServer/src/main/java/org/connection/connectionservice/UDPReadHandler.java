package org.connection.connectionservice;

import java.nio.channels.DatagramChannel;

import org.connection.connectionserviceutils.EventHandler;

class UDPReadHandler implements Handler {
    private final EventHandler eventHandler;
    private final DatagramChannel channel;

    public UDPReadHandler(DatagramChannel channel, EventHandler handler) {
        eventHandler = handler;
        this.channel = channel;
    }

    @Override
    public void handle() {
        eventHandler.onRead(new UDPIConnection(channel));
    }
}
