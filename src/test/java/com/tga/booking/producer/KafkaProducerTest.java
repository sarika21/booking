package com.tga.booking.producer;

import com.tga.booking.entity.BookingDAO;
import com.tga.booking.producer.KafkaProducer;
import com.tga.booking.testUtil.ListenerPayloadChecker;
import com.tga.booking.util.BookingConverter;
import com.tga.booking.util.BookingGenerator;
import com.tga.common.entity.Booking;
import com.tga.common.entity.SeatInfo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DirtiesContext
class KafkaProducerTest {

    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    ListenerPayloadChecker listenerPayloadChecker;
    @Autowired
    BookingGenerator bookingGenerator;
    @Autowired
    BookingConverter bookingConverter;

    static {
        System.setProperty("spring.kafka.streams.state-dir", "/tmp/kafka-streams/"+ UUID.randomUUID());
    }

    @Test
    void send_eventProduced_isSent() throws InterruptedException {
//        String orderID = "orderkey1";
//        String itemID = "itemkey1";
//
//        Order order = new Order(orderID, 2.99);
//        Item item = new Item(itemID, 4);
//        order.setItems(List.of(item));
//
//        kafkaProducer.sendOrder(order.getOrderNumber(), order);
//        listenerPayloadChecker.getLatch().await(10000, TimeUnit.MILLISECONDS);
//        ConsumerRecord<String, Order> result = listenerPayloadChecker.getConsumerRecord();
//        List<Item> items = result.value().getItems();
//
//        assertThat(result).isNotNull();
//        assertThat(items).isNotNull();
//        System.out.println();
    }

    @Test
    @Ignore
    void generalTests() {
        List<BookingDAO> orderDAOList = bookingGenerator.generateNewbookingDAOs(Optional.of(2L), Optional.of(1L));
        List<Booking> orders = bookingConverter.listOrderDaoToKafka(orderDAOList);
        orders.forEach(order -> kafkaProducer.sendOrder(order.getBookingNumber(),order));
    }
}