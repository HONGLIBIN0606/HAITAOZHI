package org.javaup.util;

/**
 * @description: 分布式锁 方法类型执行 无返回值的业务
 * @author: hlb0606
 **/
@FunctionalInterface
public interface TaskRun {
    
    /**
     * 执行任务
     * */
    void run();
}


