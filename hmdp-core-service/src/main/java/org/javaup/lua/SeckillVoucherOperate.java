package org.javaup.lua;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.javaup.redis.RedisCache;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SeckillVoucherOperate {
    
    @Resource
    private RedisCache redisCache;
    
    private DefaultRedisScript<Integer> redisScript;
    
    @PostConstruct
    public void init(){
        try {
            redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/seckillVoucher.lua")));
            redisScript.setResultType(Integer.class);
        } catch (Exception e) {
            log.error("redisScript init lua error",e);
        }
    }
    
    public Integer execute(List<String> keys, String[] args){
        return (Integer) redisCache.getInstance().execute(redisScript, keys, args);
    }
}
