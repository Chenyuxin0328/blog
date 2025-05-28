package com.chenyuxin.rear.module.auth.controller;

import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.common.util.JwtUtils;
import com.chenyuxin.rear.module.auth.model.dto.LoginDto;
import com.chenyuxin.rear.module.user.model.entity.User;
import com.chenyuxin.rear.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDto dto) {
        User user = userService.findByUsername(dto.getUsername());
        if (user == null || !user.getPassword().equals(dto.getPassword())) {
            return Result.failure("用户名或密码错误");
        }

        String token = JwtUtils.generateToken(user.getId(), user.getRole());
        return Result.success(Map.of(
            "token", token,
            "user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "avatar", user.getAvatar(),
                "role", user.getRole()
            )
        ));
    }
}
