package org.javaup.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_phone")
public class UserPhone implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
