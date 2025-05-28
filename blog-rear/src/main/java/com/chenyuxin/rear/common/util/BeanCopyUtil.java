package com.chenyuxin.rear.common.util;

import org.springframework.beans.BeanUtils;

public class BeanCopyUtil {

    public static <T> T copy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("拷贝失败", e);
        }
    }
}
