package org.javaup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.javaup.entity.VoucherReconcileLog;
import org.javaup.mapper.VoucherReconcileLogMapper;
import org.javaup.service.IVoucherReconcileLogService;
import org.springframework.stereotype.Service;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 对账日志 接口实现
 * @author: 阿星不是程序员
 **/
@Service
public class VoucherReconcileLogServiceImpl extends ServiceImpl<VoucherReconcileLogMapper, VoucherReconcileLog>
        implements IVoucherReconcileLogService {
}