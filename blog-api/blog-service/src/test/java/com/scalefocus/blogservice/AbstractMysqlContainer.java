package com.scalefocus.blogservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractMysqlContainer {

    private static final MySQLContainer MY_SQL_CONTAINER;
    private static final KafkaContainer KAFKA_CONTAINER;
    private static final ElasticsearchContainer ELASTICSEARCH_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer("mysql:latest")
                .withDatabaseName("blogdb")
                .withUsername("isil")
                .withPassword("123456");
        MY_SQL_CONTAINER.start();

        ELASTICSEARCH_CONTAINER =
                new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.17.10")
                        .withReuse(true);
        ELASTICSEARCH_CONTAINER.start();

        KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.4"))
                .withExposedPorts(9093)
                .waitingFor(Wait.forListeningPort());
        KAFKA_CONTAINER.start();

    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.elasticsearch.uris", () -> String.format("http://%s:%d", ELASTICSEARCH_CONTAINER.getHost(), ELASTICSEARCH_CONTAINER.getFirstMappedPort()));
    }

}
