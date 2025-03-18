package org.connection.connectionservice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.google.gson.JsonObject;
import org.connection.connectionserviceutils.IConnection;
import org.connection.connectionserviceutils.Status;
import static org.parser.ByteArrToJson.convertByteArrToJson;


class TCPIConnection implements IConnection {
    private final SocketChannel channel;
    private static final int BUFFER_SIZE = 1024;
    private final ByteBuffer buffer;

    public TCPIConnection(SocketChannel channel) {
        this.channel = channel;
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    @Override
    public JsonObject read() throws IOException { //same as server?
        buffer.clear();
        int bytesRead = channel.read(buffer);

        // Handle disconnection
        if (bytesRead == -1) {
            channel.close();
            return null;
        }

        // Prepare data for reading
        buffer.flip();

        byte[] arr = new byte[buffer.remaining()];
        buffer.get(arr);

        return convertByteArrToJson(arr);
    }

    @Override
    public void write(String message, Status status) throws IOException{
        buffer.clear();
        String str = status.name() + " " + message;
        buffer.put(str.getBytes(), 0, str.length());

        buffer.flip();
        channel.write(buffer);
        buffer.clear();
    }
}
