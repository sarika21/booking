package com.tga.booking;

import com.tga.booking.service.BookingManagementService;
import com.tga.common.entity.Booking;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.util.UUID;

@SpringBootApplication
@ComponentScan(basePackages = "com.tga")
@EnableKafkaStreams
@Slf4j
public class BookingApplication {


    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
    }

    private final String kafkaTopicStock;
    private final String kafkaTopicPayment;
    private final String kafkaTopicOrder;
    private final long joinWindow;
    private final BookingManagementService orderManagementService;


    public BookingApplication(@Value("${topic.name.stock}") String kafkaTopicStock,
                            @Value("${topic.name.payment}") String kafkaTopicPayment,
                            @Value("${topic.name.order}") String kafkaTopicOrder,
                            @Value("${kafka.join.window.duration.ms}") long joinWindow, BookingManagementService orderManagementService) {
        this.kafkaTopicPayment = kafkaTopicPayment;
        this.kafkaTopicStock = kafkaTopicStock;
        this.kafkaTopicOrder = kafkaTopicOrder;
        this.joinWindow = joinWindow;
        this.orderManagementService = orderManagementService;
        System.setProperty("spring.kafka.streams.state-dir", "/tmp/kafka-streams/"+ UUID.randomUUID());
    }

    @Bean
    public KStream<String, Booking> kstreamOrder(StreamsBuilder builder) {
        Serde<String> stringSerde = Serdes.String();
        JsonSerde<Booking> orderJsonSerde = new JsonSerde<>(Booking.class);

        KStream<String, Booking> orderStockStream = builder.stream(kafkaTopicStock,
                Consumed.with(stringSerde, orderJsonSerde));
        KStream<String, Booking> orderPaymentStream = builder.stream(kafkaTopicPayment,
                Consumed.with(stringSerde, orderJsonSerde));


        orderStockStream.join(orderPaymentStream, orderManagementService::processOrder, JoinWindows.of(Duration.ofMillis(joinWindow)),
                        StreamJoined.with(stringSerde, orderJsonSerde, orderJsonSerde))
                .peek((s, entityJoin) -> log.debug("order joined: {}",entityJoin))
                .to(kafkaTopicOrder);

        return orderStockStream;
    }



}
