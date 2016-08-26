package com.toprater.sync.scaler;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SyncSender implements Runnable {
    private final Producer<String, String> producer;
    private final String id;
    private final static Logger log = LoggerFactory.getLogger(SyncSender.class);
    private final LinkedBlockingQueue<String> tasks = new LinkedBlockingQueue<String>();

    public SyncSender() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        String id = System.getenv("INSTANCE_ID");
        if (id == null || id.length() < 1) {
            id = "1";
        }
        this.id = id;
        producer = new KafkaProducer<>(props);

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
        String type;
        while (true) {
            try {
                String task = tasks.poll(10, TimeUnit.SECONDS);
                if (task==null){
                    type  = "ping";
                    producer.send(new ProducerRecord<String, String>
                            ("sync", "$test:" + id +"_"+i, "test-"+i));
                } else{
                    type  = "sync";
                    producer.send(new ProducerRecord<String, String>
                            ("sync", id +"_"+i, task));
                }
                log.info("Send: "+i+" - "+type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
