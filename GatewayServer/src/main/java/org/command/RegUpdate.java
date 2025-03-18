package org.command;

import java.util.Map;

public class RegUpdate implements Command {
    private final String update;

    public RegUpdate(Map<String, String> args) {
        update = args.get("update");
    }

    @Override
    public void execute() {
        System.out.println("update: " + update);
    }
}
