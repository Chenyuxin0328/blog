package com.chenyuxin.rear.module.user.controller;

import com.chenyuxin.rear.common.context.UserContext;
import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.common.util.BeanCopyUtil;
import com.chenyuxin.rear.module.user.model.dto.GetUserInfoDto;
import com.chenyuxin.rear.module.user.model.dto.RegisterDto;
import com.chenyuxin.rear.module.user.model.dto.UpdateAvatarDto;
import com.chenyuxin.rear.module.user.model.entity.User;
import com.chenyuxin.rear.module.user.model.vo.SelectByUsernameVo;
import com.chenyuxin.rear.module.user.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/username")
    public Result<SelectByUsernameVo> selectByUserName(@RequestParam("username") String username){
        User user = userService.findByUsername(username);
        return Result.success(BeanCopyUtil.copy(user,SelectByUsernameVo.class));
    }
    @PostMapping("/register")
    public Result<Long> register(@RequestBody RegisterDto registerDto){
        Long userId = userService.register(registerDto);
        return Result.success(userId);
    }
    @PostMapping("/avatar")
    public Result<Void> updateAvatar(@RequestBody UpdateAvatarDto updateAvatarDto){
        userService.updateAvatar(updateAvatarDto);
        return Result.success();
    }
    @GetMapping("/info")
    public Result<GetUserInfoDto> getUserInfo(){
        Long userId = UserContext.getUserId();
        if(userId==null){
            return Result.failure(HttpStatus.UNAUTHORIZED.value(), "用户未登录");
        }
        GetUserInfoDto getUserInfoDto = userService.getUserInfo(userId);
        return Result.success(getUserInfoDto);
    }
}
