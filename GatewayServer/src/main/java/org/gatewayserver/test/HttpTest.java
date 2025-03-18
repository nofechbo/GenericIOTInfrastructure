package org.gatewayserver.test;

import org.gatewayserver.GatewayServer;

public class HttpTest {
    public static void main(String[] args) {
        new GatewayServer().startHttpServer();
    }
}
