package com.toprater.sync.scaler;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.Shell;

import java.util.Properties;

public class SyncTest implements Runnable{
    private final Producer<String, String> producer;
    private final String id;
    public SyncTest(){
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
        if(id==null || id.length()<1){
            id = "1";
        }
        this.id = id;
        producer = new KafkaProducer<>(props);

    }
    @Override
    public void run() {
        long i = 0;
        while (true){
            try {
                Thread.currentThread().sleep(10000);
            } catch (Exception e){
                e.printStackTrace();
            }
            producer.send(new ProducerRecord<String, String>
                    ("sync","$test:"+id+i,"test-i"));
            i++;
        }
    }
}
