package org.javaup.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: redis数据
 * @author: hlb0606
 **/
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}


