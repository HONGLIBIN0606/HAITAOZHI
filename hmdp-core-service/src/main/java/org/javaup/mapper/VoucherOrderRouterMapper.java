package org.javaup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.javaup.entity.VoucherOrderRouter;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface VoucherOrderRouterMapper extends BaseMapper<VoucherOrderRouter> {
    
    /**
     * 根据订单id删除数据
     * @param orderId 订单id
     * @return 删除数量
     */
    @Delete("DELETE FROM tb_voucher_order_router where order_id = #{orderId}")
    Integer deleteVoucherOrderRouter(Long orderId);
}
