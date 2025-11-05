import request from '@/utils/request'
export const getVoucherList = (shopId) => request.get('/voucher/list/' + shopId)
export const seckillVoucher = (id) =>
  request.post('/voucher-order/seckill/' + id)
