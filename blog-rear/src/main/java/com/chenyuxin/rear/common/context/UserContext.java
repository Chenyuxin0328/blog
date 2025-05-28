package com.chenyuxin.rear.common.context;

import java.util.Objects;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    public static void set(Long userId, String role) {
        USER_ID.set(userId);
        ROLE.set(role);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static String getRole() {
        return ROLE.get();
    }
    public static Integer getViewScope(){
        String role = ROLE.get();
        if(Objects.equals(role, "user")){
            return 2;
        }else if(Objects.equals(role, "admin")){
            return 3;
        }else{
            return 1;
        }
    }

    public static void clear() {
        USER_ID.remove();
        ROLE.remove();
    }
}
