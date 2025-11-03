-- 1.参数列表
-- 秒杀券库存的key
local seckill_stock_key = KEYS[1]
-- 秒杀券的详情
local seckill_voucher_key = KEYS[2]
-- 订单的key
local seckill_order_key = KEYS[3]
-- 优惠券id
local voucherId = ARGV[1]
-- 用户id
local userId = ARGV[2]
-- 订单id
local orderId = ARGV[3]

-- 2.数据key
-- 库存key
local stockKey = string.format(seckill_stock_key, voucherId)
-- 秒杀券的详情key
local seckillVoucherKey = string.format(seckill_voucher_key, voucherId)
-- 订单key
local orderKey = string.format(seckill_order_key, seckill_order_key)

-- 3.脚本业务
local seckillVoucherStr = redis.call('get', seckillVoucherKey);
-- 缓存中的秒杀券详情为空，则直接返回
if not seckillVoucherStr then
    return 10001
end
local seckillVoucher = cjson.decode(seckillVoucherStr)
-- 活动开始/结束时间（毫秒）
local beginTime = tonumber(seckillVoucher.beginTime)
local endTime = tonumber(seckillVoucher.endTime)

-- 当前时间（毫秒）
local timeArr = redis.call('TIME')
local nowMillis = tonumber(timeArr[1]) * 1000 + math.floor(tonumber(timeArr[2]) / 1000)

-- 时间范围判断：未开始或已结束直接返回
if nowMillis < beginTime then
    return 10002
end
if nowMillis > endTime then
    return 10003
end
local stock = redis.call('get',stockKey);
-- 缓存中的秒杀券库存为空，则直接返回
if not stock then
    return 10004
end
-- 判断库存是否充足
if(tonumber(stock) <= 0) then
    -- 库存不足，则直接返回
    return 10005
end
-- 3.2.判断用户是否下单 SISMEMBER orderKey userId
if(redis.call('sismember', orderKey, userId) == 1) then
    -- 3.3.存在，说明是重复下单，返回2
    return 10006
end
-- 3.4.扣库存 incrby stockKey -1
redis.call('incrby', stockKey, -1)
-- 3.5.下单（保存用户）sadd orderKey userId
redis.call('sadd', orderKey, userId)
return 0