package com.toprater.sync.scaler;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

public class SyncReceiver implements Runnable{
    private final static Logger log = LoggerFactory.getLogger(SyncReceiver.class);
    private final String id;
    private final KafkaConsumer<String, String> consumer;
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
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records){
                if (record.key().startsWith("$test:")){
                    log.info("Receive "+record.key()+":"+record.value());
                }
            }
        }
    }
}
