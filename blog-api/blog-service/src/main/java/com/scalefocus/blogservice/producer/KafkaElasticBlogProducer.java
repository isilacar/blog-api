package com.scalefocus.blogservice.producer;

import com.scalefocus.blogservice.entity.ElasticBlogDocument;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class KafkaElasticBlogProducer {

    @Value("${spring.kafka.topic}")
    private String topic;

    private static final Logger LOGGER = LogManager.getLogger(KafkaElasticBlogProducer.class);

    private final KafkaTemplate<Long, Object> kafkaTemplate;
    private final Queue<ProducerRecord<Long, Object>> eventQueue = new ConcurrentLinkedQueue<>();

    public void createEvent(ElasticBlogDocument elasticBlogDocument) {
        LOGGER.info("Creating elastic blog document event {}", elasticBlogDocument);
        ProducerRecord<Long, Object> record = buildRecord(elasticBlogDocument.getUserId(), elasticBlogDocument);
        sendEvent(record);
    }

    private ProducerRecord<Long, Object> buildRecord(Long key, Object value) {
        return new ProducerRecord<>(topic, key, value);
    }

    public void sendEvent(ProducerRecord<Long, Object> record) {

        CompletableFuture<SendResult<Long, Object>> sentResult = kafkaTemplate.send(record);
        sentResult.whenComplete((result, exception) -> {
            if (Objects.isNull(exception)) {
                LOGGER.info("Sent elastic.blog.saved.event value= {} , with offset= {} and partition number= {}",
                        result.getProducerRecord().value(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().partition());
            } else {
                LOGGER.error("Error occurred while sending elastic.blog.event:  {}", exception.getMessage());

                eventQueue.offer(record);
                retryFailedMessage();
            }
        });

    }

    private void retryFailedMessage() {
        if (!eventQueue.isEmpty()) {
            LOGGER.info("Retry failed messages");
            while (!eventQueue.isEmpty()) {
                ProducerRecord<Long, Object> record = eventQueue.poll();
                if (Objects.nonNull(record)) {
                    sendEvent(record);
                }
            }
        }
    }

}
