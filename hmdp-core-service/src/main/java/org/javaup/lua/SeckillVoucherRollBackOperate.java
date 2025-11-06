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
public class SeckillVoucherRollBackOperate {
    
    @Resource
    private RedisCache redisCache;
    
    // Redis Lua 数值返回在 Spring Data Redis 中通常为 Long，这里使用 Long 并在返回处做安全转换
    private DefaultRedisScript<Long> redisScript;
    
    @PostConstruct
    public void init(){
        try {
            redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/seckillVoucherRollBack.lua")));
            // 明确声明 Lua 返回类型为 Long，避免 Long->Integer 的直接强转异常
            redisScript.setResultType(Long.class);
        } catch (Exception e) {
            log.error("redisScript init lua error",e);
        }
    }
    
    public Integer execute(List<String> keys, String[] args){
        Object obj = redisCache.getInstance().execute(redisScript, keys, args);
        if (obj == null) {
            return null;
        }
        // 兼容不同驱动/序列化下的返回类型
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof Long) {
            return ((Long) obj).intValue();
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.valueOf(String.valueOf(obj));
        } catch (Exception e) {
            log.warn("Lua回滚脚本返回类型无法转换为Integer: {}", obj);
            return null;
        }
    }
}
