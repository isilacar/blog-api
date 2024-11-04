package com.scalefocus.blogservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalefocus.blogservice.entity.ElasticBlogDocument;
import com.scalefocus.blogservice.repository.ElasticBlogRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.type.SerializationException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
@RequiredArgsConstructor
public class KafkaElasticBlogConsumer {

    private static final Logger LOGGER = LogManager.getLogger(KafkaElasticBlogConsumer.class);
    private final ObjectMapper objectMapper;
    private final ElasticBlogRepository elasticBlogRepository;

    /*
    default retryable attempts is 3, I set it to 4, which means retry fail messages to consuming 4 times.
    After 4 times, if it still fails, sends the failure messages to DLT
     */
    @RetryableTopic(attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2),
            exclude = {SerializationException.class, DeserializationException.class},
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            retryTopicSuffix = "elastic-blog-event-retry",
            dltTopicSuffix = "elastic-blog-event-dlt"
    )
    @KafkaListener(topics = {"${spring.kafka.topic}"}, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEvent(ConsumerRecord<Long, Object> record) {
        ElasticBlogDocument elasticBlogDocument = objectMapper.convertValue(record.value(), ElasticBlogDocument.class);

        elasticBlogRepository.save(elasticBlogDocument);

        LOGGER.info("Elastic Blog Saved event consuming object {}, with topic name {} and the offset {} ", record.value(), record.topic(), record.offset());
    }

    //listens all the fail messages
    @DltHandler
    public void handleDLT(ConsumerRecord<Long, Object> record) {
        LOGGER.info("DLT Received from topic: {}, user with id: {}, blog: {}, offset {}", record.topic(), record.key(), record.value(), record.offset());
    }

}
