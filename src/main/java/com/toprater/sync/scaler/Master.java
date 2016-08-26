package com.toprater.sync.scaler;

import com.google.gson.Gson;
import com.toprater.sync.scaler.model.StartMessage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


import static spark.Spark.*;

public class Master{
    private final static Logger log = LoggerFactory.getLogger(Master.class);

    public static void main(String[] args) throws Exception {
        port(7476);
        Gson gson = new Gson();
        new Thread(new SyncTest()).run();
        new Thread(new SyncReceiver()).run();
        get("/prepare", (request, response) -> {
            log.info("prepare");
            return "";
        });
        get("/started","application/json", (request, response) -> {
            log.info("started");
            return new StartMessage();
        },gson::toJson);
        post("/push", (request, response) -> {
            log.info("push");
            return "";
        });
    }
}