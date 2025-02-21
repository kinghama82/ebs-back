package com.ebs.boardparadice.util;

import io.jsonwebtoken.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {
    // 실제 서비스에서는 비밀키를 안전하게 관리해야 합니다.
    private static final String SECRET_KEY = "yourSecretKey";
    private static final long EXPIRATION_TIME = 86400000; // 1일

    // JWT 토큰 생성 (예시: 이메일, 닉네임, 소셜 여부, 역할 목록을 클레임에 저장)
    public static String generateToken(String email, String nickname, boolean social, String[] roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("nickname", nickname);
        claims.put("social", social);
        claims.put("roleNames", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // JWT 토큰 검증 후 클레임 반환
    public static Map<String, Object> validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return new HashMap<>(claims);
        } catch (JwtException e) {
            throw new RuntimeException("JWT 검증 실패: " + e.getMessage());
        }
    }
}
