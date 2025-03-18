package org.gatewayserver.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static java.lang.Thread.sleep;

public class TCPTestGatewayServer {
    private static final int BUFFER_SIZE = 1024;
    private static SocketChannel client;
    private static volatile boolean isLoggedIn = true;

    private static void receive() {
        try {
            ByteBuffer received = ByteBuffer.allocate(BUFFER_SIZE);
            int bytesRead = client.read(received);
            if (bytesRead > 0) {
                received.flip();
                String reply = new String(received.array(), 0, received.limit()).trim();
                System.out.println("received: " + reply);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stopListeningThread() {
        isLoggedIn = false;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost", 12345));
        client.configureBlocking(false);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        Thread listeningThread = new Thread(() -> {
            while (isLoggedIn) {
                receive();
            }
        });
        listeningThread.start();

        //System.out.println("\n Companys: ");
        buffer.clear();
        String req = "{ \"command\": \"RegCompany\",\n" +
                "        \"args\": {\n" +
                "            \"companyName\": \"Apple\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(100);

        buffer.clear();
        req = "{ \"command\": \"RegCompany\",\n" +
                "        \"args\": {\n" +
                "            \"companyName\": \"Microsoft\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(100);

        buffer.clear();
        req = "{ \"command\": \"RegCompany\",\n" +
                "        \"args\": {\n" +
                "            \"companyName\": \"Sony\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(100);

            //System.out.println("\n Products:: ");
        buffer.clear();
        req = "{ \"command\": \"RegProduct\",\n" +
                "        \"args\": {\n" +
                "            \"productName\": \"iphone\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(10);

        buffer.clear();
        req = "{ \"command\": \"RegProduct\",\n" +
                "        \"args\": {\n" +
                "            \"productName\": \"xbox\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(10);

        buffer.clear();
        req = "{ \"command\": \"RegProduct\",\n" +
                "        \"args\": {\n" +
                "            \"productName\": \"smartTV\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(10);

            //System.out.println("\n Devices: ");
        buffer.clear();
        req = "{ \"command\": \"RegDevice\",\n" +
                "        \"args\": {\n" +
                "            \"deviceName\": \"Tzur's_iphone\",\n" +
                "            \"deviceOwner\": \"TzurB\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(10);

        buffer.clear();
        req = "{ \"command\": \"RegDevice\",\n" +
                "        \"args\": {\n" +
                "            \"deviceName\": \"Elad's_pc\",\n" +
                "            \"deviceOwner\": \"EladZ\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(10);

        buffer.clear();
        req = "{ \"command\": \"RegDevice\",\n" +
                "        \"args\": {\n" +
                "            \"deviceName\": \"Simon's_smartWatch\",\n" +
                "            \"deviceOwner\": \"SimonG\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(10);

            //System.out.println("\n Updates: ");
        buffer.clear();
        req = "{ \"command\": \"RegUpdate\",\n" +
                "        \"args\": {\n" +
                "            \"update\": \"charge_battery\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(10);

        buffer.clear();
        req = "{ \"command\": \"RegUpdate\",\n" +
                "        \"args\": {\n" +
                "            \"update\": \"gas_low\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(10);

        //should return REQUEST_NOT_FOUND
        buffer.clear();
        req = "{ \"command\": \"regUpdate\",\n" +
                "        \"args\": {\n" +
                "            \"update\": \"new_update_available\"\n" +
                "        }\n" +
                "}\n";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(10);

        //should return BAD_REQUEST
        buffer.clear();
        req = "{ \"command\": \"regUpdate\"\n}";
        buffer.put(req.getBytes(), 0, req.length());
        buffer.flip();
        client.write(buffer);
        sleep(10);

        sleep(100);
        stopListeningThread();
        listeningThread.join();
    }
}
