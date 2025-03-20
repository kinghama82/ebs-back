package com.ebs.boardparadice.config;

import com.ebs.boardparadice.security.APILoginFailHandler;
import com.ebs.boardparadice.security.APILoginSuccessHandler;
import com.ebs.boardparadice.security.CustomOAuth2UserService;
import com.ebs.boardparadice.security.OAuth2LoginSuccessHandler;
import com.ebs.boardparadice.security.filter.JWTCheckFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CustomSecurityConfig {

	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(csrf -> csrf.disable());



        // 로그인 엔드포인트 (예: /api/gamer/login)는 JWTCheckFilter 검증 제외 처리
        http.formLogin(form -> {
            form.loginPage("/api/gamer/login");
            form.successHandler(new com.ebs.boardparadice.security.APILoginSuccessHandler());
            form.failureHandler(new APILoginFailHandler());
        });
        
        //oauth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
        		.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
        		.successHandler(oAuth2LoginSuccessHandler)
        		);

        // JWTCheckFilter를 UsernamePasswordAuthenticationFilter 전에 실행
        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://43.202.30.85:3000",

                "https://boardparadice.com" // 필요에 따라 추가
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // ✅ 반드시 true 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }






    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
