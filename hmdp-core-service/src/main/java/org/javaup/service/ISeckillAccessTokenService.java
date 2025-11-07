package org.javaup.service;

public interface ISeckillAccessTokenService {
    /**
     * 是否启用访问令牌校验
     */
    boolean isEnabled();

    /**
     * 为用户申请指定voucher的访问令牌
     * @param voucherId 秒杀券ID
     * @param userId 用户ID
     * @return 访问令牌字符串
     */
    String issueAccessToken(Long voucherId, Long userId);

    /**
     * 校验并消费令牌（原子）
     * @param voucherId 秒杀券ID
     * @param userId 用户ID
     * @param token 令牌
     * @return true表示校验成功且令牌被消费
     */
    boolean validateAndConsume(Long voucherId, Long userId, String token);
}