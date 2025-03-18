package org.command;

import java.util.Map;

public class RegProduct implements Command {
    private final String productName;

    public RegProduct(Map<String, String> args) {
        productName = args.get("productName");
    }

    @Override
    public void execute() {
        System.out.println("product: " + productName);
    }
}

