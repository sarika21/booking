package com.tga.booking.producer;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tga.common.entity.Booking;

@Component
@Slf4j
public class KafkaProducer {


    private final KafkaTemplate<String, Booking> kafkaTemplate;
    private final String orderTopic;
    private final String notificationTopic;

    public KafkaProducer(KafkaTemplate<String, Booking> kafkaTemplate,
                         @Value("${topic.name.order}") String orderTopic,
                         @Value("${topic.name.notification}") String notificationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderTopic = orderTopic;
        this.notificationTopic = notificationTopic;
    }

    public void sendOrder(String key, Booking payload) {
        log.debug("sending to booking topic={}, key={}, topic={}", payload, key, orderTopic);
        kafkaTemplate.send(orderTopic, key, payload);
}
    public void sendNotification(String key, Booking payload) {
        log.debug("sending to notification topic={}, key={}, topic={}", payload, key, notificationTopic);
        kafkaTemplate.send(notificationTopic, key, payload);
    }

}
