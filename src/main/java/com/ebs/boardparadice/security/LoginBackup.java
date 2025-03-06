package com.ebs.boardparadice.security;

import com.ebs.boardparadice.DTO.GamerDTO;
import com.ebs.boardparadice.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class LoginBackup {
}

/*


import com.ebs.boardparadice.DTO.GamerDTO;
import com.ebs.boardparadice.util.JWTUtil;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("--------------------------");
        log.info(authentication);
        log.info("--------------------------");

        GamerDTO gamerDTO = (GamerDTO) authentication.getPrincipal();

        // 먼저 claims 수정: LocalDateTime -> 문자열
        Map<String, Object> claims = gamerDTO.getClaims();
        // 기존: claims.put("createdate", gamerDTO.getCreatedate().toString());
        // 수정:
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        claims.put("createdate", gamerDTO.getCreatedate().format(formatter));

        // 이제 토큰 생성
        String accessToken = JWTUtil.generateToken(claims, 10);
        String refreshToken = JWTUtil.generateToken(claims, 60 * 24);

        // 토큰 정보를 claims에 추가 (원하는 경우)
        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(claims);

        response.setContentType("application/json;charset=utf-8");

        PrintWriter printWriter = response.getWriter();
        printWriter.print(jsonStr);
        printWriter.close();
    }

}
*/

