package org.javaup.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.javaup.cache.SeckillVoucherCacheInvalidationPublisher;
import org.javaup.core.RedisKeyManage;
import org.javaup.dto.Result;
import org.javaup.dto.SeckillVoucherDto;
import org.javaup.dto.UpdateSeckillVoucherDto;
import org.javaup.dto.VoucherDto;
import org.javaup.entity.SeckillVoucher;
import org.javaup.entity.Voucher;
import org.javaup.handler.BloomFilterHandlerFactory;
import org.javaup.mapper.VoucherMapper;
import org.javaup.redis.RedisCache;
import org.javaup.redis.RedisKeyBuild;
import org.javaup.service.ISeckillVoucherService;
import org.javaup.service.IVoucherService;
import org.javaup.toolkit.SnowflakeIdGenerator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.javaup.constant.Constant.BLOOM_FILTER_HANDLER_VOUCHER;
import static org.javaup.utils.RedisConstants.SECKILL_STOCK_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Resource
    private BloomFilterHandlerFactory bloomFilterHandlerFactory;
    @Resource
    private RedisCache redisCache;
    @Resource
    private SeckillVoucherCacheInvalidationPublisher seckillVoucherCacheInvalidationPublisher;
    
    @Override
    public Long addVoucher(VoucherDto voucherDto) {
        Voucher one = lambdaQuery().orderByDesc(Voucher::getId).one();
        long newId = 1L;
        if (one != null) {
            newId = one.getId() + 1;
        }
        Voucher voucher = new Voucher();
        BeanUtil.copyProperties(voucherDto, voucher);
        voucher.setId(newId);
        save(voucher);
        bloomFilterHandlerFactory.get(BLOOM_FILTER_HANDLER_VOUCHER).add(voucher.getId().toString());
        return voucher.getId();
    }
    
    @Override
    public Result<List<Voucher>> queryVoucherOfShop(Long shopId) {
        // 查询优惠券信息
        List<Voucher> vouchers = getBaseMapper().queryVoucherOfShop(shopId);
        // 返回结果
        return Result.ok(vouchers);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSeckillVoucher(SeckillVoucherDto seckillVoucherDto) {
        //黑马点评v1版本
        //return doAddSeckillVoucherV1(seckillVoucherDto);
        //黑马点评v2版本
        return doAddSeckillVoucherV2(seckillVoucherDto);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSeckillVoucher(UpdateSeckillVoucherDto updateSeckillVoucherDto) {
        Long voucherId = updateSeckillVoucherDto.getVoucherId();
        // 更新 tb_voucher 表的非空字段
        boolean updatedVoucher = false;
        var voucherUpdate = this.lambdaUpdate().eq(Voucher::getId, voucherId);
        if (updateSeckillVoucherDto.getTitle() != null) {
            voucherUpdate.set(Voucher::getTitle, updateSeckillVoucherDto.getTitle());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getSubTitle() != null) {
            voucherUpdate.set(Voucher::getSubTitle, updateSeckillVoucherDto.getSubTitle());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getRules() != null) {
            voucherUpdate.set(Voucher::getRules, updateSeckillVoucherDto.getRules());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getPayValue() != null) {
            voucherUpdate.set(Voucher::getPayValue, updateSeckillVoucherDto.getPayValue());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getActualValue() != null) {
            voucherUpdate.set(Voucher::getActualValue, updateSeckillVoucherDto.getActualValue());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getType() != null) {
            voucherUpdate.set(Voucher::getType, updateSeckillVoucherDto.getType());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getStatus() != null) {
            voucherUpdate.set(Voucher::getStatus, updateSeckillVoucherDto.getStatus());
            updatedVoucher = true;
        }
        if (updatedVoucher) {
            voucherUpdate.set(Voucher::getUpdateTime, LocalDateTimeUtil.now()).update();
        }

        // 更新 tb_seckill_voucher 表的非空字段（仅时间相关）
        boolean updatedSeckill = false;
        var seckillUpdate = seckillVoucherService.lambdaUpdate().eq(SeckillVoucher::getVoucherId, voucherId);
        if (updateSeckillVoucherDto.getBeginTime() != null) {
            seckillUpdate.set(SeckillVoucher::getBeginTime, updateSeckillVoucherDto.getBeginTime());
            updatedSeckill = true;
        }
        if (updateSeckillVoucherDto.getEndTime() != null) {
            seckillUpdate.set(SeckillVoucher::getEndTime, updateSeckillVoucherDto.getEndTime());
            updatedSeckill = true;
        }
        if (updateSeckillVoucherDto.getStock() != null) {
            seckillUpdate.set(SeckillVoucher::getStock, updateSeckillVoucherDto.getStock());
            updatedSeckill = true;
        }
        // 受众规则字段更新
        if (updateSeckillVoucherDto.getAllowedLevels() != null) {
            seckillUpdate.set(SeckillVoucher::getAllowedLevels, updateSeckillVoucherDto.getAllowedLevels());
            updatedSeckill = true;
        }
        if (updateSeckillVoucherDto.getMinLevel() != null) {
            seckillUpdate.set(SeckillVoucher::getMinLevel, updateSeckillVoucherDto.getMinLevel());
            updatedSeckill = true;
        }
        if (updatedSeckill) {
            seckillUpdate.set(SeckillVoucher::getUpdateTime, LocalDateTimeUtil.now()).update();
        }

        // 更新后清理缓存，等待读路径按新数据重建缓存
        if (updatedVoucher || updatedSeckill) {
            seckillVoucherCacheInvalidationPublisher.publishInvalidate(voucherId, "update");
        }
    }
    
    public Long doAddSeckillVoucherV1(SeckillVoucherDto seckillVoucherDto) {
        // 保存优惠券
        VoucherDto voucherDto = new VoucherDto();
        BeanUtil.copyProperties(seckillVoucherDto, voucherDto);
        Long voucherId = addVoucher(voucherDto);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setId(snowflakeIdGenerator.nextId());
        seckillVoucher.setVoucherId(voucherId);
        seckillVoucher.setStock(seckillVoucherDto.getStock());
        seckillVoucher.setBeginTime(seckillVoucherDto.getBeginTime());
        seckillVoucher.setEndTime(seckillVoucherDto.getEndTime());
        seckillVoucherService.save(seckillVoucher);
        // 保存秒杀库存到Redis中
        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucherId, seckillVoucher.getStock().toString());
        // 如果数据库查询不是空的，将秒杀优惠券信息写入缓存，TTL为距离结束时间的秒数
        long ttlSeconds = Math.max(
                LocalDateTimeUtil.between(LocalDateTimeUtil.now(), seckillVoucher.getEndTime()).getSeconds(),
                1L
        );
        seckillVoucher.setStock(null);
        redisCache.set(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId),
                seckillVoucher,
                ttlSeconds,
                TimeUnit.SECONDS
        );
        return voucherId;
    }
    
    public Long doAddSeckillVoucherV2(SeckillVoucherDto seckillVoucherDto) {
        // 保存优惠券
        VoucherDto voucherDto = new VoucherDto();
        BeanUtil.copyProperties(seckillVoucherDto, voucherDto);
        Long voucherId = addVoucher(voucherDto);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setId(snowflakeIdGenerator.nextId());
        seckillVoucher.setVoucherId(voucherId);
        seckillVoucher.setStock(seckillVoucherDto.getStock());
        seckillVoucher.setBeginTime(seckillVoucherDto.getBeginTime());
        seckillVoucher.setEndTime(seckillVoucherDto.getEndTime());
        // 受众规则字段
        seckillVoucher.setAllowedLevels(seckillVoucherDto.getAllowedLevels());
        seckillVoucher.setMinLevel(seckillVoucherDto.getMinLevel());
        seckillVoucherService.save(seckillVoucher);
        // 如果数据库查询不是空的，将秒杀优惠券信息写入缓存，TTL为距离结束时间的秒数
        long ttlSeconds = Math.max(
                LocalDateTimeUtil.between(LocalDateTimeUtil.now(), seckillVoucher.getEndTime()).getSeconds(),
                1L
        );
        // 保存秒杀优惠券库存到Redis中（单槽位Hash Tag键）
        redisCache.set(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, voucherId),
                String.valueOf(seckillVoucher.getStock()),
                ttlSeconds,
                TimeUnit.SECONDS
        );
        // 保存秒杀优惠券详情到Redis中（单槽位Hash Tag键）
        seckillVoucher.setStock(null);
        redisCache.set(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId),
                seckillVoucher,
                ttlSeconds,
                TimeUnit.SECONDS
        );
        return voucherId;
    }
}
