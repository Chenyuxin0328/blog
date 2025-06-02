package com.chenyuxin.rear.module.user.controller.admin;

import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.module.user.model.vo.SelectPageUser;
import com.chenyuxin.rear.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/admin")
public class UserAdminController {
    @Autowired
    private UserService userService;
    @GetMapping("/page")
    public Result<PageResult<SelectPageUser>> selectPageUser(@RequestParam("search")String search,
                                                             @RequestParam("role")String role,
                                                             @RequestParam("pageNum") Integer pageNum,
                                                             @RequestParam("pageSize") Integer pageSize){
        PageResult<SelectPageUser> pageResult = userService.selectPageUser(search,role,pageNum,pageSize);
        return Result.success(pageResult);
    }
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUserById(@PathVariable Long userId){
        userService.deleteUserById(userId);
        return Result.success();
    }
    // 赋予用户管理员权限
    @GetMapping("/{userId}")
    public Result<Void> setUserRole(@PathVariable Long userId,@RequestParam String role){
        userService.setUserRole(userId,role);
        return Result.success();
    }
}
