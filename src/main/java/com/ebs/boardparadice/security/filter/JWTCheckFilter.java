package com.ebs.boardparadice.security.filter;

import com.ebs.boardparadice.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.info("체크urlJWTCheckFilter path: " + path);

        // OPTIONS 메서드와 특정 경로에 대해서는 필터를 적용하지 않음
        return request.getMethod().equals("OPTIONS")
                || path.startsWith("/api/auth")
                || path.equals("/api/gamer/login")
                || path.equals("/api/gamer/new"); // 정확하게 new 요청만 필터링 제외
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("---------JWTCheckFilter processing request...");

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Map<String, Object> claims = JWTUtil.validateToken(token);

                String email = (String) claims.get("email");
                List<String> roleNames = (List<String>) claims.get("roleNames");

                List<SimpleGrantedAuthority> authorities = roleNames.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (RuntimeException e) {
                log.error("JWT 검증 실패: " + e.getMessage());
                response.setContentType("application/json");
                PrintWriter writer = response.getWriter();
                writer.write(new Gson().toJson(Map.of("error", "JWT 검증 실패")));
                writer.flush();
                return;
            }
        } else {
            // JWT가 없거나 인증되지 않은 경우, 익명 사용자로 설정
            SecurityContextHolder.getContext().setAuthentication(
                    new AnonymousAuthenticationToken(
                            "anonymous", "anonymousUser",
                            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))
            );
        }

        filterChain.doFilter(request, response);
    }
}
