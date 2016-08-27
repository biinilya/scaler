package com.toprater.sync.scaler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class SyncReceiver implements Runnable{
    private final static Logger log = LoggerFactory.getLogger(SyncReceiver.class);
    private final String id;
    private volatile boolean isRunning = true;
    public void stop(){
        isRunning = false;
    }
    public SyncReceiver(){
        String id = System.getenv("INSTANCE_ID");
        if(id==null || id.length()<1){
            id = "1";
        }
        this.id = id;
        log.info("I am "+id);
    }

    @Override
    public void run() {
        log.info("Listen "+id);
        while (isRunning) {
            try {
                String task = Master.tasks.poll(10, TimeUnit.SECONDS);
                if (task != null) {

                    URL u = new URL("http://127.0.0.1:8080/sync");
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Content-Length", String.valueOf(task.length()));
                    OutputStream os = conn.getOutputStream();
                    os.write(task.getBytes());
                    os.flush();
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
