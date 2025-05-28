package com.chenyuxin.rear.interceptor;

import com.chenyuxin.rear.common.context.UserContext;
import com.chenyuxin.rear.common.util.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Pattern;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("使用了拦截器");

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if(request.getRequestURI().contains("/admin")||request.getRequestURI().contains("/user")){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            return true;
        }

        String token = authHeader.substring(7); // 去掉 "Bearer " 前缀

        try {
            // 解析并验证token，如果无效会抛异常
            JwtUtils.JwtUserInfo userInfo = JwtUtils.getUserInfo(token);
            UserContext.set(userInfo.getUserId(),userInfo.getRole());
        } catch (ExpiredJwtException e) {
            log.warn("Token过期");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            log.warn("非法的token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
