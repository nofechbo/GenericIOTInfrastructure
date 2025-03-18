package org.connection.connectionservice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.google.gson.JsonObject;
import org.connection.connectionserviceutils.IConnection;
import org.connection.connectionserviceutils.Status;
import static org.parser.ByteArrToJson.convertByteArrToJson;


class UDPIConnection implements IConnection {
    private final DatagramChannel channel;
    private static final int BUFFER_SIZE = 1024;
    private InetSocketAddress address;
    private final ByteBuffer buffer;

    public UDPIConnection(DatagramChannel channel) {
        this.channel = channel;
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    @Override
    public JsonObject read() throws IOException {
        buffer.clear();
        address = (InetSocketAddress) channel.receive(buffer);
        buffer.flip();

        byte[] arr = new byte[buffer.remaining()];
        buffer.get(arr);

        return convertByteArrToJson(arr);
    }

    @Override
    public void write(String message, Status status) throws IOException {
        buffer.clear();
        String str = status.name() + " " + message;
        buffer.put(str.getBytes(), 0, str.length());

        buffer.flip();
        channel.send(buffer, address);
        buffer.clear();
    }
}
