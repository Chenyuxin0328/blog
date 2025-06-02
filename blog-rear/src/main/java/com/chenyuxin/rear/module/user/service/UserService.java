package com.chenyuxin.rear.module.user.service;


import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.module.user.model.dto.GetUserInfoDto;
import com.chenyuxin.rear.module.user.model.dto.RegisterDto;
import com.chenyuxin.rear.module.user.model.dto.UpdateAvatarDto;
import com.chenyuxin.rear.module.user.model.entity.User;
import com.chenyuxin.rear.module.user.model.vo.SelectPageUser;

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

    /**
     * 分页查询用户信息
     * @param search 搜索
     * @param role 角色
     * @param pageNum 第几页
     * @param pageSize 每页展示的记录数
     * @return PageResult<SelectPageUser>
     */
    PageResult<SelectPageUser> selectPageUser(String search, String role, Integer pageNum, Integer pageSize);

    /**
     * 根据id查询用户信息
     * @param userId 用户id
     */
    void deleteUserById(Long userId);

    /**
     * 设置用户管理员权限
     * @param userId 用户id
     * @param role 角色
     */
    void setUserRole(Long userId,String role);

}
