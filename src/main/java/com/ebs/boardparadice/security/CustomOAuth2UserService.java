package com.ebs.boardparadice.security;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.GamerRole;
import com.ebs.boardparadice.repository.GamerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final GamerRepository gamerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws UsernameNotFoundException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        
        // OAuth 제공자 ID 가져오기 (google, naver 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        log.info("🔍 OAuth2 로그인 요청: {}", registrationId);
        log.info("🔍 OAuth2 응답 데이터: {}", attributes); // 로그 추가
        
        // 제공자별 사용자 정보 매핑
        OAuthUserInfo userInfo = getOAuthUserInfo(registrationId, attributes);

        // 이메일이 NULL인지 체크 
        if (userInfo.getEmail() == null || userInfo.getEmail().trim().isEmpty()) {
            throw new RuntimeException("이메일 정보를 제공하지 않는 OAuth2 계정입니다.");
        }
        log.info("🔍 사용자 이메일: {}", userInfo.getEmail());
        
        // 기존 Gamer 조회 또는 새로 생성
        Optional<Gamer> existingGamer = gamerRepository.findByEmail(userInfo.getEmail());
        Gamer gamer = existingGamer.orElseGet(() -> {
            Gamer newGamer = Gamer.builder()
                    .email(userInfo.getEmail().trim())
                    .nickname(userInfo.getName())
                    .name(userInfo.getName())        //이게 닉네임으로 들어감
                    .password("")
                    .profileImage(userInfo.getProfileImage())
                    .phone(null)
                    .createdate(LocalDateTime.now())
                    .level("newbie")
                    .social(true)
                    .age(0)
                    .build();
            newGamer.addRole(GamerRole.USER);
            return gamerRepository.save(newGamer);
        });
        
     // OAuth2User에 전달할 사용자 정보 Map 생성
        Map<String, Object> attributes2 = Map.of(
                "id", gamer.getId(),
                "name", gamer.getNickname(),
                "email", gamer.getEmail(),
                "profileImage", gamer.getProfileImage(),
                "social", gamer.isSocial()
        );

        // ✅ `CustomUserDetailsService` 방식과 동일한 `GamerDTO` 생성
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes2,
                "email"  // 사용자 식별자 필드
        );
        
    }

/**
 * OAuth2 제공자별로 데이터를 매핑하는 메서드
 */
private OAuthUserInfo getOAuthUserInfo(String registrationId, Map<String, Object> attributes) {
    if ("naver".equals(registrationId)) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return new OAuthUserInfo(
                (String) response.get("email"),
                (String) response.get("nickname"),
                (String) response.get("profile_image")
        );
    } else { // 기본적으로 구글
        return new OAuthUserInfo(
                (String) attributes.get("email"),
                (String) attributes.get("name"),
                (String) attributes.get("picture")
        );
    }
}

/**
 * OAuth 사용자 정보를 담는 DTO 클래스
 */
private static class OAuthUserInfo {
    private final String email;
    private final String name;
    private final String profileImage;

    public OAuthUserInfo(String email, String name, String profileImage) {
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
}
