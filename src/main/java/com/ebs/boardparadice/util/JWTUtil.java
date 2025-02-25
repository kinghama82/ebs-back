package com.ebs.boardparadice.util;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class JWTUtil {
    //    문자열 생성시 필요한 암호키를 저장
    private static final String key = "1234567890123456789012345678901234567890";

    public static String generateToken(Map<String , Object> valueMap, int minute) {

        SecretKey key = null;

        try {
//            HMAC 알고리즘을 적용한 KEY 생성
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        String jwtstr = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(valueMap)   // 토큰에 저장할 데이터
                .setIssuedAt(Date.from(ZonedDateTime.now().plusMinutes(minute).toInstant()))
                .signWith(key)
                .compact();
        return jwtstr;
    }
    //    토큰 검증을 위한 메소드
    public static Map<String, Object> validateToken(String token) {

        Map<String, Object> claims = null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

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





/*

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
*/
