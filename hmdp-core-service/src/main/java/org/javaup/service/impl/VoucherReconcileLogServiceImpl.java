package org.javaup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.javaup.entity.VoucherReconcileLog;
import org.javaup.enums.LogType;
import org.javaup.kafka.message.SeckillVoucherMessage;
import org.javaup.mapper.VoucherReconcileLogMapper;
import org.javaup.message.MessageExtend;
import org.javaup.service.IVoucherReconcileLogService;
import org.javaup.toolkit.SnowflakeIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 对账日志 接口实现
 * @author: 阿星不是程序员
 **/
@Service
public class VoucherReconcileLogServiceImpl extends ServiceImpl<VoucherReconcileLogMapper, VoucherReconcileLog>
        implements IVoucherReconcileLogService {
    
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    
    /**
     * 构建并保存对账日志：根据日志类型设置数量字段，记录业务过程数据。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveReconcileLog(final LogType logType, 
                                 final Integer businessType, 
                                 final String detail, 
                                 final MessageExtend<SeckillVoucherMessage> message) {
        SeckillVoucherMessage body = message.getMessageBody();
        VoucherReconcileLog logEntity = new VoucherReconcileLog();
        logEntity.setId(snowflakeIdGenerator.nextId())
                .setOrderId(body.getOrderId())
                .setUserId(body.getUserId())
                .setVoucherId(body.getVoucherId())
                .setMessageId(message.getUuid())
                .setBusinessType(businessType)
                .setDetail(detail)
                .setTraceId(body.getTraceId())
                .setLogType(logType.getCode())
                .setCreateTime(java.time.LocalDateTime.now())
                .setUpdateTime(java.time.LocalDateTime.now())
                .setBeforeQty(body.getBeforeQty())
                .setChangeQty(body.getChangeQty())
                .setAfterQty(body.getAfterQty())
                .setTraceId(body.getTraceId());
        if (logType == LogType.RESTORE) {
            logEntity.setBeforeQty(body.getAfterQty());
            logEntity.setAfterQty(body.getBeforeQty());
        }
        save(logEntity);
    }
}