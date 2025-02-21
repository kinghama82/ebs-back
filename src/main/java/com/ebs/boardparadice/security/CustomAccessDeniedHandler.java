package com.ebs.boardparadice.security;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("error", "접근 권한이 없습니다."));


        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        PrintWriter printWriter = response.getWriter();
        printWriter.print(jsonStr);
        printWriter.close();
    }
}
