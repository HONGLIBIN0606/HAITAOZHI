package org.javaup.servicelock;

/**
 * @description: 分布式锁 锁类型
 * @author: hlb0606
 **/
public enum LockType {
    /**
     * 锁类型
     */
    Reentrant,
    
    Fair,
   
    Read,
    
    Write;

    LockType() {
    }

}


