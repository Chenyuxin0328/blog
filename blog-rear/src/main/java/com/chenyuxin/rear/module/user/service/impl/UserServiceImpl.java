package com.chenyuxin.rear.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenyuxin.rear.common.exception.BusinessException;
import com.chenyuxin.rear.common.exception.ErrorCode;
import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.common.util.BeanCopyUtil;
import com.chenyuxin.rear.module.file.constant.MinioBucket;
import com.chenyuxin.rear.module.user.mapper.UserMapper;
import com.chenyuxin.rear.module.user.model.dto.GetUserInfoDto;
import com.chenyuxin.rear.module.user.model.dto.RegisterDto;
import com.chenyuxin.rear.module.user.model.dto.UpdateAvatarDto;
import com.chenyuxin.rear.module.user.model.entity.User;
import com.chenyuxin.rear.module.user.model.vo.SelectPageUser;
import com.chenyuxin.rear.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Value("${ip}")
    private String ip;
    // 根据用户名查询用户
    @Override
    public User findByUsername(String username) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUsername,username);
        return userMapper.selectOne(wrapper);
    }
    // 注册
    @Override
    public Long register(RegisterDto registerDto) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUsername,registerDto.getUsername());
        User user2 = userMapper.selectOne(wrapper);
        if(user2!=null){
            throw new BusinessException(ErrorCode.USERNAME_SAME);
        }
        User user = BeanCopyUtil.copy(registerDto, User.class);
        user.setRole("user");
        user.setAvatar("http://"+ip+":9000/"+ MinioBucket.USER_HEAD.getBucketName()+"/a19.png");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public void updateAvatar(UpdateAvatarDto updateAvatarDto) {
        String path = "http://"+ip+":9000/"+ MinioBucket.USER_HEAD.getBucketName()+"/"+updateAvatarDto.getAvatar();
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId,updateAvatarDto.getUserId())
                .set(User::getAvatar,path);
        userMapper.update(wrapper);
    }

    @Override
    public GetUserInfoDto getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        GetUserInfoDto getUserInfoDto = BeanCopyUtil.copy(user, GetUserInfoDto.class);
        return getUserInfoDto;
    }
    // 分页查询用户
    @Override
    public PageResult<SelectPageUser> selectPageUser(String search, String role, Integer pageNum, Integer pageSize) {
        IPage<User> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(search)) {
            wrapper.like(User::getUsername, search);
        }
        if (StringUtils.isNotBlank(role)) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getCreateTime);
        IPage<User> userIPage = userMapper.selectPage(page, wrapper);
        List<SelectPageUser> selectPageUsers = userIPage.getRecords().stream()
                .map(user -> BeanCopyUtil.copy(user, SelectPageUser.class))
                .toList();
        return new PageResult<>(pageNum,pageSize,userIPage.getTotal(),selectPageUsers);
    }
    // 根据用户id删除用户
    @Override
    public void deleteUserById(Long userId) {
        userMapper.deleteById(userId);
    }



    // 赋予用户管理员权限
    @Override
    public void setUserRole(Long userId,String role) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId,userId)
                .set(User::getRole,role);
        userMapper.update(wrapper);
    }

}
