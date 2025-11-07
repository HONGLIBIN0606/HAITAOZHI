package org.javaup.ratelimit.extension;

/**
 * 限流场景：用于为不同接口选择不同的窗口与阈值配置
 */
public enum RateLimitScene {
    /** 发令牌接口 */
    ISSUE_TOKEN,
    /** 下单（秒杀）接口 */
    SECKILL_ORDER
}