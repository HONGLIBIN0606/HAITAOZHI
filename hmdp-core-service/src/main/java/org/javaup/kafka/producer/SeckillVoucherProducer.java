package org.javaup.kafka.producer;

import cn.hutool.core.collection.ListUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.javaup.AbstractProducerHandler;
import org.javaup.core.RedisKeyManage;
import org.javaup.enums.BaseCode;
import org.javaup.enums.LogType;
import org.javaup.kafka.message.SeckillVoucherMessage;
import org.javaup.lua.SeckillVoucherRollBackOperate;
import org.javaup.message.MessageExtend;
import org.javaup.redis.RedisKeyBuild;
import org.javaup.toolkit.SnowflakeIdGenerator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SeckillVoucherProducer extends AbstractProducerHandler<MessageExtend<SeckillVoucherMessage>> {
    
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    
    @Resource
    private SeckillVoucherRollBackOperate seckillVoucherRollBackOperate;
    
    public SeckillVoucherProducer(final KafkaTemplate<String,MessageExtend<SeckillVoucherMessage>> kafkaTemplate) {
        super(kafkaTemplate);
    }
    
    @Override
    protected void afterSendFailure(final String topic, final MessageExtend<SeckillVoucherMessage> message, final Throwable throwable) {
        super.afterSendFailure(topic, message, throwable);
        // 回滚Redis中的数据
        long traceId = snowflakeIdGenerator.nextId();
        rollbackRedisVoucherData(traceId,message);
    }
    
    public void rollbackRedisVoucherData(Long traceId,
                                         MessageExtend<SeckillVoucherMessage> message) {
        try {
            // 回滚redis中的数据
            List<String> keys = ListUtil.of(
                    RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, message.getMessageBody().getVoucherId()).getRelKey(),
                    RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY, message.getMessageBody().getVoucherId()).getRelKey(),
                    RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_TRACE_LOG_TAG_KEY, message.getMessageBody().getVoucherId()).getRelKey()
            );
            String[] args = new String[6];
            args[0] = message.getMessageBody().getVoucherId().toString();
            args[1] = message.getMessageBody().getUserId().toString();
            args[2] = message.getMessageBody().getOrderId().toString();
            args[3] = String.valueOf(traceId);
            args[4] = String.valueOf(LogType.RESTORE.getCode());
            args[5] = String.valueOf(600);
            Integer result = seckillVoucherRollBackOperate.execute(
                    keys,
                    args
            );
            if (!result.equals(BaseCode.SUCCESS.getCode())) {
                //TODO
                log.error("回滚失败");
            }
        }catch (Exception e){
            //TODO
            log.error("回滚失败",e);
        }
    }
}
