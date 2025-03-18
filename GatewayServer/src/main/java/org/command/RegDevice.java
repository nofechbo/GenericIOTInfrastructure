package org.command;

import java.util.Map;

public class RegDevice implements Command {
    private final String DeviceName;
    private final String DeviceOwner;

    public RegDevice(Map<String, String> args) {
        DeviceName = args.get("deviceName");
        DeviceOwner = args.get("deviceOwner");
    }

    @Override
    public void execute() {
        System.out.println("device: " + DeviceName + ", owner: " + DeviceOwner);
    }
}
