package com.chenyuxin.rear.module.auth.controller;

import com.chenyuxin.rear.common.exception.BusinessException;
import com.chenyuxin.rear.common.exception.ErrorCode;
import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.common.util.JwtUtils;
import com.chenyuxin.rear.module.auth.model.dto.LoginDto;
import com.chenyuxin.rear.module.user.model.entity.User;
import com.chenyuxin.rear.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody LoginDto dto) {
        User user = userService.findByUsername(dto.getUsername());
        if (user == null || !user.getPassword().equals(dto.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAIL);
        }
        String token = JwtUtils.generateToken(user.getId(), user.getRole());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization","Bearer "+ token);
        return new ResponseEntity<>(Result.success(Map.of(
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "avatar", user.getAvatar(),
                        "role", user.getRole()
                )
        )),httpHeaders,HttpStatus.OK);
    }
}
