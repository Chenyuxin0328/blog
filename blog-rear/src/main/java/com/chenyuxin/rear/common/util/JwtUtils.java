package com.chenyuxin.rear.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {

    private static final String SECRET_KEY = "xakdhakjjdjdsajkdjkljdlascnosacjaiocaiocicjqowcoasicaosicqwocowcnxoqcncascnasdojodoijqodoasjciasjxqowjxqoixinqsocnqwhiodxqwoxcnqwxqwjoixjqwjdxcqwoicxqxqosixjiqos";

    public static String generateToken(Long userId, String role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12)) // 12小时
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 新增的复合返回类
    public static class JwtUserInfo {
        private Long userId;
        private String role;

        public JwtUserInfo(Long userId, String role) {
            this.userId = userId;
            this.role = role;
        }

        public Long getUserId() {
            return userId;
        }

        public String getRole() {
            return role;
        }
    }

    // 新方法：一次解析，返回 userId 和 role
    public static JwtUserInfo getUserInfo(String token) {
        Claims claims = parseClaims(token);
        Long userId = ((Number) claims.get("userId")).longValue();
        String role = (String) claims.get("role");
        return new JwtUserInfo(userId, role);
    }

    private static Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}
