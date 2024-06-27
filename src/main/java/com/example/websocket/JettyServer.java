package com.example.websocket;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class JettyServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8000); // Port number

        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(MyWebSocketServer .class);
            }
        };

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.setHandler(wsHandler);

        server.setHandler(context);
        server.start();
        server.join();
    }
}
