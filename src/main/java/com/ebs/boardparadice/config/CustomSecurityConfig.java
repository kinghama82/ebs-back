package com.ebs.boardparadice.config;

import com.ebs.boardparadice.security.APILoginFailHandler;
import com.ebs.boardparadice.security.APILoginSuccessHandler;
import com.ebs.boardparadice.security.filter.JWTCheckFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Applying securityFilterChain...");

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(csrf -> csrf.disable());

/*        // 인증이 필요 없는 엔드포인트 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/gamer/**", "/error").permitAll() // 회원가입, 로그인 API는 인증 필요 없음
                .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
        );*/

        // 로그인 엔드포인트 (예: /api/gamer/login)는 JWTCheckFilter 검증 제외 처리
        http.formLogin(form -> {
            form.loginPage("/api/gamer/login");
            form.successHandler(new com.ebs.boardparadice.security.APILoginSuccessHandler());
            form.failureHandler(new APILoginFailHandler());
        });

        // JWTCheckFilter를 UsernamePasswordAuthenticationFilter 전에 실행
        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://43.202.30.85:3000")); // ✅ 특정 오리진 지정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // ✅ 반드시 true 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



    /*@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://43.202.30.85:3000", "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true); // ✅ 반드시 true 설정 // 인증 허용 (JWT, 세션 등)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/
//        configuration.setAllowedOriginPatterns(Arrays.asList("*"));  //로컬환경 테스트시 이걸 주석추가




    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
