package com.ebs.boardparadice.security;

import com.ebs.boardparadice.DTO.GamerDTO;
import com.ebs.boardparadice.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

        // ✅ JWT 생성
        Map<String, Object> claims = gamerDTO.getClaims();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        claims.put("createdate", gamerDTO.getCreatedate().format(formatter));

        String accessToken = JWTUtil.generateToken(claims, 10);
        String refreshToken = JWTUtil.generateToken(claims, 60 * 24);

        // ✅ `Set-Cookie`로 쿠키 저장 (ResponseCookie 사용)
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(false)  // ✅ 클라이언트에서도 접근 가능
                .secure(false)    // ✅ 개발 환경에서는 false, 배포 환경에서는 true
                .sameSite("None") // ✅ CORS 허용을 위해 None 설정
                .path("/")
                .maxAge(10 * 60)  // ✅ 10분 유효
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(false)
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // ✅ 7일 유효
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // ✅ JSON 응답도 유지 (프론트에서 토큰을 사용할 수 있도록)
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




/*
//        로그인에 성공한 후 Json 형태로 데이터 생성
@Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    log.info("--------------------------");
    log.info(authentication);
    log.info("--------------------------");

    GamerDTO gamerDTO = (GamerDTO) authentication.getPrincipal();

    Map<String, Object> claims = gamerDTO.getClaims();

    String accessToken = JWTUtil.generateToken(claims, 10);
    String refreshToken = JWTUtil.generateToken(claims, 60*24);


//        시간저장오류 해결 테스트1
    claims.put("createdate", gamerDTO.getCreatedate().toString());

    claims.put("accessToken", accessToken);
    claims.put("refreshToken", refreshToken);

    Gson gson = new Gson();

    String jsonStr = gson.toJson(claims);

    response.setContentType("application/json;charset=utf-8");

    PrintWriter printWriter = response.getWriter();
    printWriter.print(jsonStr);
    printWriter.close();
}*/
