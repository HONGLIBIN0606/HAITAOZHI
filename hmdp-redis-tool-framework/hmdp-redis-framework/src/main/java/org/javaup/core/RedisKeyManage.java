package org.javaup.core;


import lombok.Getter;

/**
 * @program: 数据中台实战项目。 添加 阿星不是程序员 微信，添加时备注 中台 来获取项目的完整资料 
 * @description: redis key管理
 * @author: 阿星不是程序员
 **/
@Getter
public enum RedisKeyManage {
    /**
     * redis 缓存 key管理
     * */
    CACHE_SHOP_KEY("cache:shop:%s","商铺","value为Shop类型","k"),
    
    CACHE_SHOP_KEY_NULL("cache:shop_null:%s","商铺空的数据","value为这是空值","k"),
    ;

    /**
     * key值
     * */
    private final String key;

    /**
     * key的说明
     * */
    private final String keyIntroduce;

    /**
     * value的说明
     * */
    private final String valueIntroduce;

    /**
     * 作者
     * */
    private final String author;

    RedisKeyManage(String key, String keyIntroduce, String valueIntroduce, String author){
        this.key = key;
        this.keyIntroduce = keyIntroduce;
        this.valueIntroduce = valueIntroduce;
        this.author = author;
    }

    public static RedisKeyManage getRc(String keyCode) {
        for (RedisKeyManage re : RedisKeyManage.values()) {
            if (re.key.equals(keyCode)) {
                return re;
            }
        }
        return null;
    }
    
}
