package org.javaup.core;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料 
 * @description: 延迟队列 消费者接口
 * @author: 阿星不是程序员
 **/
public interface ConsumerTask {
    
    /**
     * 消费任务
     * @param content 具体参数
     * */
    void execute(String content);
    /**
     * 主题
     * @return 主题
     * */
    String topic();
}
