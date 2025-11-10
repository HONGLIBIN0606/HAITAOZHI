package org.javaup.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 秒杀优惠券的全部信息
 * @author: 阿星不是程序员
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SeckillVoucherFullModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键
     */
    private Long id;
    
    /**
     * 关联的优惠券的id
     */
    private Long voucherId;
    
    /**
     * 初始化库存
     */
    private Integer initStock;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 允许参与的会员等级，逗号分隔，如："1,2,3"
     */
    private String allowedLevels;

    /**
     * 最低会员等级
     */
    private Integer minLevel;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 生效时间
     */
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    private LocalDateTime endTime;
    
    /**
     * 优惠券状态 1,上架; 2,下架; 3,过期
     */
    private Integer status;
    
    /**
     * 商铺id
     */
    private Long shopId;

}
