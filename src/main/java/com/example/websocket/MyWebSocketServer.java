package com.example.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

@WebSocket
public class MyWebSocketServer {
    private static long counter = 0;
    private static final long MAX_IDLE_TIME = 5000;
    private static final ConcurrentHashMap<Session, Long> prevActivities = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger logger = LoggerFactory.getLogger(MyWebSocketServer.class);

    static long checkInterval = 1000;

    static {
        scheduler.scheduleAtFixedRate(MyWebSocketServer::checkIdleClients, checkInterval, checkInterval, TimeUnit.MILLISECONDS);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        prevActivities.put(session, System.currentTimeMillis());
        try {
            session.getRemote().sendString("Hello Client");
        } catch (IOException e) {
            logger.error("Error sending message to client", e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        prevActivities.put(session, System.currentTimeMillis());
         logger.info(message);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        prevActivities.remove(session);
        System.out.println(statusCode);
        logger.info("Session closed with status code: {}, Reason: {}", statusCode, reason);
    }

    private static void checkIdleClients() {
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<Session, Long> entry : prevActivities.entrySet()) {
            Session session = entry.getKey();
            long prevTime = entry.getValue();

            if (currentTime - prevTime > MAX_IDLE_TIME) {
                try {
                    session.getRemote().sendString("Idle timeout warning");
                    logger.warn("Idle timeout warning for session: {}", session);
                    prevActivities.put(session, currentTime);
                    counter++;
                    if (counter > 5) {
                        session.close(4000, "server initiated closure");
                    }
                } catch (IOException e) {
                    logger.error("Error sending idle timeout warning to client", e);
                }
            }
        }
    }
}

