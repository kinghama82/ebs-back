package com.ebs.boardparadice.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;



public class JWTUtil {
    //    문자열 생성시 필요한 암호키를 저장
    private static final String key = "1234567890123456789012345678901234567890";

    public static String generateToken(Map<String , Object> valueMap, int minute) {
        SecretKey key;

        try {
//            HMAC 알고리즘을 적용한 KEY 생성
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        // 토큰에 저장할 데이터
        return Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(valueMap)   // 토큰에 저장할 데이터
                .setIssuedAt(Date.from(ZonedDateTime.now().plusMinutes(minute).toInstant()))
                .signWith(key)
                .compact();
    }
    //    토큰 검증을 위한 메소드
    public static Map<String, Object> validateToken(String token) {

        Map<String, Object> claims ;

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes(StandardCharsets.UTF_8));

            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (MalformedJwtException malformedJwtException) {
            throw new CustomJWTException("Malformed");
        }catch (ExpiredJwtException expiredJwtException) {
            throw new CustomJWTException("Expired");
        }catch (InvalidClaimException invalidClaimException) {
            throw new CustomJWTException("Invalid");
        }catch (JwtException jwtException) {
            throw new CustomJWTException("JWTError");
        }catch (Exception e) {
            throw new CustomJWTException("Error");
        }
        return claims;
    }
}




