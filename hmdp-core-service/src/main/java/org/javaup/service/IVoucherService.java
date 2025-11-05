package org.javaup.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaup.dto.Result;
import org.javaup.dto.SeckillVoucherDto;
import org.javaup.dto.UpdateSeckillVoucherDto;
import org.javaup.dto.VoucherDto;
import org.javaup.entity.Voucher;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IVoucherService extends IService<Voucher> {

    Long addVoucher(VoucherDto voucherDto);
    
    Result<List<Voucher>> queryVoucherOfShop(Long shopId);

    Long addSeckillVoucher(SeckillVoucherDto seckillVoucherDto);
    
    void updateSeckillVoucher(UpdateSeckillVoucherDto updateSeckillVoucherDto);
}
