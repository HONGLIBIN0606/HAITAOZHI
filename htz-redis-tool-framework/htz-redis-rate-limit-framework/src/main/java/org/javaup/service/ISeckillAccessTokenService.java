package org.javaup.service;

/**
 * @description: 令牌
 * @author: hlb0606
 **/
public interface ISeckillAccessTokenService {
  
    boolean isEnabled();
 
    String issueAccessToken(Long voucherId, Long userId);
    
    boolean validateAndConsume(Long voucherId, Long userId, String token);
}

