package org.javaup.config;

import cn.hutool.core.collection.CollectionUtil;
import org.javaup.handler.BloomFilterHandler;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 根据配置注册多个 BloomFilterHandler Bean，Bean 名字与配置中的过滤器 name 相同，
 * 同时为业务别名（filters 的 key）注册一个别名，便于按业务注入或获取。
 */
public class BloomFilterHandlerRegistrar implements InitializingBean {

    private final ConfigurableApplicationContext applicationContext;
    private final RedissonClient redissonClient;
    private final BloomFilterProperties bloomFilterProperties;

    public BloomFilterHandlerRegistrar(ConfigurableApplicationContext applicationContext,
                                       RedissonClient redissonClient,
                                       BloomFilterProperties bloomFilterProperties) {
        this.applicationContext = applicationContext;
        this.redissonClient = redissonClient;
        this.bloomFilterProperties = bloomFilterProperties;
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, BloomFilterProperties.Filter> filters = bloomFilterProperties.getFilters();
        if (CollectionUtil.isEmpty(filters)) {
            // 未配置则不注册任何 BloomFilterHandler
            return;
        }
        filters.forEach((alias, cfg) -> {
            String beanName = StringUtils.hasText(cfg.getName()) ? cfg.getName() : alias;
            BloomFilterHandler handler = new BloomFilterHandler(
                    redissonClient,
                    beanName,
                    cfg.getExpectedInsertions(),
                    cfg.getFalseProbability()
            );
            applicationContext.getBeanFactory().registerSingleton(beanName, handler);
            if (!beanName.equals(alias)) {
                applicationContext.getBeanFactory().registerAlias(beanName, alias);
            }
        });
    }
}