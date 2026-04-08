package org.javaup.util;

/**
 * @description: 分布式锁 方法类型执行 有返回值的业务
 * @author: hlb0606
 **/
@FunctionalInterface
public interface TaskCall<V> {

    /**
     * 执行任务
     * @return 结果
     * */
    V call();
}


