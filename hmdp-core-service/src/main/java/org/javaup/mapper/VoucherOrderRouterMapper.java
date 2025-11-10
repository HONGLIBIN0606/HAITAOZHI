package org.javaup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

    /**
     * 查询指定店铺最近days天购买次数最多的用户ID列表（按店铺Top-N）。
     * 通过voucher_id关联到tb_voucher的shop_id进行过滤。
     */
    @Select("SELECT vor.user_id FROM tb_voucher_order_router vor " +
            "JOIN tb_voucher v ON v.id = vor.voucher_id " +
            "WHERE v.shop_id = #{shopId} AND vor.create_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY vor.user_id ORDER BY COUNT(1) DESC LIMIT #{limit}")
    java.util.List<Long> findTopBuyerUserIdsByShop(@Param("shopId") Long shopId,
                                                  @Param("limit") int limit,
                                                  @Param("days") int days);
}
