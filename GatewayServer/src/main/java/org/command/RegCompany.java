package org.command;

import java.util.Map;

public class RegCompany implements Command{
    private final String companyName;

    public RegCompany(Map<String, String> args) {
        companyName = args.get("companyName");
    }

    @Override
    public void execute() {
        System.out.println("Company: " + companyName);
    }

}

