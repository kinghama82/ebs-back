package com.ebs.boardparadice.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//uploads 경로 설정위한 클래스
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // URL에서 "/uploads/파일명"으로 접근 가능
                .addResourceLocations("file:./src/main/resources/static/uploads/"); // 실제 저장 위치
                .addResourceLocations("file:///D:/ebs-back/src/main/resources/static/uploads/"); // 실제 저장 위치
    }
}
