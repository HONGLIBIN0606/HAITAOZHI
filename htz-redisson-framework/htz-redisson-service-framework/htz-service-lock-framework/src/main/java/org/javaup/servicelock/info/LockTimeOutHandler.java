package org.javaup.servicelock.info;

/**
 * @description: 处理失败抽象
 * @author: hlb0606
 **/
public interface LockTimeOutHandler {
    
    /**
     * 处理
     * @param lockName 锁名
     * */
    void handler(String lockName);
}


