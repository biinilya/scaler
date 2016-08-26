package com.toprater.sync.scaler;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.utils.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;

public class SyncTest implements Runnable {
    private final Producer<String, String> producer;
    private final String id;
    private final static Logger log = LoggerFactory.getLogger(SyncTest.class);

    public SyncTest() {
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

    @Override
    public void run() {
        log.info("Send "+id);
        long i = 0;
        while (true) {
            try {
                Thread.currentThread().sleep(10000);
                RecordMetadata meta = producer.send(new ProducerRecord<String, String>
                        ("sync", "$test:" + id + i, "test-i")).get();
                log.info("Send ",meta.partition());
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
