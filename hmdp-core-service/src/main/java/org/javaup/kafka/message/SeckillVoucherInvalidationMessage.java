package org.javaup.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 秒杀券缓存失效广播消息
 * @author: 阿星不是程序员
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillVoucherInvalidationMessage {

    /** 秒杀券的 voucherId */
    private Long voucherId;

    /** 可选：失效原因（update/delete/expire等），仅做观测用 */
    private String reason;
}