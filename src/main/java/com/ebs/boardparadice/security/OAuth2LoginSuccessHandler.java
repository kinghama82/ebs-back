package com.ebs.boardparadice.security;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final GamerRepository gamerRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("✅ OAuth2 로그인 성공: " + authentication.getPrincipal());

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profileImage = oAuth2User.getAttribute("picture");
        log.info("🔍 로그인한 사용자 이메일: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("이메일 정보를 제공하지 않는 OAuth2 계정입니다.");
        }
        
        // 기존 Gamer 조회 또는 새로 생성
        
        
        Optional<Gamer> existingGamer = gamerRepository.findByEmail(email);
        Gamer gamer;
        if(existingGamer.isPresent()) {
        	gamer = existingGamer.get();
        	log.info("✅ 기존 회원 로그인: {}", email);
        }else {
        	gamer = Gamer.builder()
                    .email(email.trim())
                    .name(name)
                    .profileImage(profileImage)
                    .social(true)
                    .build();
        	gamer = gamerRepository.save(gamer);
        	log.info("✅ 신규 회원 가입 완료: {}", email);
        }

     // ✅ JWT에 DB의 `id` 포함
        Map<String, Object> claims = Map.of(
            "id", gamer.getId(),  // ✅ DB의 ID 값 추가
            "email", email,
            "nickname", gamer.getName(),
            "profileImage", gamer.getProfileImage(),
            "social", gamer.isSocial()
        );
        
     // JWT 발급
        String accessToken = JWTUtil.generateToken(claims, 10);
        String refreshToken = JWTUtil.generateToken(claims, 60 * 24);

        // ✅ JSON 응답 대신 바로 프론트엔드 `/auth/callback`으로 리디렉트
        String redirectUrl = "https://www.boardparadice.com/auth/callback?accessToken=" + accessToken + "&refreshToken=" + refreshToken;
        response.sendRedirect(redirectUrl);
    }
}
