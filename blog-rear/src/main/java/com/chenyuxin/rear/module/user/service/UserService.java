package com.chenyuxin.rear.module.user.service;


import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.module.user.model.dto.GetUserInfoDto;
import com.chenyuxin.rear.module.user.model.dto.RegisterDto;
import com.chenyuxin.rear.module.user.model.dto.UpdateAvatarDto;
import com.chenyuxin.rear.module.user.model.entity.User;

public interface UserService {
    User findByUsername(String username);

    /**
     * 用户注册
     * @param registerDto RegisterDto
     * @return userId
     */
    Long register(RegisterDto registerDto);

    /**
     * 更新头像
     * @param updateAvatarDto UpdateAvatarDto
     */
    void updateAvatar(UpdateAvatarDto updateAvatarDto);

    /**
     * 根据用户id查询用户信息
     * @param userId 用户id
     * @return GetUserInfoDto
     */
    GetUserInfoDto getUserInfo(Long userId);
}
