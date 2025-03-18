package org.rps;

import java.util.Map;
import java.util.NoSuchElementException;

import org.command.*;
import org.connection.connectionserviceutils.Status;
import org.factory.Factory;
import org.gatewayserver.Message;
import org.parser.JSONParser;
import org.threadpool.ThreadPool;

public class RPS {
    private final ThreadPool pool = new ThreadPool();
    private final Factory<String, Map<String, String>, Command> factory = new Factory<>();

    public RPS() {
        factory.add("RegCompany", RegCompany::new);
        factory.add("RegProduct", RegProduct::new);
        factory.add("RegDevice", RegDevice::new);
        factory.add("RegUpdate", RegUpdate::new);
    }

    public void handleRequest(Message message) {
        pool.submit(() -> {
            Map<String, String> requestArgs = new JSONParser().parse(message.getRequest());
            if (requestArgs == null) {
                message.sendFeedback("invalid request", Status.BAD_REQUEST);
                return null;
            }
            getCommand(requestArgs, message);
            return null;
        });
    }

    private void getCommand(Map<String, String> args, Message message) {
        try {
            Command command = factory.create(args.get("command"), args);
            pool.submit(()-> {
                command.execute();
                return null;
            });
        } catch (NoSuchElementException e) {
            message.sendFeedback("no such request", Status.REQUEST_NOT_FOUND);
            return;
        }

        message.sendFeedback("request fulfilled", Status.SUCCESS);
    }
}
