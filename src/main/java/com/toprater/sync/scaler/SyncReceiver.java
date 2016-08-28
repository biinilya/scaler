package com.toprater.sync.scaler;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

public class SyncReceiver implements Runnable{
    private final static Logger log = LoggerFactory.getLogger(SyncReceiver.class);
    private final String id;
    private final KafkaConsumer<String, String> consumer;
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
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka:9092");
        props.put("group.id", id);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("sync"));
    }

    @Override
    public void run() {
        log.info("Listen "+id);
        while (isRunning) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records){
                if (record.key().startsWith("$test:")) {
                    log.info("Receive Ping" + record.key() + ":" + record.value());
                } else{
                    log.info("Receive Task" + record.key() + ":" + record.value());
                    if (!record.key().startsWith(id+"_")){
                        String task = record.value();
                        try {
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
                            conn.getInputStream().close();
                            log.info("API response: "+conn.getResponseCode()+" = "+conn.getResponseCode());
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    } else{
                        log.info("Ignore self task");
                    }
                }
            }
        }
    }
}
