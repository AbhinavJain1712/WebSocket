package com.example.websocket;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.concurrent.*;

@WebSocket
public class mywebsocket {

    private static long MAX_IDLE_TIME = 5000;
    private static long Chech_Interval = 1000;
    private static  ConcurrentHashMap<Session,Long> prev = new ConcurrentHashMap<>();
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static {
        scheduler.scheduleAtFixedRate(mywebsocket::IdleTime, Chech_Interval,Chech_Interval,TimeUnit.MILLISECONDS);
    }

    @OnWebSocketConnect
    public void OnConnect(Session session)
    {
        prev.put(session,System.currentTimeMillis());
    }

    @OnWebSocketMessage
    public void OnMessage(Session session, String message)
    {
        prev.put(session,System.currentTimeMillis());
        System.out.println(message);
    }

    @OnWebSocketClose
    public void onClose(Session session,int statusCode, String res)
    {
        prev.remove(session);
        System.out.println(statusCode);
    }

    private static void IdleTime()
    {
        long Currentime = System.currentTimeMillis();

        for(Session session : prev.keySet())
        {
            long last = prev.get(session);
            if(Currentime-last>MAX_IDLE_TIME)
            {
                System.out.println("Warning");
                prev.put(session,Currentime);
            }
        }
    }
}
