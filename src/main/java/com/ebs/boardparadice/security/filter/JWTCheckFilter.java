package com.ebs.boardparadice.security.filter;

import com.ebs.boardparadice.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 인증이 필요 없는 경로(예: 회원가입, 로그인, public API 등)는 필터 제외
        String path = request.getRequestURI();
        if(request.getMethod().equals("OPTIONS") || path.startsWith("/api/member")
                || path.startsWith("/api/products/view")
                || path.startsWith("/api/games")
                || path.startsWith("/games")
        ) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = authHeader.substring(7); // "Bearer " 이후의 토큰 추출
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);



            String email = (String) claims.get("email");
            @SuppressWarnings("unchecked")
            List<String> roleNames = (List<String>) claims.get("roleNames");

            List<SimpleGrantedAuthority> authorities = roleNames.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } catch (Exception e) {

            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.print(new Gson().toJson(Map.of("error", "JWT 검증 실패")));
            writer.close();
        }
    }
}
