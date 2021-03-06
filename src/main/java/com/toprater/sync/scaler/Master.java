package com.toprater.sync.scaler;

import com.google.gson.Gson;
import com.toprater.sync.scaler.model.StartMessage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


import static spark.Spark.*;

public class Master{
    private final static Logger log = LoggerFactory.getLogger(Master.class);

    private static SyncReceiver receiver;
    private static SyncSender sender;
    public static void main(String[] args) throws Exception {
        port(7476);
        Gson gson = new Gson();
        sender = new SyncSender();
        new Thread(sender).start();
        get("/prepare", (request, response) -> {
            log.info("prepare");
            synchronized(Master.class) {
                if (receiver != null) {
                    receiver.stop();
                }
                receiver = new SyncReceiver();
            }
            return "";
        });
        get("/started","application/json", (request, response) -> {
            log.info("started");
            synchronized(Master.class) {
                if (receiver == null) {
                    log.info("EXIT bad logic!!!");
                    System.exit(1);
                }
                new Thread(receiver).start();
            }
            return new StartMessage();
        },gson::toJson);
        post("/push", (request, response) -> {
            log.info("push");
            sender.send(request.body());
            return "";
        });
    }
}