package org.javaup.cache;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.javaup.entity.SeckillVoucher;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 本地缓存：秒杀优惠券详情
 * @author: 阿星不是程序员
 **/
@Component
public class SeckillVoucherLocalCache {

    private final Cache<Long, SeckillVoucher> cache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfter(new Expiry<Long, SeckillVoucher>() {
                @Override
                public long expireAfterCreate(Long key, SeckillVoucher value, long currentTime) {
                    long ttlSeconds = 60L;
                    if (value != null && value.getEndTime() != null) {
                        ttlSeconds = Math.max(
                                LocalDateTimeUtil.between(LocalDateTimeUtil.now(), value.getEndTime()).getSeconds(),
                                1L
                        );
                    }
                    return TimeUnit.NANOSECONDS.convert(ttlSeconds, TimeUnit.SECONDS);
                }

                @Override
                public long expireAfterUpdate(Long key, SeckillVoucher value, long currentTime, long currentDuration) {
                    return currentDuration;
                }

                @Override
                public long expireAfterRead(Long key, SeckillVoucher value, long currentTime, long currentDuration) {
                    return currentDuration;
                }
            })
            .build();

    public SeckillVoucher get(Long voucherId) {
        return cache.getIfPresent(voucherId);
    }

    public void put(Long voucherId, SeckillVoucher voucher) {
        if (voucherId != null && voucher != null) {
            cache.put(voucherId, voucher);
        }
    }

    public void invalidate(Long voucherId) {
        cache.invalidate(voucherId);
    }
}