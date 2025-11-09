package org.javaup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.javaup.entity.VoucherOrderRouter;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 优惠券订单路由 Mapper
 * @author: 阿星不是程序员
 **/
public interface VoucherOrderRouterMapper extends BaseMapper<VoucherOrderRouter> {
    
    /**
     * 根据订单id删除数据
     * @param orderId 订单id
     * @return 删除数量
     */
    @Delete("DELETE FROM tb_voucher_order_router where order_id = #{orderId}")
    Integer deleteVoucherOrderRouter(Long orderId);
}
