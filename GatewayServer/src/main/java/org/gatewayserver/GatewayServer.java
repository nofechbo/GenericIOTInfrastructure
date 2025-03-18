package org.gatewayserver;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.connection.connectionservice.ConnectionService;
import org.connection.connectionserviceutils.EventHandler;
import org.connection.connectionserviceutils.IConnection;
import org.connection.connectionserviceutils.Status;
import org.connection.generichttpserver.Controller;
import org.connection.generichttpserver.GenericHTTPServer;
import org.rps.RPS;

public class GatewayServer {
    private final RPS rps = new RPS();
    private ConnectionService connectionService;
    private GenericHTTPServer httpServer;
    private final ServerEventHandler serverEventHandler = new ServerEventHandler();

    public void startServer(String address, int TCPPort, int UDPPort)  {
        try {
            connectionService = new ConnectionService(serverEventHandler);
            connectionService.addTCPConnection(address, TCPPort);
            connectionService.addUDPConnection(address, UDPPort);
            connectionService.start();
        } catch (IOException e) {
            connectionService = null;
            throw new RuntimeException(e);
        }
    }
    public void startHttpServer() {
        try {
            httpServer = new GenericHTTPServer();
            httpServer.addRoute("/iots", new HTTPController());
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class ServerEventHandler implements EventHandler {
        @Override
        public boolean onAccept(IConnection connection) {
            return true;
        }

        @Override
        public void onRead(IConnection connection) {
            JsonObject json;
            try {
                json = connection.read();
                if (json == null) {
                    return;
                }
            } catch (IOException e) {
                return;
            }

            Message request = new Message(json, connection, this);
            rps.handleRequest(request);
        }

        @Override
        public void onWrite(IConnection connection, String message, Status status) {
            try {
                connection.write(message, status);
            } catch (IOException e) {
                return;
            }
        }
    }

    class HTTPController implements Controller {
        @Override
        public void handle(IConnection connection) {
            serverEventHandler.onRead(connection);
        }
    }

    public void stopServer(){
        connectionService.stop();
    }
    public void stopHttpServer() {
        httpServer.stop();
    }
}

