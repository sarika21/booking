package com.tga.booking.testUtil;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tga.common.entity.Booking;

import java.util.concurrent.CountDownLatch;

/**
 * a simple listener with the purpose to check the payload received
 */
@Component
public class ListenerPayloadChecker {

    private CountDownLatch latch = new CountDownLatch(1);
    private ConsumerRecord<String, Booking> consumerRecord = null;

    @KafkaListener(topics = "${topic.name.order}", groupId = "test")
    private void receive(ConsumerRecord<String, Booking> consumerRecord) {
        setConsumerRecord(consumerRecord);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public ConsumerRecord<String, Booking> getConsumerRecord() {
        return consumerRecord;
    }

    public void setConsumerRecord(ConsumerRecord<String, Booking> consumerRecord) {
        this.consumerRecord = consumerRecord;
    }
}
