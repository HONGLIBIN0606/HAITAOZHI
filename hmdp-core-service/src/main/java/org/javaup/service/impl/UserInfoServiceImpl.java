package org.javaup.service.impl;

import org.javaup.entity.UserInfo;
import org.javaup.mapper.UserInfoMapper;
import org.javaup.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 用户信息 接口实现
 * @author: 阿星不是程序员
 **/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
