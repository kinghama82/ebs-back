package com.ebs.boardparadice.controller;

import com.ebs.boardparadice.util.CustomJWTException;
import com.ebs.boardparadice.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class APIRefreshController {

    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new CustomJWTException("NULL_REFRESH");
        }
        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_STRING");
        }
        String accessToken = authHeader.substring(7);

        if (!checkExpiredToken(accessToken)) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        log.info("refresh ... claims: " + claims);

        String newAccessToken = JWTUtil.generateToken(claims, 10);
        // refreshToken은 필요에 따라 갱신할 수 있음
        return Map.of("accessToken", newAccessToken, "refreshToken", refreshToken);
    }

    private boolean checkExpiredToken(String token) {
        try {
            JWTUtil.validateToken(token);
        } catch (CustomJWTException ex) {
            return ex.getMessage().equals("Expired");
        }
        return false;
    }
}
