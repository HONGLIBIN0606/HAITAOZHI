package org.javaup.delay.consumer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.javaup.core.ConsumerTask;
import org.javaup.core.RedisKeyManage;
import org.javaup.core.SpringUtil;
import org.javaup.delay.message.DelayedVoucherReminderMessage;
import org.javaup.entity.UserInfo;
import org.javaup.mapper.VoucherOrderRouterMapper;
import org.javaup.model.SeckillVoucherFullModel;
import org.javaup.redis.RedisCache;
import org.javaup.redis.RedisKeyBuild;
import org.javaup.service.ISeckillVoucherService;
import org.javaup.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.javaup.constant.Constant.DELAY_VOUCHER_REMINDER;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 延迟抢购优惠券提醒-消费
 * @author: 阿星不是程序员
 **/

@Slf4j
@Component
public class ConsumerDelayedVoucherReminder implements ConsumerTask {
    @Resource
    private RedisCache redisCache;
    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private IUserInfoService userInfoService;
    @Resource
    private VoucherOrderRouterMapper voucherOrderRouterMapper;

    @Value("${seckill.reminder.notify.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${seckill.reminder.notify.app.enabled:false}")
    private boolean appEnabled;

    @Value("${seckill.reminder.notify.sms.to:}")
    private String smsTo;

    @Value("${seckill.reminder.dedup.window.seconds:1800}")
    private long dedupWindowSeconds;

    /**
     * 当优惠券未设置allowedLevels/minLevel时的默认最小会员等级
     * */
    @Value("${seckill.reminder.notify.default.minLevel:1}")
    private int defaultMinLevel;
    /**
     * 每次提醒的最大用户数量，防止一次性通知过多
     * */
    @Value("${seckill.reminder.notify.max.users:1000}")
    private int maxNotifyUsers;
    /**
     * 是否附加通知“最近购买活跃用户”
     * */
    @Value("${seckill.reminder.notify.top.buyers.enabled:false}")
    private boolean topBuyersEnabled;
    /**
     * 统计最近多少天的购买行为
     * */
    @Value("${seckill.reminder.notify.top.buyers.days:30}")
    private int topBuyersDays;
    /**
     * Top购买用户数量（与maxNotifyUsers合并后去重）
     * */
    @Value("${seckill.reminder.notify.top.buyers.count:200}")
    private int topBuyersCount;
    
    @Override
    public void execute(final String content) {
        try {
            DelayedVoucherReminderMessage msg = parseMessage(content);
            if (msg == null) { return; }
            Long voucherId = msg.getVoucherId();
            SeckillVoucherFullModel voucherFull = seckillVoucherService.queryByVoucherId(voucherId);
            if (voucherFull == null) {
                log.warn("[DELAY_REMINDER_CONSUMER] 秒杀券不存在或缓存未命中 voucherId={}", voucherId);
                return;
            }
            Set<String> userIds = buildAudienceUserIds(voucherFull);
            if (CollectionUtil.isEmpty(userIds)) {
                log.info("[DELAY_REMINDER_CONSUMER] 无符合规则的用户 voucherId={}", voucherId);
                return;
            }
            int notified = notifyUsers(voucherId, msg.getBeginTime(), userIds);
            log.info("[DELAY_REMINDER_CONSUMER] 完成提醒 voucherId={} totalUsers={} notified={}",
                    voucherId, userIds.size(), notified);
        } catch (Exception e) {
            log.warn("[DELAY_REMINDER_CONSUMER] 执行异常", e);
        }
    }

    private DelayedVoucherReminderMessage parseMessage(String content) {
        try {
            DelayedVoucherReminderMessage msg = JSON.parseObject(content, DelayedVoucherReminderMessage.class);
            if (msg == null || msg.getVoucherId() == null) {
                log.warn("[DELAY_REMINDER_CONSUMER] 消息解析失败 content={}", content);
                return null;
            }
            return msg;
        } catch (Exception ex) {
            log.warn("[DELAY_REMINDER_CONSUMER] 消息反序列化异常 content={}", content, ex);
            return null;
        }
    }

    private Set<String> buildAudienceUserIds(SeckillVoucherFullModel voucherFull) {
        String allowedLevelsStr = voucherFull.getAllowedLevels();
        Integer minLevel = voucherFull.getMinLevel();
        // 商铺id（用于Top买家统计按店铺过滤）
        Long shopId = voucherFull.getShopId();
        List<UserInfo> userInfos;
        if (StrUtil.isNotBlank(allowedLevelsStr)) {
            Set<Integer> allowed = new HashSet<>();
            try {
                for (String s : allowedLevelsStr.split(",")) {
                    if (StrUtil.isNotBlank(s)) {
                        allowed.add(Integer.valueOf(s.trim()));
                    }
                }
            } catch (Exception ignore) { }
            if (CollectionUtil.isNotEmpty(allowed)) {
                userInfos = userInfoService.lambdaQuery()
                        .select(UserInfo::getUserId, UserInfo::getLevel)
                        .in(UserInfo::getLevel, allowed)
                        .last("limit " + maxNotifyUsers)
                        .list();
            } else {
                int useMin = Objects.nonNull(minLevel) ? minLevel : defaultMinLevel;
                userInfos = userInfoService.lambdaQuery()
                        .select(UserInfo::getUserId, UserInfo::getLevel)
                        .ge(UserInfo::getLevel, useMin)
                        .last("limit " + maxNotifyUsers)
                        .list();
            }
        } else if (Objects.nonNull(minLevel)) {
            userInfos = userInfoService.lambdaQuery()
                    .select(UserInfo::getUserId, UserInfo::getLevel)
                    .ge(UserInfo::getLevel, minLevel)
                    .last("limit " + maxNotifyUsers)
                    .list();
        } else {
            userInfos = userInfoService.lambdaQuery()
                    .select(UserInfo::getUserId, UserInfo::getLevel)
                    .ge(UserInfo::getLevel, defaultMinLevel)
                    .last("limit " + maxNotifyUsers)
                    .list();
        }
        Set<String> userIds = new LinkedHashSet<>();
        if (CollectionUtil.isNotEmpty(userInfos)) {
            for (UserInfo ui : userInfos) {
                if (Objects.nonNull(ui) && Objects.nonNull(ui.getUserId())) {
                    userIds.add(String.valueOf(ui.getUserId()));
                }
            }
        }
        if (topBuyersEnabled) {
            try {
                List<Long> topBuyerIds = voucherOrderRouterMapper.findTopBuyerUserIdsByShop(shopId, topBuyersCount, topBuyersDays);
                for (Long uid : topBuyerIds) {
                    if (uid != null) { 
                        userIds.add(String.valueOf(uid)); 
                    }
                }
            } catch (Exception ex) {
                log.warn("[DELAY_REMINDER_CONSUMER] 查询店铺Top购买用户失败, shopId={}, days={}, count={}, ex={}", shopId, topBuyersDays, topBuyersCount, ex.getMessage());
            }
        }
        return userIds;
    }

    private int notifyUsers(Long voucherId, java.time.LocalDateTime beginTime, Set<String> userIds) {
        int notifyCount = 0;
        for (String userIdStr : userIds) {
            if (StrUtil.isBlank(userIdStr)) { continue; }
            boolean shouldNotify;
            try {
                shouldNotify = redisCache.setIfAbsent(
                        RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_REMINDER_NOTIFY_DEDUP_KEY, voucherId, userIdStr),
                        "1",
                        dedupWindowSeconds,
                        java.util.concurrent.TimeUnit.SECONDS
                );
            } catch (Exception e) {
                shouldNotify = true;
            }
            if (!shouldNotify) { continue; }
            String notifyContent = String.format("[REMINDER] voucherId=%s userId=%s beginTime=%s",
                    voucherId, userIdStr, beginTime);
            if (smsEnabled && StrUtil.isNotBlank(smsTo)) {
                log.info("[REMINDER_SMS] to={} content={}", smsTo, notifyContent);
            }
            if (appEnabled) {
                log.info("[REMINDER_APP] userId={} content={}", userIdStr, notifyContent);
            }
            notifyCount++;
        }
        return notifyCount;
    }
    
    @Override
    public String topic() {
        return SpringUtil.getPrefixDistinctionName() + "-" + DELAY_VOUCHER_REMINDER;
    }
}
