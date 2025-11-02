package org.javaup.message;

import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
public final class MessageExtend<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息体
     */
    @NonNull
    private T messageBody;

    /**
     * 唯一标识
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * 消息发送时间
     */
    private Date nowTime = DateTime.now();
}
