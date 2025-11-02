package org.javaup.consumer;

import org.javaup.message.MessageExtend;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;

import java.util.Map;

public class OrderConsumer extends AbstractConsumerHandler<OrderEvent> {

    public OrderConsumer() {
        super(OrderEvent.class);
    }

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void onMessage(String value,
                          @Headers Map<String, Object> headers,
                          @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        consumeRaw(value, key, headers);
    }

    @Override
    protected void doConsume(MessageExtend<OrderEvent> message) {
        OrderEvent event = message.getMessageBody();
        // 你的消费逻辑（幂等校验、业务处理等）
        // 可用 message.getHeaders() 获取传入的业务元数据（如 traceId、bizType）
    }
}