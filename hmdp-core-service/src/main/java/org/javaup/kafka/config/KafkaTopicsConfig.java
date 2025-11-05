package org.javaup.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.javaup.core.SpringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static org.javaup.constant.Constant.SECKILL_VOUCHER_CACHE_INVALIDATION_TOPIC;

/**
 * Kafka 主题自动创建配置：创建缓存失效广播主题及其 DLQ 主题。
 */
@Configuration
public class KafkaTopicsConfig {

    @Value("${seckill.cache.invalidate.topic.partitions:3}")
    private int topicPartitions;

    @Value("${seckill.cache.invalidate.topic.replicationFactor:1}")
    private int topicReplicationFactor;

    @Value("${seckill.cache.invalidate.topic.dlq.partitions:3}")
    private int dlqPartitions;

    @Value("${seckill.cache.invalidate.topic.dlq.replicationFactor:1}")
    private int dlqReplicationFactor;

    @Bean
    public NewTopic seckillVoucherInvalidationTopic() {
        String name = SpringUtil.getPrefixDistinctionName() + "-" + SECKILL_VOUCHER_CACHE_INVALIDATION_TOPIC;
        return TopicBuilder.name(name)
                .partitions(topicPartitions)
                .replicas(topicReplicationFactor)
                .build();
    }

    @Bean
    public NewTopic seckillVoucherInvalidationDlqTopic() {
        String name = SpringUtil.getPrefixDistinctionName() + "-" + SECKILL_VOUCHER_CACHE_INVALIDATION_TOPIC + ".DLQ";
        return TopicBuilder.name(name)
                .partitions(dlqPartitions)
                .replicas(dlqReplicationFactor)
                .build();
    }
}