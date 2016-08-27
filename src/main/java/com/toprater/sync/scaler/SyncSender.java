package com.toprater.sync.scaler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SyncSender implements Runnable {
    private final String id;
    private final static Logger log = LoggerFactory.getLogger(SyncSender.class);
    private final LinkedBlockingQueue<String> tasks = new LinkedBlockingQueue<String>();

    public SyncSender() {
      String id = System.getenv("INSTANCE_ID");
        if (id == null || id.length() < 1) {
            id = "1";
        }
        this.id = id;

    }

    public void send(String body){
        try {
            tasks.put(body);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        log.info("Send "+id);
        long i = 0;
        while (true) {
            try {
                String task = tasks.poll(10, TimeUnit.SECONDS);
                if(task!=null) {
                    Master.tasks.put(task);
                    log.info("Send: " + i);
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
