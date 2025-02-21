package com.ebs.boardparadice.security.filter;

import com.ebs.boardparadice.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // OPTIONS 메서드나 인증/회원가입 관련 경로는 JWT 검증 제외
        if(request.getMethod().equals("OPTIONS")) {
            return true;
        }
        String path = request.getRequestURI();
        log.info("JWTCheckFilter path: " + path);
        if(path.startsWith("/api/auth")) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("JWTCheckFilter processing request...");
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
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
        }
        filterChain.doFilter(request, response);
    }
}
