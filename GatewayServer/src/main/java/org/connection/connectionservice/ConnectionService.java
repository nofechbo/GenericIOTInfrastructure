package org.connection.connectionservice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;

import org.connection.connectionserviceutils.EventHandler;

public class ConnectionService {
        private final Selector selector;
        private volatile boolean isWorking;
        private final EventHandler handler;

        public ConnectionService(EventHandler handler) throws IOException {
            this.handler = handler;
            selector = Selector.open();
            isWorking = true;
        }

        public void addTCPConnection(String ipAddress, int port) throws IOException {
            ServerSocketChannel TCPChannel = ServerSocketChannel.open();
            TCPChannel.bind(new InetSocketAddress(ipAddress, port));
            TCPChannel.configureBlocking(false);
            TCPChannel.register(selector, SelectionKey.OP_ACCEPT, new TCPAcceptHandler(TCPChannel, selector, handler));
        }

        public void addUDPConnection(String ipAddress, int port) throws IOException {
            DatagramChannel UDPChannel = DatagramChannel.open();
            UDPChannel.bind(new InetSocketAddress(ipAddress, port));
            UDPChannel.configureBlocking(false);
            UDPChannel.register(selector, SelectionKey.OP_READ, new UDPReadHandler(UDPChannel, handler));
        }

        public void start() throws IOException {
            while (isWorking) {
                if (selector.select() == 0) {
                    continue;
                }

                for (SelectionKey key : selector.selectedKeys()) {
                    ((Handler)key.attachment()).handle();
                }
                selector.selectedKeys().clear();
            }
        }

        public void stop() {
            isWorking = false;
        }
}
