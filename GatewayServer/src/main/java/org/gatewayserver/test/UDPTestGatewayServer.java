package org.gatewayserver.test;

import java.io.IOException;
import java.net.*;


public class UDPTestGatewayServer {
    private static final int BUFFER_SIZE = 1024;
    private static final int PORT = 12346;
    private static DatagramSocket client;

    private static void receive() {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            client.receive(packet);
            System.out.println(new String(packet.getData(), packet.getOffset(), packet.getLength()).trim());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        client = new DatagramSocket();
        InetAddress address = InetAddress.getByName("localhost");
        byte[] buffer;
        DatagramPacket packet;

        String req = "{ \"command\": \"RegCompany\",\n" +
                "        \"args\": {\n" +
                "            \"companyName\": \"Apple\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

        req = "{ \"command\": \"RegCompany\",\n" +
                "        \"args\": {\n" +
                "            \"companyName\": \"Microsoft\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

        req = "{ \"command\": \"RegCompany\",\n" +
                "        \"args\": {\n" +
                "            \"companyName\": \"Sony\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

            //System.out.println("\n Products:: ");
        req = "{ \"command\": \"RegProduct\",\n" +
                "        \"args\": {\n" +
                "            \"productName\": \"iphone\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

        req = "{ \"command\": \"RegProduct\",\n" +
                "        \"args\": {\n" +
                "            \"productName\": \"xbox\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

        req = "{ \"command\": \"RegProduct\",\n" +
                "        \"args\": {\n" +
                "            \"productName\": \"smartTV\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

            //System.out.println("\n Devices: ");
        req = "{ \"command\": \"RegDevice\",\n" +
                "        \"args\": {\n" +
                "            \"deviceName\": \"Tzur's_iphone\",\n" +
                "            \"deviceOwner\": \"TzurB\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

        req = "{ \"command\": \"RegDevice\",\n" +
                "        \"args\": {\n" +
                "            \"deviceName\": \"Elad's_pc\",\n" +
                "            \"deviceOwner\": \"EladZ\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

        req = "{ \"command\": \"RegDevice\",\n" +
                "        \"args\": {\n" +
                "            \"deviceName\": \"Simon's_smartWatch\",\n" +
                "            \"deviceOwner\": \"SimonG\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

            //System.out.println("\n Updates: ");
        req = "{ \"command\": \"RegUpdate\",\n" +
                "        \"args\": {\n" +
                "            \"update\": \"charge_battery\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

        req = "{ \"command\": \"RegUpdate\",\n" +
                "        \"args\": {\n" +
                "            \"update\": \"gas_low\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

        //should return REQUEST_NOT_FOUND
        req = "{ \"command\": \"regUpdate\",\n" +
                "        \"args\": {\n" +
                "            \"update\": \"new_update_available\"\n" +
                "        }\n" +
                "}\n";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();

        //should return BAD_REQUEST
        req = "{ \"command\": \"regUpdate\"\n}";
        buffer = req.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
        receive();
    }


}
