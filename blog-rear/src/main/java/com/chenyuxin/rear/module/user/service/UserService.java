package com.chenyuxin.rear.module.user.service;


import com.chenyuxin.rear.module.user.model.entity.User;

public interface UserService {
    User findByUsername(String username);
}
