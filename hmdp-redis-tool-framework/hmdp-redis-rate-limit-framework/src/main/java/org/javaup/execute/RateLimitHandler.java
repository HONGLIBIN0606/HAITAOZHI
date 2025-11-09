package org.javaup.execute;
import org.javaup.ratelimit.extension.RateLimitScene;

public interface RateLimitHandler {
    /**
     * 执行限流（带场景），便于为不同接口使用不同的窗口与阈值
     * @param voucherId 秒杀券ID
     * @param userId 用户ID
     * @param scene 限流场景（发令牌/下单）
     */
    void execute(Long voucherId, Long userId, RateLimitScene scene);
}
