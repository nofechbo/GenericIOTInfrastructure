package org.connection.generichttpserver;

import org.connection.connectionserviceutils.IConnection;

import java.io.IOException;

public interface Controller {
    void handle(IConnection connection) throws IOException;
}
