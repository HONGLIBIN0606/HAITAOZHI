package org.javaup.execute;
import org.javaup.ratelimit.extension.RateLimitScene;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 限流执行 接口
 * @author: 阿星不是程序员
 **/
public interface RateLimitHandler {
    /**
     * 执行限流（带场景），便于为不同接口使用不同的窗口与阈值，提供滑动窗口与动态令牌两种
     * @param voucherId 秒杀券ID
     * @param userId 用户ID
     * @param scene 限流场景（发令牌/下单）
     */
    void execute(Long voucherId, Long userId, RateLimitScene scene);
}
