package org.javaup.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀券缓存失效广播消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillVoucherInvalidationMessage {

    /** 秒杀券的 voucherId */
    private Long voucherId;

    /** 可选：失效原因（update/delete/expire等），仅做观测用 */
    private String reason;
}